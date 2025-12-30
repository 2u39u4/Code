package com.sztu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("chatbot_chat_details")
public class ChatDetails implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    @TableField("chat_history_id")
    private Long chatHistoryId;
    @TableField("user_chat")
    private String userChat;
    @TableField("ai_chat")
    private String aiChat;
    @TableField("create_time")
    private LocalDateTime createTime;

    public ChatDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatHistoryId() {
        return chatHistoryId;
    }

    public void setChatHistoryId(Long chatHistoryId) {
        this.chatHistoryId = chatHistoryId;
    }

    public String getUserChat() {
        return userChat;
    }

    public void setUserChat(String userChat) {
        this.userChat = userChat;
    }

    public String getAiChat() {
        return aiChat;
    }

    public void setAiChat(String aiChat) {
        this.aiChat = aiChat;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
