package me.jaketheduque.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.jaketheduque.data.Clothes;
import me.jaketheduque.data.OutfitType;
import me.jaketheduque.data.Type;
import me.jaketheduque.sql.ClothesRepository;
import me.jaketheduque.sql.DailyClothesRepository;
import me.jaketheduque.sql.OutfitTypeRepository;
import me.jaketheduque.sql.TypeRepository;
import me.jaketheduque.util.OutfitGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class OutfitRestController {
    private static final Logger log = LoggerFactory.getLogger(OutfitRestController.class);

    @Autowired
    private OutfitTypeRepository outfitTypeRepository;

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private DailyClothesRepository dailyClothesRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private OutfitGenerator outfitGenerator;

    @PostMapping(path = "/api/addoutfittype",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addOutfitType(@RequestBody String payload) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode node = mapper.readTree(payload);

            String name = node.get("name").asText();

            // Create bottom types array
            ArrayNode bottomOptionsNode = (ArrayNode) node.get("bottom_options");
            Type[] bottoms = new Type[bottomOptionsNode.size()];
            for (int i = 0 ; i < bottomOptionsNode.size() ; i++) {
                bottoms[i] = typeRepository.getTypeByUUID(UUID.fromString(bottomOptionsNode.get(i).asText()));
            }

            // Create top layers type map
            Map<Type, Integer> topLayers = new HashMap<>();
            JsonNode topLayersNode = node.get("top_layers");
            var entries = topLayersNode.fields();
            while (entries.hasNext()) {
                Map.Entry<String, JsonNode> entry = entries.next();

                UUID uuid = UUID.fromString(entry.getKey());
                topLayers.put(typeRepository.getTypeByUUID(uuid), entry.getValue().asInt());
            }

            // Create outfit type and add to database
            OutfitType outfitType = new OutfitType(UUID.randomUUID(), name, topLayers, bottoms);
            outfitTypeRepository.addOutfitType(outfitType);

            log.info("Added new outfit type with {} layers!", outfitType.getLayers());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @GetMapping(path = "/api/getoutfittype")
    public List<OutfitType> getOutfitType(@RequestParam(value = "uuid", required = false) String uuid) {
        // If no id is provided then return all outfit types
        if (uuid == null) {
            List<OutfitType> outfitTypes = outfitTypeRepository.getAllOutfitTypes();

            log.info("All outfit types requested");

            return outfitTypes;
        } else {
            OutfitType outfitType = outfitTypeRepository.getOutfitTypeByUUID(uuid);

            log.info("Outfit type requested: " + uuid);

            List<OutfitType> outfitTypes = new ArrayList<>();
            outfitTypes.add(outfitType);

            return outfitTypes;

        }
    }

    /**
     * This is where a LOT of work can be done to improve clothes choosing algorithm
     *
     * @param payload
     * @return
     */
    @PostMapping(path = "/api/getoutfit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOutfit(@RequestBody String payload) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode node = objectMapper.readTree(payload);
            String selectionMethod = node.get("type").asText();

            List<Pair<Clothes, Integer>> clothes = new ArrayList<>();
            OutfitType outfitType = null;
            String uuid = null;

            log.info("Selection method: {}", selectionMethod);
            switch (selectionMethod) {
                case "random":
                    outfitType = outfitTypeRepository.getRandomOutfitType();
                    clothes = outfitGenerator.randomGenerate(outfitType);
                    break;
                case "item-generate":
                    // Gets the clothing item from the uuid
                    uuid = node.get("clothes_uuid").asText();
                    Clothes item = clothesRepository.getClothesFromID(uuid);

                    // Gets outfit type that has the item type
                    outfitType = outfitTypeRepository.getRandomOutfitTypeWithType(item.getType());

                    clothes = outfitGenerator.itemGenerate(outfitType, item);
                    break;
                case "outfit-type-generate":
                    // Gets the outfit type from the uuid
                    uuid = node.get("outfit_type_uuid").asText();
                    outfitType = outfitTypeRepository.getOutfitTypeByUUID(uuid);

                    clothes = outfitGenerator.randomGenerate(outfitType);
                    break;
                case "color-scheme":
                    outfitType = outfitTypeRepository.getOutfitTypeByUUID(node.get("outfit_type_uuid").asText());
                    clothes = outfitGenerator.colorSchemeGenerate(outfitType, node);
                    break;
            }

            log.info("Outfit Type: {}", outfitType.getName());

            // Creates base nodes
            ObjectNode returnNode = objectMapper.createObjectNode();
            ArrayNode clothesNode = returnNode.putArray("clothes");

            // Adds each clothing item found to clothesNode as a dictionary value with layer as the key
            for (var c : clothes) {
                ObjectNode itemNode = objectMapper.valueToTree(c.getFirst());
                itemNode.set("layer", objectMapper.valueToTree(c.getSecond()));

                clothesNode.add(itemNode);
            }

            // Adds the generated clothes type to return JSON
            returnNode.set("outfit_type", objectMapper.valueToTree(outfitType.getName()));

            log.info("Item names: {}", clothes.stream().map(c -> c.getFirst().getName()).collect(Collectors.joining(", ")));

            return new ResponseEntity<> (returnNode.toString(), HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/api/adddailyoutfit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addDailyOutfit(@RequestBody String payload) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode node = objectMapper.readTree(payload);
            Date date = Date.valueOf(node.get("date").asText());

            var clothes = node.get("layered_uuids").fields();

            var layeredClothes = new ArrayList<Pair<Clothes, Integer>>();
            while (clothes.hasNext()) {
                var item = clothes.next();

                layeredClothes.add(Pair.of(clothesRepository.getClothesFromID(item.getKey()), item.getValue().asInt()));
            }

            dailyClothesRepository.insertClothes(layeredClothes, date);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
