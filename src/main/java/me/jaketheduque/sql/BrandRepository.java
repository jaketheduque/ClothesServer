package me.jaketheduque.sql;

import me.jaketheduque.data.Brand;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BrandRepository extends CrudRepository<Brand, Long> {
    @Query(value = "SELECT * FROM brands", nativeQuery = true)
    List<Brand> getAllBrands();

    @Query(value = "SELECT * FROM brands WHERE brand_uuid=?1", nativeQuery = true)
    Brand getBrandByUUID(UUID uuid);
}
