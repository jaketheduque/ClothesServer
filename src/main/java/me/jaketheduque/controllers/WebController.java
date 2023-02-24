package me.jaketheduque.controllers;

import me.jaketheduque.sql.BrandRepository;
import me.jaketheduque.sql.PatternRepository;
import me.jaketheduque.sql.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private PatternRepository patternRepository;

    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("brands", brandRepository.getAllBrands());
        model.addAttribute("types",typeRepository.getAllTypes());
        model.addAttribute("patterns",patternRepository.getAllPatterns());
        return "add";
    }
}
