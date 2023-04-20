package me.jaketheduque.sql;

import me.jaketheduque.data.LayeredClothes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Date;

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

    public LayeredClothes[] getDailyClothes(Date date) {
        return null;
    }
}
