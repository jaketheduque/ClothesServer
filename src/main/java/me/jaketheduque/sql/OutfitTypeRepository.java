package me.jaketheduque.sql;

import me.jaketheduque.data.OutfitType;
import me.jaketheduque.data.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class OutfitTypeRepository {
    private static final Logger log = LoggerFactory.getLogger(OutfitTypeRepository.class);

    @Autowired
    private TypeRepository typeRepository;

    @Value("${spring.datasource.url}")
    private String JDBC_URL;
    @Value("${spring.datasource.username}")
    private String JDBC_USERNAME;
    @Value("${spring.datasource.password}")
    private String JDBC_PASSWORD;

    public List<OutfitType> getAllOutfitTypes() {
        ArrayList<OutfitType> outfitTypes = new ArrayList<>();
        String sql = "SELECT BIN_TO_UUID(outfit_type_uuid) AS outfit_type_uuid FROM outfit_types";

        // Open a connection
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                OutfitType type = getOutfitTypeByUUID(result.getString("outfit_type_uuid"));
                outfitTypes.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return outfitTypes;
    }

    public OutfitType getOutfitTypeByUUID(String uuid) {
        OutfitType outfitType = null;
        String sql = "SELECT * FROM v_type_layers_replacement WHERE outfit_type_uuid=?";

        // Open a connection
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Sets outfit_type_uuid before executing query
            stmt.setString(1, uuid);

            // Get all types
            try (ResultSet result = stmt.executeQuery()) {
                HashMap<Type, Integer> typeLayerMap = new HashMap<>();
                List<Type> bottoms = new ArrayList<>();

                // "Static" variables which do not change from row to row
                UUID outfitTypeUUID = UUID.fromString(uuid);
                String outfitTypeName = null;
                while (result.next()) {
                    outfitTypeName = result.getString("outfit_name");

                    UUID typeUUID = UUID.fromString(result.getString("type_uuid"));
                    Type type = typeRepository.getTypeByUUID(typeUUID);

                    if (result.getBoolean("bottom")) { // If bottom add to list
                        bottoms.add(type);
                    } else {
                        typeLayerMap.put(type, result.getInt("layer"));
                    }
                }

                outfitType = new OutfitType(outfitTypeUUID, outfitTypeName, typeLayerMap, bottoms.toArray(new Type[0]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return outfitType;
    }

    public OutfitType getRandomOutfitType() {
        OutfitType outfitType = null;
        String sql = "SELECT BIN_TO_UUID(outfit_type_uuid) AS uuid FROM `clothes-server`.outfit_types ORDER BY RAND()";

        // Open a connection
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)) {
            String uuid = null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Gets a random outfit type
                try (ResultSet result = stmt.executeQuery()) {
                    result.next();
                    uuid = result.getString("uuid");
                }
            }

            // Sets outfit_type_uuid before executing query
            sql = "SELECT * FROM v_type_layers_replacement WHERE outfit_type_uuid=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, uuid);

                try (ResultSet result = stmt.executeQuery()) {
                    HashMap<Type, Integer> typeLayerMap = new HashMap<>();
                    List<Type> bottoms = new ArrayList<>();

                    // "Static" variables which do not change from row to row
                    UUID outfitTypeUUID = UUID.fromString(uuid);
                    String outfitTypeName = null;

                    while (result.next()) {
                        outfitTypeName = result.getString("outfit_name");

                        UUID typeUUID = UUID.fromString(result.getString("type_uuid"));
                        Type type = typeRepository.getTypeByUUID(typeUUID);

                        if (result.getBoolean("bottom")) { // If bottom add to list
                            bottoms.add(type);
                        } else {
                            typeLayerMap.put(type, result.getInt("layer"));
                        }
                    }

                    outfitType = new OutfitType(outfitTypeUUID, outfitTypeName, typeLayerMap, bottoms.toArray(new Type[0]));
                }
            }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return outfitType;
    }

    public boolean addOutfitType(OutfitType outfitType) {
        UUID outfitTypeUUID = UUID.randomUUID();
        String sql = "INSERT INTO outfit_types VALUES (UUID_TO_BIN(?), ?)"; // Parameters: Outfit type UUID, Outfit type name

        // Open a connection
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)) {
            // Create outfit type
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, outfitTypeUUID.toString());
                stmt.setString(2, outfitType.getName());
                stmt.executeUpdate();
            }

            // Updates query to layers query
            sql = "INSERT INTO outfit_type_layers VALUES (UUID_TO_BIN(UUID()), UUID_TO_BIN(?), UUID_TO_BIN(?), ?, ?)"; // Parameters: Outfit type UUID, Type UUID, layer, bottom

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Add bottom options
                for (Type bottom : outfitType.getBottoms()) {
                    stmt.setString(1, outfitTypeUUID.toString());
                    stmt.setString(2, bottom.getUUID().toString());
                    stmt.setInt(3, 1);
                    stmt.setBoolean(4, true);
                    stmt.addBatch();
                }

                // Add top layers
                for (Map.Entry<Type, Integer> entry : outfitType.getTypeLayerMap().entrySet()) {
                    Type top = entry.getKey();
                    int layer = entry.getValue();

                    stmt.setString(1, outfitTypeUUID.toString());
                    stmt.setString(2, top.getUUID().toString());
                    stmt.setInt(3, layer);
                    stmt.setBoolean(4, false);
                    stmt.addBatch();
                }

                // Execute update
                stmt.executeBatch();

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
