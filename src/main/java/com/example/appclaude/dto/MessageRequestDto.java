package com.example.appclaude.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageRequestDto {

    @JsonProperty("msg_uuid")
    private String msgUuid;

    @JsonProperty("head")
    private Boolean head;

    public MessageRequestDto() {
    }

    public MessageRequestDto(String msgUuid, Boolean head) {
        this.msgUuid = msgUuid;
        this.head = head;
    }

    public String getMsgUuid() {
        return msgUuid;
    }

    public void setMsgUuid(String msgUuid) {
        this.msgUuid = msgUuid;
    }

    public Boolean getHead() {
        return head;
    }

    public void setHead(Boolean head) {
        this.head = head;
    }
}
