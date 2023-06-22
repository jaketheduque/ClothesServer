package me.jaketheduque.data;

public class ClothesLayer {
    private Clothes item;
    private int layer;

    public ClothesLayer() {};

    public ClothesLayer(Clothes item, int layer) {
        this.item = item;
        this.layer = layer;
    }

    public Clothes getItem() {
        return item;
    }

    public void setItem(Clothes item) {
        this.item = item;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
