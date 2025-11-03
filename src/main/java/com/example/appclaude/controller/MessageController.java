package com.example.appclaude.controller;

import com.example.appclaude.dto.MessageRequestDto;
import com.example.appclaude.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/post-message")
    public ResponseEntity<String> postMessage(@RequestBody MessageRequestDto requestDto) {
        LocalDateTime receivedTime = LocalDateTime.now();

        logger.info("{} – [Received HTTP Request] {{ \"msg_uuid\": \"{}\", \"head\": {}, \"method\": \"POST\", \"uri\": \"/post-message\" }}",
                receivedTime.format(formatter),
                requestDto.getMsgUuid(),
                requestDto.getHead());

        messageService.saveMessage(requestDto);

        return ResponseEntity.ok("Message accepted");
    }
}
