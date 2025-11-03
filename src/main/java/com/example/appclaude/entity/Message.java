package com.example.appclaude.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @Column(name = "\"msgUuid\"", nullable = false)
    private UUID msgUuid;

    @Column(name = "\"head\"", nullable = false)
    private Boolean head;

    @Column(name = "\"timeRq\"", nullable = false)
    private LocalDateTime timeRq;

    public Message() {
    }

    public Message(UUID msgUuid, Boolean head, LocalDateTime timeRq) {
        this.msgUuid = msgUuid;
        this.head = head;
        this.timeRq = timeRq;
    }

    public UUID getMsgUuid() {
        return msgUuid;
    }

    public void setMsgUuid(UUID msgUuid) {
        this.msgUuid = msgUuid;
    }

    public Boolean getHead() {
        return head;
    }

    public void setHead(Boolean head) {
        this.head = head;
    }

    public LocalDateTime getTimeRq() {
        return timeRq;
    }

    public void setTimeRq(LocalDateTime timeRq) {
        this.timeRq = timeRq;
    }
}
