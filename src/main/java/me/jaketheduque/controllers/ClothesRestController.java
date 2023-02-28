package me.jaketheduque.controllers;

import me.jaketheduque.data.Clothes;
import me.jaketheduque.sql.ClothesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ClothesRestController {
    private static final Logger log = LoggerFactory.getLogger(ClothesRestController.class);

    @Autowired
    private ClothesRepository clothesRepository;

    @PostMapping(path = "/api/addclothes",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Clothes> add(@RequestBody Clothes clothes) {
        log.info("Attempting to add clothing item: {}", clothes.toString());

        boolean success = clothesRepository.insertClothes(clothes);

        if (success) {
            log.info("Successfully added to database!");
            return new ResponseEntity<>(clothes, HttpStatus.CREATED);
        } else {
            log.warn("Failed to add to database!");
            return new ResponseEntity<>(clothes, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/api/getclothes")
    public List<Clothes> getClothes(@RequestParam(value = "uuid", required = false) String uuid) {
        // If no id is provided then return all clothes
        if (uuid == null) {
            List<Clothes> clothes = clothesRepository.getAllClothes();

            log.info("All clothes requested");

            return clothes;
        } else {
            Clothes item = clothesRepository.getClothesFromID(uuid);

            log.info("Clothing item requested: " + item);

            List<Clothes> clothes = new ArrayList<>();
            clothes.add(item);

            return clothes;
        }
    }
}
