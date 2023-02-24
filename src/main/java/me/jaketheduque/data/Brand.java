package me.jaketheduque.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@Immutable
@Table(name = "brands")
public class Brand {
    @Id
    private UUID brand_uuid;

    @Column(name="name")
    private String name;

    public Brand(UUID brand_uuid, String name) {
        this.brand_uuid = brand_uuid;
        this.name = name;
    }

    public Brand() {}

    public UUID getUUID() {
        return brand_uuid;
    }

    public void setUUID(UUID brand_id) {
        this.brand_uuid = brand_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "brand_uuid=" + brand_uuid +
                ", name='" + name + '\'' +
                '}';
    }
}
