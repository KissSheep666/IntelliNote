package com.example.ainote.service;

import com.example.ainote.mapper.NoteMapper;
import com.example.ainote.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 负责「笔记」的业务逻辑。
 * 以前用内存 List 存数据，现在换成 MySQL。
 *
 * 对比一下变化：
 *   以前: notes.add(note)      → 现在: noteMapper.insert(note)
 *   以前: notes.stream().filter(...)  → 现在: noteMapper.findById(id)
 *   以前: return notes          → 现在: noteMapper.findAll()
 *
 * Service 层只管业务逻辑，具体的 SQL 交给 Mapper。
 */
@Service
public class NoteService {

    private final NoteMapper noteMapper;

    // 构造器注入:Spring 自动把 NoteMapper 的实例传进来
    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    /** 新建一条笔记，存到数据库 */
    // insert into note (title content) values (#{title}, #{content})
    public Note create(Note note) {
        noteMapper.insert(note);    // 插入后，MyBatis 会自动把生成的 id 回填到 note 对象
        return note;
    }

    /** 列出所有笔记（从数据库查） */
    // select * from note order by id desc
    public List<Note> list() {
        return noteMapper.findAll();
    }

    /** 按 id 找一条笔记，找不到返回 null */
    // select * from note where id = #{id}
    public Note get(Long id) {
        return noteMapper.findById(id);
    }

    /** 更新笔记（比如 AI 总结写回来） */
    // update note set title = #{title} , content = #{content}, ai_summary = #{aiSummary} where id = #{id}
    public void update(Note note) {
        noteMapper.update(note);
    }

    /** 根据id删除笔记 */
    // delete from note where id = #{id}
    public void deleteNote(Long id) {
        noteMapper.deleteById(id);
    }
}
