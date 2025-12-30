package com.sztu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {
    private String role;
    private String content;
    private List<byte[]> images;

    public MessageDto() {
    }

    public MessageDto(String role, String content, List<byte[]> images) {
        this.role = role;
        this.content = content;
        this.images = images;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<byte[]> getImages() {
        return images;
    }

    public void setImages(List<byte[]> images) {
        this.images = images;
    }
}
