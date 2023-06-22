package me.jaketheduque.controllers;

import me.jaketheduque.data.Clothes;
import me.jaketheduque.data.ClothesLayer;
import me.jaketheduque.sql.ClothesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ClothesSocketController {
    @Autowired
    private ClothesRepository clothesRepository;

    @MessageMapping("/registeritem")
    @SendTo("/api/socket/listentoitems")
    public ClothesLayer item(Pair<String, Integer> uuid) {
        Clothes item = clothesRepository.getClothesFromID(uuid.getFirst());

        return new ClothesLayer(item, uuid.getSecond());
    }
}
