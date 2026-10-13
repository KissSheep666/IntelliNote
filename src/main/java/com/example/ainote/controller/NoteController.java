package com.example.ainote.controller;

import com.example.ainote.model.Note;
import com.example.ainote.service.AiService;
import com.example.ainote.service.NoteService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 负责「笔记」相关的 HTTP 接口。
 *
 * 分层:Controller(收请求、返回结果) -> Service(干业务逻辑)。
 * Controller 自己不干重活,都是转交给 Service。
 */
@RestController //接口控制器 接收浏览器发送的http请求 并将返回值转为json
@RequestMapping("/api/notes")   //统一所有接口的路径前缀
public class NoteController {

    /**
     * final限制的是变量引用不能变，不是对象内部内容不能改。
     * private final List<String> list = new ArrayList<>();
     * list.add("abc");   可以✅
     * list = new ArrayList<>();   不可以❌
     */
    private final NoteService noteService;  //final:只能赋值一次 且该成员变量需在构造器内赋值(见下)
    private final AiService aiService;

    // 构造器注入:Spring 会自动把 NoteService、AiService 的实例传进来
    public NoteController(NoteService noteService, AiService aiService) {
        this.noteService = noteService; //在构造器内赋值
        this.aiService = aiService;
    }

    /** 创建一条笔记:POST /api/notes  body 里放 {"title":"...","content":"..."} */
    @PostMapping
    public Note create(@RequestBody Note note) {
        return noteService.create(note);
    }

    /** 列出所有笔记:GET /api/notes */
    @GetMapping
    public List<Note> list() {
        return noteService.list();
    }

    /** 对某条笔记做 AI 总结:POST /api/notes/{id}/summarize */
    @PostMapping("/{id}/summarize")
    public Note summarize(@PathVariable Long id) throws Exception {
        Note note = noteService.get(id);    //通过id查找笔记
        if (note == null) {
            throw new RuntimeException("笔记不存在:id=" + id);
        }
        note.setAiSummary(aiService.summarize(note.getContent()));
        noteService.update(note);           // 保存到数据库
        return note;
    }

    /** 对某笔记的内容生成AI标题: POST /api/notes/{id}/generateTitle */
    @PostMapping("/{id}/generate-title")
    public Note generateTitle(@PathVariable Long id) throws IOException, InterruptedException {     //返回Note:以便前端获取更新后的完整数据
        Note note = noteService.get(id);
        if(note == null){
            throw new RuntimeException("笔记" + id + "不存在");
        }
        String content = note.getContent();

        String generateTitle = aiService.generateTitle(content);
        note.setTitle(generateTitle);
        noteService.update(note);           // 保存到数据库
        return note;
    }

    /** 更新笔记: PUT /api/notes/{id} */
    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id, @RequestBody Note note){
        //通过id查询原笔记
        Note originalNote = noteService.get(id);
        //判断原笔记是否为空 update修改的前提原数据必须在库中
        if(originalNote == null){
            throw new RuntimeException("原笔记" + id + "不存在 无法修改");
        }

        //为修改后的笔记赋id值
        note.setId(id); //前端不会传id到body中

        //返回的还是原本传入的note 若数据库对字段有更新无法获取: return noteService.updateNote(note);
        noteService.update(note);   //先更新数据库
        return noteService.get(id); //再重新查找笔记
    }

    /** 删除一条笔记: DELETE /api/notes/{id} */
    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable Long id){
        noteService.deleteNote(id);
    }
}
