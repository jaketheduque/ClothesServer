package me.jaketheduque.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.jaketheduque.data.Clothes;
import me.jaketheduque.data.OutfitType;
import me.jaketheduque.data.Type;
import me.jaketheduque.sql.ClothesRepository;
import me.jaketheduque.sql.OutfitTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.*;
import java.util.List;

@RestController
public class OutfitRestController {
    @Autowired
    private OutfitTypeRepository outfitTypeRepository;

    @Autowired
    private ClothesRepository clothesRepository;

    /**
     * This is where a LOT of work can be done to improve clothes choosing algorithm
     *
     * @param payload
     * @return
     */
    @PostMapping(path = "/api/getoutfit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Clothes, Integer>> add(@RequestBody String payload) {
        Map<Clothes, Integer> clothesMap = new HashMap();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode node = objectMapper.readTree(payload);
            OutfitType outfitType = outfitTypeRepository.getOutfitTypeByUUID(node.get("outfit_type_uuid").asText());
            ArrayNode colorsNode = ((ArrayNode) node.get("colors"));

            // Get colors of the scheme
            List<Color> colors = new ArrayList<>();
            for (JsonNode color : colorsNode) {
                int r = color.get("rgb").get("r").asInt();
                int g = color.get("rgb").get("g").asInt();
                int b = color.get("rgb").get("b").asInt();

                colors.add(new Color(r, g, b));
            }

            // Goes through each clothes type and chooses a clothing item based on the color
            for (Type type : outfitType.getTypeLayerMap().keySet()) {
                // Chooses a random color from the scheme
                Collections.shuffle(colors);
                Color color = colors.get(0);

                clothesMap.put(clothesRepository.getClothesFromColorAndType(color, type).get(0), outfitType.getTypeLayerMap().get(type));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<> (clothesMap, HttpStatus.OK);
    }
}
