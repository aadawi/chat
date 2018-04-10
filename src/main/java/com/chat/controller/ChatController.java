package com.chat.controller;

import com.chat.model.Chat;
import com.chat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Controller
public class ChatController {

    @Autowired
    ChatRepository chatRepository;

    Queue<SseEmitter> sseEmitters = new ConcurrentLinkedQueue<>();
    boolean isNotFirstRequest;

    @GetMapping(path = "/chat")
    public String startChat(Model model) {
        model.addAttribute("chat",new Chat());
        isNotFirstRequest =false;
        return "chat";
    }

    @PostMapping(path = "/chat")
    public ResponseEntity<?> sendChat(@RequestBody Chat chatRequest) {

        chatRequest.setId(System.nanoTime());
        try {
            chatRepository.save(chatRequest);
        }catch (Exception e){
            e.printStackTrace();
        }


        if (!isNotFirstRequest){
            chatRepository.findAll().forEach(request -> {
                sseEmitters.forEach(emitter -> {
                    try {
                        emitter.send(request, MediaType.APPLICATION_JSON);
                        emitter.onCompletion(() -> sseEmitters.remove(emitter));
                        emitter.onTimeout(() ->{
                            System.out.println(" ERROR "+emitter.getTimeout());
                        });
                    } catch (Exception e) {
//                e.printStackTrace();
                    }
                });
            });
        } else {
            sseEmitters.forEach(emitter -> {
                try {
                    emitter.send(chatRequest, MediaType.APPLICATION_JSON);
                    emitter.onCompletion(() -> sseEmitters.remove(emitter));
                    emitter.onTimeout(() ->{
                        System.out.println(" ERROR "+emitter.getTimeout());
                    });
                } catch (Exception e) {
//                e.printStackTrace();
                }
            });
        }
        System.out.println(isNotFirstRequest);

        isNotFirstRequest = true;


        return ResponseEntity.ok(chatRequest);
    }

    @GetMapping(path = "/register")
    public SseEmitter register() throws IOException {
        SseEmitter emitter = new SseEmitter(100000l);
        sseEmitters.add(emitter);
        return emitter;
    }
}
