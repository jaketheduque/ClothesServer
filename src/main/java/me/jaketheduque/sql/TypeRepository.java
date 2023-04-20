package me.jaketheduque.sql;

import me.jaketheduque.data.Type;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TypeRepository extends CrudRepository<Type, UUID> {
    @Query(value = "SELECT * FROM types", nativeQuery = true)
    List<Type> getAllTypes();

    @Query(value = "SELECT * FROM types WHERE type_uuid=?1", nativeQuery = true)
    Type getTypeByUUID(UUID uuid);
}
