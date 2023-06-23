package me.jaketheduque.controllers;

import me.jaketheduque.data.ClothesLayer;
import me.jaketheduque.data.UUIDLayer;
import me.jaketheduque.sql.ClothesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {
    private static final Logger log = LoggerFactory.getLogger(SocketController.class);

    @Autowired
    private ClothesRepository clothesRepository;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ClothesLayer send(UUIDLayer message) throws Exception {
        log.info("Received UUID layer pair from Websocket: {} for layer {}", message.getUUID(), message.getLayer());
        return new ClothesLayer(clothesRepository.getClothesFromID(message.getUUID()), message.getLayer());
    }
}
