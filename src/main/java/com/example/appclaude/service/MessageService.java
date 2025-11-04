package com.example.appclaude.service;

import com.example.appclaude.dto.MessageRequestDto;
import com.example.appclaude.entity.Message;
import com.example.appclaude.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final MessageRepository messageRepository;
    private volatile long delayMs;

    public MessageService(MessageRepository messageRepository,
                          @Value("${app.message.delay-ms:1000}") long defaultDelayMs) {
        this.messageRepository = messageRepository;
        this.delayMs = Math.max(0, defaultDelayMs);
    }

    @Async
    public void saveMessage(MessageRequestDto requestDto) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted during sleep", e);
            return;
        }

        UUID msgUuid = UUID.fromString(requestDto.getMsgUuid());
        LocalDateTime timeRq = LocalDateTime.now();

        Message message = new Message(msgUuid, requestDto.getHead(), timeRq);
        messageRepository.save(message);

        logger.info("{} – [Write to DB] {{ \"msgUuid\": \"{}\", \"head\": {}, \"timeRq\": \"{}\" }}",
                timeRq.format(formatter),
                msgUuid,
                requestDto.getHead(),
                timeRq.format(formatter));
    }

    public long getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(long newDelayMs) {
        long sanitized = Math.max(0, newDelayMs);
        long old = this.delayMs;
        this.delayMs = sanitized;
        logger.info("Delay updated: {} ms -> {} ms", old, sanitized);
    }
}
