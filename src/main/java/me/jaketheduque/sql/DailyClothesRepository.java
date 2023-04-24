package me.jaketheduque.sql;

import me.jaketheduque.data.Clothes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class DailyClothesRepository {
    private final static Logger log = LoggerFactory.getLogger(DailyClothesRepository.class);

    @Autowired
    private ClothesRepository clothesRepository;

    @Value("${spring.datasource.url}")
    private String JDBC_URL;
    @Value("${spring.datasource.username}")
    private String JDBC_USERNAME;
    @Value("${spring.datasource.password}")
    private String JDBC_PASSWORD;

    public boolean insertClothes(List<Pair<Clothes, Integer>> clothes, Date date) {
        String sql = "INSERT INTO daily_clothes VALUES (UUID_TO_BIN(UUID()), ?, UUID_TO_BIN(?), ?)"; // Date, clothes_id, layer

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareCall(sql)) {

            for (var item : clothes) {
                stmt.setDate(1, date);
                stmt.setString(2, item.getFirst().getUUID().toString());
                stmt.setInt(3, item.getSecond());
                stmt.addBatch();
            }

            stmt.executeBatch();

            log.info("Inserted {} items for {}", clothes.size(), date.toString());

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
