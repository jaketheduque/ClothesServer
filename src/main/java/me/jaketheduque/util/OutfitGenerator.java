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
import java.util.Collections;
import java.util.List;

@Component("outfitGenerator")
public class OutfitGenerator {
    // TODO Replace List<Pair<Clothes, Integer>> with custom object a la List<LayeredClothes>

    private static final Logger log = LoggerFactory.getLogger(OutfitGenerator.class);

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private OutfitTypeRepository outfitTypeRepository;

    /**
     * Fully random generation for an outfit type
     *
     * @param outfitType
     * @return Clothes to layer pairs
     */
    public List<Pair<Clothes, Integer>> randomGenerate(OutfitType outfitType) {
        var types = outfitType.getRandomTypeToLayers();

        // Gets clothes list from list of types and list of colors
        List<Pair<Clothes, Integer>> clothes = new ArrayList<>();
        for (int i = 0 ; i < types.size() ; i++) {
            Clothes item = clothesRepository.getRandomItemFromType(types.get(i).getFirst());
            clothes.add(Pair.of(item, types.get(i).getSecond()));
        }

        return clothes;
    }

    /**
     * Fully random generation for an outfit type also including the given item (outfit type given must have the item type)
     *
     * @param outfitType
     * @param item
     * @return Clothes to layer pairs
     */
    public List<Pair<Clothes, Integer>> itemGenerate(OutfitType outfitType, Clothes item) {
        // Get layer where given clothing item would go
        int itemLayer = outfitType.getTypeLayerMap().get(item.getType());

        // Call completely random generate with outfit type
        var clothes = randomGenerate(outfitType);

        // Remove generated clothes item map with given clothing item at given layer (also checking if the given item is a bottom item)
        for (int i = 0 ; i < clothes.size() ; i++) {
            Pair<Clothes, Integer> pair = clothes.get(i);
            if (pair.getFirst().getType().isBottom() == item.getType().isBottom()) {
                if (pair.getSecond() == itemLayer) {
                    clothes.remove(pair);
                }
            }
        }

        // Adds given item back
        clothes.add(Pair.of(item, itemLayer));

        return clothes;
    }

    /**
     * Generate outfit from outfit type and color scheme (provided in JsonNode)
     *
     * @param outfitType
     * @param node
     * @return Clothes to layer pairs
     */
    public List<Pair<Clothes, Integer>> colorSchemeGenerate(OutfitType outfitType, JsonNode node) {
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

        var types = outfitType.getRandomTypeToLayers();

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
