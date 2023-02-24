package me.jaketheduque.data;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@Immutable
@Table(name = "colors")
public class Color {
    @Id
    private UUID color_uuid;

    @Column(name="name")
    private String name;

    @Column(name="hex")
    private String hex;

    public Color() {
    }

    public Color(UUID color_uuid, String name, String hex) {
        this.color_uuid = color_uuid;
        this.name = name;
        this.hex = hex;
    }

    public UUID getUUID() {
        return color_uuid;
    }

    public void setUUID(UUID color_uuid) {
        this.color_uuid = color_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    @Override
    public String toString() {
        return "Color{" +
                "color_uuid=" + color_uuid +
                ", name='" + name + '\'' +
                ", hex='" + hex + '\'' +
                '}';
    }
}
