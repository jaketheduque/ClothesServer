package me.jaketheduque.data;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.util.HashMap;
import java.util.Set;
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

    public OutfitType(UUID outfit_type_uuid, String name, HashMap<Type, Integer> typeLayerMap) {
        this.outfit_type_uuid = outfit_type_uuid;
        this.name = name;
        this.typeLayerMap = typeLayerMap;
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

    @Override
    public String toString() {
        return "OutfitType{" +
                "outfit_type_uuid=" + outfit_type_uuid +
                ", name='" + name + '\'' +
                ", typeLayerMap=" + typeLayerMap +
                '}';
    }
}
