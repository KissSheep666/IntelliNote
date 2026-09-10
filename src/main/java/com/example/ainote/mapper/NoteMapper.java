package com.example.ainote.mapper;

import com.example.ainote.model.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 操作 note 表的 Mapper。
 * 用注解写 SQL，每个方法对应一条 SQL 语句。
 *
 * 苍穹外卖里你可能用的是 XML 写 SQL，这里用注解更简单直观：
 * 方法上面写 @Select / @Insert / @Update / @Delete，里面直接写 SQL。
 */
@Mapper   // 告诉 MyBatis：这是个 Mapper，帮我生成实现类
public interface NoteMapper {

    /**
     * 插入一条笔记，并把自动生成的 id 回填到 note 对象里。
     * useGeneratedKeys = true  → 告诉 MyBatis 我要拿到数据库自动生成的 id
     * keyProperty = "id"       → 把 id 塞到 note 对象的 id 字段里
     * #{title}  → 取 note 对象的 title 属性值，安全地填到 SQL 里（防 SQL 注入）
     */
    @Insert("INSERT INTO note (title, content) VALUES (#{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Note note);

    /**
     * 查询所有笔记，按 id 倒序（新创建的排前面）。
     * SQL 里的 ai_summary 会自动映射到 Note 的 aiSummary 字段
     * （因为 application.properties 里开了驼峰转换）。
     */
    @Select("SELECT * FROM note ORDER BY id DESC")
    List<Note> findAll();

    /**
     * 按 id 查一条笔记。
     * 如果查不到，MyBatis 自动返回 null。
     */
    @Select("SELECT * FROM note WHERE id = #{id}")
    Note findById(Long id);

    /**
     * 更新整条笔记。
     * #{xxx} 会从传入的 note 对象里取值。
     */
    @Update("UPDATE note SET title=#{title}, content=#{content}, ai_summary=#{aiSummary} WHERE id=#{id}")
    void update(Note note);

    /**
     * 根据id删除笔记
     * @param id
     */
    @Delete("delete from note where id = #{id}")
    void deleteById(Long id);
}
