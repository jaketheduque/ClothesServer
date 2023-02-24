package me.jaketheduque.sql;

import me.jaketheduque.data.Pattern;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatternRepository extends CrudRepository<Pattern, Long> {
    @Query(value = "SELECT * FROM patterns", nativeQuery = true)
    List<Pattern> getAllPatterns();

    @Query(value = "SELECT * FROM patterns WHERE pattern_uuid=?1", nativeQuery = true)
    Pattern getPatternByUUID(UUID uuid);
}
