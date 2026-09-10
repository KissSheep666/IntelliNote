package com.example.ainote.model;

/**
 * 一条笔记。就是一个普普通通的 Java 类(叫 POJO / 模型类)。
 * 它有 4 个字段:id、标题、正文、AI 总结。
 * Jackson 会自动把它和 JSON 互转:字段名 <-> JSON 的 key。
 */
public class Note {
    private Long id;         // 笔记 id,自动生成
    private String title;    // 标题
    private String content;  // 正文
    private String aiSummary; // AI 生成的总结(一开始为空,调用 AI 后填上)
    private String createdAt; // 创建时间(数据库自动生成)
    private String updatedAt; // 更新时间(数据库自动更新)

    // 无参构造:Jackson 反序列化(把 JSON 转成对象)时必须要有
        /** 执行new Note() 先获取一个空的java对象
            *  再调用set方法 将json里的值依次赋值到对应字段
        */
    public Note() {
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // ---- getter / setter ----
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
