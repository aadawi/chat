package com.chat.controller;

import com.chat.model.Chat;
import com.chat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Controller
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    Queue<SseEmitter> sseEmitters = new ConcurrentLinkedQueue<>();
    boolean isNotFirstRequest;

    @GetMapping(path = {"/chat","/"})
    public String startChat(Model model) {
        model.addAttribute("chat",new Chat());
        isNotFirstRequest =false;
        return "chat";
    }

    @GetMapping(path = {"/showall"})
    public String showAllChat(Model model) {
        model.addAttribute("chat",new Chat());
        model.addAttribute("showAll",true);
        isNotFirstRequest =false;
        return "chat";
    }

    @PostMapping(path = "/chat")
    public ResponseEntity<?> sendChat(@RequestBody Chat chatRequest) {

        chatRequest.setId(System.nanoTime());
        try {
            if (StringUtils.isEmpty(chatRequest.getUsername())){
                chatRequest.setUsername("Anonymous");
            }
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

    @GetMapping(path = "/clear")
    public String clear(Model model)  {
        chatRepository.deleteAll();
        model.addAttribute("chat",new Chat());
        isNotFirstRequest =false;
        return "chat";
    }

    @GetMapping(path = "/register")
    public SseEmitter register() throws IOException {
        SseEmitter emitter = new SseEmitter();
        sseEmitters.add(emitter);
        return emitter;
    }
}
