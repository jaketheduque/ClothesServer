package me.jaketheduque.sql;

import me.jaketheduque.data.Clothes;
import me.jaketheduque.data.Color;
import me.jaketheduque.data.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ClothesRepository {
    @Autowired
    private ColorsRepository colorsRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private PatternRepository patternRepository;

    @Value("${spring.datasource.url}")
    private String JDBC_URL;
    @Value("${spring.datasource.username}")
    private String JDBC_USERNAME;
    @Value("${spring.datasource.password}")
    private String JDBC_PASSWORD;

    public List<Clothes> getAllClothes() {
        List<Clothes> clothes = new ArrayList<>();
        String sql = "SELECT * FROM v_clothes_full_replacement";

        // Open a connection
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet result = stmt.executeQuery()) {
            while (result.next()) {

                // Retrieve row and create clothes object
                Color primaryColor = new Color(UUID.fromString(result.getString("color_uuid")), result.getString("color_name"), result.getString("color_hex"));

                // Get lists for secondary color ids, names, and hexes
                Set<Color> secondaryColors = new HashSet<>();
                if (result.getString("secondary_color_uuids") != null) {
                    List<String> secondaryColorIDs = Arrays.stream(result.getString("secondary_color_uuids").split(",")).collect(Collectors.toList());
                    List<String> secondaryColorNames = Arrays.stream(result.getString("secondary_color_names").split(",")).collect(Collectors.toList());
                    List<String> secondaryColorHexes = Arrays.stream(result.getString("secondary_color_hexes").split(",")).collect(Collectors.toList());

                    // Add each color to secondary colors set
                    for (int n = 0; n < secondaryColorIDs.size(); n++) {
                        secondaryColors.add(new Color(UUID.fromString(secondaryColorIDs.get(n)), secondaryColorNames.get(n), secondaryColorHexes.get(n)));
                    }
                }

                clothes.add(new Clothes(UUID.fromString(result.getString("clothes_uuid")),
                        result.getString("name"),
                        primaryColor,
                        secondaryColors,
                        typeRepository.getTypeByUUID(UUID.fromString(result.getString("type_uuid"))),
                        patternRepository.getPatternByUUID(UUID.fromString(result.getString("pattern_uuid"))),
                        result.getString("brand_uuid") == null ? null : brandRepository.getBrandByUUID(UUID.fromString(result.getString("brand_uuid")))));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return clothes;
    }

    public Clothes getClothesFromID(String uuid) {
        Clothes item = null;
        String sql = "SELECT * FROM v_clothes_full_replacement WHERE clothes_uuid=UUID_TO_BIN(?)";

        // Open a connection
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Sets clothes_id before executing query
            stmt.setString(1, uuid);
            try (ResultSet result = stmt.executeQuery()) {
                result.next();

                // Retrieve row and create clothes object
                Color primaryColor = new Color(UUID.fromString(result.getString("color_uuid")), result.getString("color_name"), result.getString("color_hex"));

                // Get lists for secondary color ids, names, and hexes
                Set<Color> secondaryColors = new HashSet<>();
                if (result.getString("secondary_color_uuids") != null) {
                    List<String> secondaryColorIDs = Arrays.stream(result.getString("secondary_color_uuids").split(",")).collect(Collectors.toList());
                    List<String> secondaryColorNames = Arrays.stream(result.getString("secondary_color_names").split(",")).collect(Collectors.toList());
                    List<String> secondaryColorHexes = Arrays.stream(result.getString("secondary_color_hexes").split(",")).collect(Collectors.toList());

                    // Add each color to secondary colors set
                    for (int n = 0; n < secondaryColorIDs.size(); n++) {
                        secondaryColors.add(new Color(UUID.fromString(secondaryColorIDs.get(n)), secondaryColorNames.get(n), secondaryColorHexes.get(n)));
                    }
                }

                item = new Clothes(UUID.fromString(result.getString("clothes_uuid")),
                        result.getString("name"),
                        primaryColor,
                        secondaryColors,
                        typeRepository.getTypeByUUID(UUID.fromString(result.getString("type_uuid"))),
                        patternRepository.getPatternByUUID(UUID.fromString(result.getString("pattern_uuid"))),
                        result.getString("brand_uuid") == null ? null : brandRepository.getBrandByUUID(UUID.fromString(result.getString("brand_uuid"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return item;
    }

    public List<Clothes> getClothesFromColorAndType(Color color, Type... types) {
        String typeQuery = "";
        boolean first = false;

        // Adds OR clause for each type
        for (Type type : types) {
            if (first) {
                typeQuery += String.format("type_uuid=UUID_TO_BIN(%s)", type.getUUID().toString());
            } else {
                typeQuery += String.format("OR type_uuid=UUID_TO_BIN(%s)", type.getUUID().toString());
            }
        }

        String sql = "SELECT * FROM Main.v_clothes_full_rgb_replacement WHERE " + typeQuery + " ORDER BY CAST(RGB_DIFFERENCE(color_rgb, '?,?,?') AS FLOAT)";
        List<Clothes> clothes = new ArrayList<>();

        // Set RGB values for query
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
        PreparedStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, java.awt.Color.decode(color.getHex()).getRed());
            stmt.setInt(2, java.awt.Color.decode(color.getHex()).getGreen());
            stmt.setInt(3, java.awt.Color.decode(color.getHex()).getBlue());
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {

                    // Retrieve row and create clothes object
                    Color primaryColor = new Color(UUID.fromString(result.getString("color_uuid")), result.getString("color_name"), result.getString("color_hex"));

                    // Get lists for secondary color ids, names, and hexes
                    Set<Color> secondaryColors = new HashSet<>();
                    if (result.getString("secondary_color_uuids") != null) {
                        List<String> secondaryColorIDs = Arrays.stream(result.getString("secondary_color_uuids").split(",")).collect(Collectors.toList());
                        List<String> secondaryColorNames = Arrays.stream(result.getString("secondary_color_names").split(",")).collect(Collectors.toList());
                        List<String> secondaryColorHexes = Arrays.stream(result.getString("secondary_color_hexes").split(",")).collect(Collectors.toList());

                        // Add each color to secondary colors set
                        for (int n = 0; n < secondaryColorIDs.size(); n++) {
                            secondaryColors.add(new Color(UUID.fromString(secondaryColorIDs.get(n)), secondaryColorNames.get(n), secondaryColorHexes.get(n)));
                        }
                    }

                    clothes.add(new Clothes(UUID.fromString(result.getString("clothes_uuid")),
                            result.getString("name"),
                            primaryColor,
                            secondaryColors,
                            typeRepository.getTypeByUUID(UUID.fromString(result.getString("type_uuid"))),
                            patternRepository.getPatternByUUID(UUID.fromString(result.getString("pattern_uuid"))),
                            result.getString("brand_uuid") == null ? null : brandRepository.getBrandByUUID(UUID.fromString(result.getString("brand_uuid")))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clothes;
    }

    public boolean insertClothes(Clothes... clothes) {
        String sql = "CALL insert_into_clothes(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Open a connection
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareCall(sql)) {
            // For each clothes object
            for (Clothes c : clothes) {
                // Set parameters
                stmt.setString(1, c.getName());
                stmt.setString(2, c.getColor().getName());
                stmt.setString(3, c.getColor().getHex());
                stmt.setString(4, c.getSecondaryColors().stream().map(Color::getName).collect(Collectors.joining(",")));
                stmt.setString(5, c.getSecondaryColors().stream().map(Color::getHex).collect(Collectors.joining(",")));
                stmt.setString(6, c.getType().getName());
                stmt.setBoolean(7, c.getType().isBottom());
                stmt.setBoolean(8, c.getType().isHooded());
                stmt.setBoolean(9, c.getType().isLongSleeve());
                stmt.setBoolean(10, c.getType().isButtoned());
                stmt.setBoolean(11, c.getType().isZippered());
                stmt.setBoolean(12, c.getType().isCollared());
                stmt.setString(13, c.getPattern().getName());
                stmt.setString(14, c.getBrand() == null ? "" : c.getBrand().getName());

                stmt.executeUpdate();

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }

        return false;
    }
}