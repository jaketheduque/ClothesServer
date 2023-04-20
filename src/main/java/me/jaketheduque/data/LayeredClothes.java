package me.jaketheduque.data;

import java.util.Set;
import java.util.UUID;

public class LayeredClothes extends Clothes {
    private int layer;

    public LayeredClothes(UUID clothes_uuid, String name, Color color, Set<Color> secondaryColors, Type type, Pattern pattern, Brand brand, int layer) {
        super(clothes_uuid, name, color, secondaryColors, type, pattern, brand);
        this.layer = layer;
    }

    public LayeredClothes() {
    }
}
