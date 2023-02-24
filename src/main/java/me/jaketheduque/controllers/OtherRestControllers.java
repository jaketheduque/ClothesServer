package me.jaketheduque.controllers;

import me.jaketheduque.data.Brand;
import me.jaketheduque.data.Pattern;
import me.jaketheduque.data.Type;
import me.jaketheduque.sql.BrandRepository;
import me.jaketheduque.sql.PatternRepository;
import me.jaketheduque.sql.TypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OtherRestControllers {
    private static final Logger log = LoggerFactory.getLogger(OtherRestControllers.class);

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private PatternRepository patternRepository;

    @Autowired
    private TypeRepository typeRepository;

    @GetMapping("/api/getbrands")
    public List<Brand> allBrands() {
        List<Brand> brands = brandRepository.getAllBrands();

        log.info("All brands requested");

        return brands;
    }

    @GetMapping("/api/getpatterns")
    public List<Pattern> allPatterns() {
        List<Pattern> patterns = patternRepository.getAllPatterns();

        log.info("All patterns requested");

        return patterns;
    }

    @GetMapping("/api/gettypes")
    public List<Type> allTypes() {
        List<Type> types = typeRepository.getAllTypes();

        log.info("All types requested");

        return types;
    }
}
