package edu.demo.game_gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameMessage {
    @JsonProperty("type")
    private MessageType type;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("data")
    private Object data;
    
    @JsonProperty("timestamp")
    private long timestamp;

    public GameMessage() {
        this.timestamp = System.currentTimeMillis();
    }

    public GameMessage(MessageType type, String title, String content) {
        this();
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public GameMessage(MessageType type, String title, String content, Object data) {
        this(type, title, content);
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public enum MessageType {
        CONNECTION,
        SEARCHING,
        MATCH_FOUND,
        GAME_READY,
        GAME_UPDATE,
        GAME_OVER,
        GAME_RESULT
    }
}
