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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class OutfitRestController {
    private static final Logger log = LoggerFactory.getLogger(OutfitRestController.class);

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
    public ResponseEntity<String> add(@RequestBody String payload) {
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

            // Shuffles colors list
            Collections.shuffle(colors);

            // Gets list of clothes type to filter down multiple top layers to one type per layer
            List<Type> types = new ArrayList<>();
            for (int layer = 0 ; layer < outfitType.getLayers() ; layer++) {
                for (Map.Entry entry : outfitType.getTypeLayerMap().entrySet()) {
                    // Looks for a type with a layer which matches the current layer being searched for
                    if (((Integer) entry.getValue()) == (layer + 1)) {
                        Type t = (Type) entry.getKey();
                        types.add(t);
                    }
                }
            }

            // Selects a random bottom type
            List<Type> bottoms = Arrays.stream(outfitType.getBottoms()).collect(Collectors.toList()); // Needed so that the list can be modified
            Collections.shuffle(bottoms);
            types.add(bottoms.get(0));

            // Gets clothes list from list of types and list of colors
            List<Clothes> clothes = new ArrayList<>();
            for (int i = 0 ; i < types.size() ; i++) {
                Clothes item = clothesRepository.getClothesFromTypeAndColors(types.get(i), colors.toArray(new Color[0])).get(0);
                clothes.add(item);
            }

            // Goes through each clothes type and chooses a clothing item based on the color
            // Also adds the JSON node to array node
            ArrayNode array = objectMapper.createArrayNode();

            // Adds each type in the final types list to the array node to be sent as response
            for (Clothes c : clothes) {
                JsonNode itemNode = objectMapper.valueToTree(c);
                array.add(itemNode);
            }

            log.info("Generated outfit of type '{}' with {} items", outfitType.getName(), outfitType.getLayers());
            log.info("Item names: {}", clothes.stream().map(c -> c.getName()).collect(Collectors.joining(", ")));

            return new ResponseEntity<> (array.toString(), HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
