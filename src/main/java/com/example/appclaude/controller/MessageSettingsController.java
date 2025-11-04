package com.example.appclaude.controller;

import com.example.appclaude.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message-delay")
public class MessageSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(MessageSettingsController.class);

    private final MessageService messageService;

    public MessageSettingsController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<Long> getDelay() {
        return ResponseEntity.ok(messageService.getDelayMs());
    }

    @PutMapping
    public ResponseEntity<String> setDelay(@RequestParam("ms") long delayMs) {
        if (delayMs < 0) {
            return ResponseEntity.badRequest().body("delay must be >= 0");
        }
        messageService.setDelayMs(delayMs);
        logger.info("Runtime delay set via API to {} ms", delayMs);
        return ResponseEntity.ok("delay updated to " + delayMs + " ms");
    }
}


