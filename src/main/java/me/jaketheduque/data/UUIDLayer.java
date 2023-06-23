package me.jaketheduque.data;

public class UUIDLayer {
    private String uuid;
    private int layer;

    public UUIDLayer() {}

    public UUIDLayer(String uuid, int layer) {
        this.uuid = uuid;
        this.layer = layer;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
