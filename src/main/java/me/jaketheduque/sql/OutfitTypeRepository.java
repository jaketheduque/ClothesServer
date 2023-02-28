package me.jaketheduque.sql;

import me.jaketheduque.data.OutfitType;
import me.jaketheduque.data.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class OutfitTypeRepository {
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
        String sql = "SELECT BIN_TO_UUID(outfit_type_uuid) AS outfit_type_uuid FROM Main.outfit_types";

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
        String sql = "SELECT * FROM Main.v_type_layers_replacement WHERE outfit_type_uuid=?";

        // Open a connection
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Sets clothes_id before executing query
            stmt.setString(1, uuid);
            try (ResultSet result = stmt.executeQuery()) {
                HashMap<Type, Integer> typeLayerMap = new HashMap<>();

                // Manually handle first row to get outfit UUID and name plus first layer
                result.next();
                UUID outfitTypeUUID = UUID.fromString(result.getString("outfit_type_uuid"));
                String name = result.getString("outfit_name");
                UUID typeUUID = UUID.fromString(result.getString("type_uuid"));
                typeLayerMap.put(typeRepository.getTypeByUUID(typeUUID), result.getInt("layer"));

                // Handle remaining rows
                while (result.next()) {
                    typeLayerMap.put(typeRepository.getTypeByUUID(UUID.fromString(result.getString("type_uuid"))), result.getInt("layer"));
                }

                outfitType = new OutfitType(outfitTypeUUID, name, typeLayerMap);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return outfitType;
    }
}
