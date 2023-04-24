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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.sql.Date;
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

    @Autowired
    private DailyClothesRepository dailyClothesRepository;

    @Autowired
    private TypeRepository typeRepository;

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
            int currentLayer = 1;
            List<Pair<Type, Integer>> types = new ArrayList<>();
            while (currentLayer < outfitType.getLayers())
                for (Map.Entry entry : outfitType.getTypeLayerMap().entrySet()) {
                    Type t = (Type) entry.getKey();
                        // Looks for a type with a layer which matches the current layer being searched for
                        if (((Integer) entry.getValue()) == currentLayer) {
                            types.add(Pair.of(t, currentLayer));
                            currentLayer++;
                        }
                }

            // Selects a random bottom type
            List<Type> bottoms = Arrays.stream(outfitType.getBottoms()).collect(Collectors.toList()); // Needed so that the list can be modified
            Collections.shuffle(bottoms);
            types.add(Pair.of(bottoms.get(0), 1));

            // Gets clothes list from list of types and list of colors
            List<Pair<Clothes, Integer>> clothes = new ArrayList<>();
            for (int i = 0 ; i < types.size() ; i++) {
                Clothes item = clothesRepository.getClothesFromTypeAndColors(types.get(i).getFirst(), colors.toArray(new Color[0])).get(0);
                clothes.add(Pair.of(item, types.get(i).getSecond()));
            }

            // Creates base dictionary node for clothes
            ArrayNode clothesNode = objectMapper.createArrayNode();

            // Adds each clothing item found to clothesNode as a dictionary value with layer as the key
            for (var c : clothes) {
                ObjectNode itemNode = objectMapper.valueToTree(c.getFirst());
                itemNode.set("layer", objectMapper.valueToTree(c.getSecond()));

                clothesNode.add(itemNode);
            }

            log.info("Generated outfit of type '{}' with {} items", outfitType.getName(), outfitType.getLayers());
            log.info("Item names: {}", clothes.stream().map(c -> c.getFirst().getName()).collect(Collectors.joining(", ")));

            return new ResponseEntity<> (clothesNode.toString(), HttpStatus.OK);
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
