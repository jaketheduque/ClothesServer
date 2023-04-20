package me.jaketheduque.data;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.util.Set;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "clothes")
public class Clothes {
    @Id
    private UUID clothes_uuid;

    @Column(name="name")
    private String name;

    @ManyToOne
    private Color color;

    @ManyToMany
    private Set<Color> secondaryColors;

    @ManyToOne
    private Type type;

    @ManyToOne
    private Pattern pattern;

    @ManyToOne
    private Brand brand;

    public Clothes(UUID clothes_uuid, String name, Color color, Set<Color> secondaryColors, Type type, Pattern pattern, Brand brand) {
        this.clothes_uuid = clothes_uuid;
        this.name = name;
        this.color = color;
        this.secondaryColors = secondaryColors;
        this.type = type;
        this.pattern = pattern;
        this.brand = brand;
    }

    public Clothes() {
    }

    public UUID getUUID() {
        return clothes_uuid;
    }

    public void setUUID(UUID clothes_uuid) {
        this.clothes_uuid = clothes_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Set<Color> getSecondaryColors() {
        return secondaryColors;
    }

    public void setSecondaryColors(Set<Color> secondaryColors) {
        this.secondaryColors = secondaryColors;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public LayeredClothes getLayeredClothes(int layer) {
        return new LayeredClothes(clothes_uuid, name, color, secondaryColors, type, pattern, brand, layer);
    }

    @Override
    public String toString() {
        return "Clothes{" +
                "clothes_uuid=" + clothes_uuid +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", secondaryColors=" + secondaryColors +
                ", type=" + type +
                ", pattern=" + pattern +
                ", brand=" + brand +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return this.clothes_uuid.equals(((Clothes) obj).clothes_uuid);
    }
}
