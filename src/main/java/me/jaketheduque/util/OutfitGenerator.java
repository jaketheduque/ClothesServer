package me.jaketheduque.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.jaketheduque.data.Clothes;
import me.jaketheduque.data.OutfitType;
import me.jaketheduque.data.Type;
import me.jaketheduque.sql.ClothesRepository;
import me.jaketheduque.sql.OutfitTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("outfitGenerator")
public class OutfitGenerator {
    private static final Logger log = LoggerFactory.getLogger(OutfitGenerator.class);

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private OutfitTypeRepository outfitTypeRepository;

    public List<Pair<Clothes, Integer>> randomGenerate(JsonNode node) {
        OutfitType outfitType = outfitTypeRepository.getRandomOutfitType();

        // Quick testing android IDE pt. 2
        // TODO Finish this method up
        return null;
    }

    public List<Pair<Clothes, Integer>> colorSchemeGenerate(JsonNode node) {
        ArrayNode colorsNode = ((ArrayNode) node.get("colors"));
        OutfitType outfitType = outfitTypeRepository.getOutfitTypeByUUID(node.get("outfit_type_uuid").asText());

        // Get colors of the scheme
        java.util.List<Color> colors = new ArrayList<>();
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
        java.util.List<Pair<Type, Integer>> types = new ArrayList<>();
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
        java.util.List<Type> bottoms = Arrays.stream(outfitType.getBottoms()).collect(Collectors.toList()); // Needed so that the list can be modified
        Collections.shuffle(bottoms);
        types.add(Pair.of(bottoms.get(0), 1));

        // Gets clothes list from list of types and list of colors
        List<Pair<Clothes, Integer>> clothes = new ArrayList<>();
        for (int i = 0 ; i < types.size() ; i++) {
            Clothes item = clothesRepository.getClothesFromTypeAndColors(types.get(i).getFirst(), colors.toArray(new Color[0])).get(0);
            clothes.add(Pair.of(item, types.get(i).getSecond()));
        }

        log.info("Generated color-scheme outfit of type '{}' with {} items", outfitType.getName(), outfitType.getLayers());

        return clothes;
    }
}
