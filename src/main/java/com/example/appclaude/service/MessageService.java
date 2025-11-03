package com.example.appclaude.service;

import com.example.appclaude.dto.MessageRequestDto;
import com.example.appclaude.entity.Message;
import com.example.appclaude.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Async
    public void saveMessage(MessageRequestDto requestDto) {
        try {
            Thread.sleep(1000);
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
}
