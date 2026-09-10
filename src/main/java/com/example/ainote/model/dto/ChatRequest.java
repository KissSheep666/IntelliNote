package com.example.ainote.model.dto;

import java.util.List;

/**
 * DTO
 * 发给大模型的「请求体」结构。
 * Java对象会被 Jackson 转成这样的 JSON 发给 DeepSeek:
 *
 * {
 *   "model": "deepseek-v4-flash",
 *   "messages": [ { "role": "user",
 *                   "content": "..." } ]
 * }
 *
 * DTO = Data Transfer Object,专门用来「传数据」的类。
 */
public class ChatRequest {  //类名

    //成员变量
    private String model;           // 用哪个模型,比如 deepseek-chat
    private List<Message> messages; // 对话内容(这里一般只放一条 user 消息)

    //无参构造器
    public ChatRequest() {
    }

    //get/set方法
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    //静态内部类
    /**
     * 别处也需要复用message类 -> 抽为独立类
     * 仅该类里需要 -> 内部类
     */
    public static class Message {  /** 一条消息:谁说的(role) + 说了什么(content) */
        private String role;     // "user" / "assistant" / "system"
        private String content;  // 具体内容

        public Message() {
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
    }
}
