package me.jaketheduque.controllers;

import me.jaketheduque.data.Color;
import me.jaketheduque.sql.ColorsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ColorsRestController {
    private static final Logger log = LoggerFactory.getLogger(ColorsRestController.class);

    @Autowired
    private ColorsRepository colorsRepository;

    @GetMapping("/api/getcolors")
    public List<Color> color(@RequestParam(value = "id", required = false) Long id) {
        // If no id is provided then return all clothes
        if (id == null) {
            List<Color> colors = colorsRepository.getAllColors();

            log.info("All colors requested");

            return colors;
        } else {
            Color color = colorsRepository.getColorFromID(id);

            log.info("Color requested: " + color);

            List<Color> colors = new ArrayList<>();
            colors.add(color);

            return colors;
        }
    }
}
