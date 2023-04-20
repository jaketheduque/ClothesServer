package me.jaketheduque.sql;

import me.jaketheduque.data.Color;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ColorsRepository extends CrudRepository<Color, UUID> {
    @Query(value = "SELECT * FROM colors", nativeQuery = true)
    List<Color> getAllColors();

    @Query(value = "SELECT * FROM colors WHERE color_id=?1", nativeQuery = true)
    Color getColorFromID(long id);
}