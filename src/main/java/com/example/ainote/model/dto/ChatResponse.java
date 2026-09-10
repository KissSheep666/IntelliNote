package com.example.ainote.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * 大模型返回的「响应体」结构。
 * DeepSeek 返回的 JSON 大致长这样:
 *
 * {
 *   "id": "chatcmpl-xxx", "object": "chat.completion", "created": 123,
 *   "choices": [
 *     {
 *     "message": { "content": "这是 AI 的回答" }
 *     }
 *   ]
 * }
 *
 * 它其实还返回 id / object / created / usage 等一堆字段,但我们只关心 choices。
 * 下面这个注解就是告诉 Jackson:「遇到没声明的字段,忽略掉,别报错」。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatResponse {

    //成员变量
    private List<Choice> choices;

    //无参构造器
    public ChatResponse() {
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }


    // 静态内部类1
    /** 一个候选答案(还带 index、finish_reason 等字段,同样忽略) */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private Message message;

        public Choice() {
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    // 静态内部类2
    /** AI 返回的一条消息(其实还有 role 字段,我们只取 content,同样忽略) */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String content;

        public Message() {
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
