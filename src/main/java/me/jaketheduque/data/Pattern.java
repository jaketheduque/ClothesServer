package me.jaketheduque.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@Immutable
@Table(name = "patterns")
public class Pattern {
    @Id
    private UUID pattern_uuid;

    @Column(name="name")
    private String name;

    public Pattern() {}

    public Pattern(UUID pattern_uuid, String name) {
        this.pattern_uuid = pattern_uuid;
        this.name = name;
    }

    public UUID getUUID() {
        return pattern_uuid;
    }

    public void setUUID(UUID pattern_uuid) {
        this.pattern_uuid = pattern_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "pattern_uuid=" + pattern_uuid +
                ", name='" + name + '\'' +
                '}';
    }
}
