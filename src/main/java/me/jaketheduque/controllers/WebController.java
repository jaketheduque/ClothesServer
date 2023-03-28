package me.jaketheduque.controllers;

import me.jaketheduque.data.Type;
import me.jaketheduque.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("brands", brandRepository.getAllBrands());
        model.addAttribute("types",typeRepository.getAllTypes());
        model.addAttribute("patterns",patternRepository.getAllPatterns());
        return "add";
    }

    @RequestMapping("/view")
    public String view(Model model) {
        model.addAttribute("clothes", clothesRepository.getAllClothes());
        return "view";
    }

    @RequestMapping("/outfit")
    public String outfit(Model model) {
        model.addAttribute("outfit_types", outfitTypeRepository.getAllOutfitTypes());
        return "outfit";
    }

    @RequestMapping("/addtype")
    public String addType(Model model) {
        model.addAttribute("bottoms", typeRepository.getAllTypes().stream().filter(Type::isBottom).toArray());
        model.addAttribute("tops", typeRepository.getAllTypes().stream().filter(t -> t.isBottom() == false).toArray());
        return "outfittypecreator";
    }
}
