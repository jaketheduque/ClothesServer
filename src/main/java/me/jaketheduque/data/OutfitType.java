package me.jaketheduque.data;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "outfit_types")
public class OutfitType {
    @Id
    private UUID outfit_type_uuid;

    @Column(name="name")
    private String name;

    @ManyToOne(targetEntity = Type.class)
    private HashMap<Type, Integer> typeLayerMap;

    @ManyToOne
    private Type[] bottoms;

    @Column(name="layers")
    private int layers;

    public OutfitType(UUID outfit_type_uuid, String name, HashMap<Type, Integer> typeLayerMap, Type[] bottoms) {
        this.outfit_type_uuid = outfit_type_uuid;
        this.name = name;
        this.typeLayerMap = typeLayerMap;
        this.bottoms = bottoms;

        // Gets total layers number from typeLayerMap adding one to include the bottom
        int layers = Collections.max(typeLayerMap.values()) + 1;

        this.layers = layers;
    }

    public OutfitType() {}

    public UUID getUUID() {
        return outfit_type_uuid;
    }

    public void setUUID(UUID outfit_type_uuid) {
        this.outfit_type_uuid = outfit_type_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Type, Integer> getTypeLayerMap() {
        return typeLayerMap;
    }

    public void setTypeLayerMap(HashMap<Type, Integer> typeLayerMap) {
        this.typeLayerMap = typeLayerMap;
    }

    public Type[] getBottoms() {
        return bottoms;
    }

    public void setBottoms(Type[] bottoms) {
        this.bottoms = bottoms;
    }

    public int getLayers() {
        return layers;
    }

    public void setLayers(int layers) {
        this.layers = layers;
    }

    @Override
    public String toString() {
        return "OutfitType{" +
                "outfit_type_uuid=" + outfit_type_uuid +
                ", name='" + name + '\'' +
                ", typeLayerMap=" + typeLayerMap +
                ", bottoms=" + Arrays.toString(bottoms) +
                ", layers=" + layers +
                '}';
    }
}
