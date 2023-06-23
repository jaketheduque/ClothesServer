package me.jaketheduque.controllers;

import me.jaketheduque.data.Clothes;
import me.jaketheduque.data.Type;
import me.jaketheduque.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class WebController {
    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private PatternRepository patternRepository;

    @Autowired
    private OutfitTypeRepository outfitTypeRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/socket")
    public String socket() {
        return "socket";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("brands", brandRepository.getAllBrands());
        model.addAttribute("types",typeRepository.getAllTypes());
        model.addAttribute("patterns",patternRepository.getAllPatterns());
        return "add";
    }

    @GetMapping("/view")
    public String view(Model model) {
        // Convert to java.util.Date for Thymeleaf
        List<Clothes> clothes = new ArrayList<>();
        for (Clothes c : clothesRepository.getAllClothes()) {
            c.setLastDateWorn(c.getLastDateWorn() == null ? null : new Date(c.getLastDateWorn().getTime())); // Sets date as null if previous date was also null to prevent NullPointerException
            clothes.add(c);
        }

        model.addAttribute("clothes", clothes);
        return "view";
    }

    @GetMapping("/outfit")
    public String outfit(Model model) {
        model.addAttribute("outfit_types", outfitTypeRepository.getAllOutfitTypes());
        return "outfit";
    }

    @GetMapping("/addtype")
    public String addType(Model model) {
        model.addAttribute("bottoms", typeRepository.getAllTypes().stream().filter(Type::isBottom).toArray());
        model.addAttribute("tops", typeRepository.getAllTypes().stream().filter(t -> t.isBottom() == false).toArray());
        return "outfittypecreator";
    }
}
