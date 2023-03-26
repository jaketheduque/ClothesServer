package me.jaketheduque.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@Immutable
@Table(name = "types")
public class Type {
    @Id
    private UUID type_uuid;

    @Column(name="name")
    private String name;

    @Column(name="bottom")
    private boolean bottom;

    @Column(name="hooded")
    private boolean hooded;

    @Column(name="long_sleeve")
    private boolean longSleeve;

    @Column(name="buttoned")
    private boolean buttoned;

    @Column(name="zippered")
    private boolean zippered;

    @Column(name="collared")
    private boolean collared;

    public Type() {}

    public Type(UUID type_uuid, String name, boolean bottom, boolean hooded, boolean longSleeve, boolean buttoned, boolean zippered, boolean collared) {
        this.type_uuid = type_uuid;
        this.name = name;
        this.bottom = bottom;
        this.hooded = hooded;
        this.longSleeve = longSleeve;
        this.buttoned = buttoned;
        this.zippered = zippered;
        this.collared = collared;
    }

    public Type(UUID type_uuid, String name) {
        this.type_uuid = type_uuid;
        this.name = name;
    }

    public UUID getUUID() {
        return type_uuid;
    }

    public void setUUID(UUID type_uuid) {
        this.type_uuid = type_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBottom() {
        return bottom;
    }

    public void setBottom(boolean bottom) {
        this.bottom = bottom;
    }

    public boolean isHooded() {
        return hooded;
    }

    public void setHooded(boolean hooded) {
        this.hooded = hooded;
    }

    public boolean isLongSleeve() {
        return longSleeve;
    }

    public void setLongSleeve(boolean longSleeve) {
        this.longSleeve = longSleeve;
    }

    public boolean isButtoned() {
        return buttoned;
    }

    public void setButtoned(boolean buttoned) {
        this.buttoned = buttoned;
    }

    public boolean isZippered() {
        return zippered;
    }

    public void setZippered(boolean zippered) {
        this.zippered = zippered;
    }

    public boolean isCollared() {
        return collared;
    }

    public void setCollared(boolean collared) {
        this.collared = collared;
    }

    @Override
    /**
     * Returns the Type in JSON format
     */
    public String toString() {
        return "Type{" +
                "type_uuid=" + type_uuid +
                ", name='" + name + '\'' +
                ", hooded=" + hooded +
                ", longSleeve=" + longSleeve +
                ", buttoned=" + buttoned +
                ", zippered=" + zippered +
                '}';
    }
}
