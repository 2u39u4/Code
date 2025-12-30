package com.sztu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;


@TableName("chatbot_question_bank")
public class QuestionBank implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    private String name;
    private String questionCommon;
    private String questionKnowledge;

    private String description;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    public QuestionBank() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestionCommon() {
        return questionCommon;
    }

    public void setQuestionCommon(String questionCommon) {
        this.questionCommon = questionCommon;
    }

    public String getQuestionKnowledge() {
        return questionKnowledge;
    }

    public void setQuestionKnowledge(String questionKnowledge) {
        this.questionKnowledge = questionKnowledge;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
