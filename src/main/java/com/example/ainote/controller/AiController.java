package com.example.ainote.controller;

import com.example.ainote.service.AiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 负责「AI」相关的 HTTP 接口。
 * 这是最直接的 AI 接口:给它一段文字,它返回 AI 总结。
 */

//声明该类是接口控制器 专门用于接收浏览器发来的http的请求
@RestController //等价于@Controller+@ResponseBody

//定义访问路径
@RequestMapping("/api/ai")  //统一加前缀路径
public class AiController {

    private final AiService aiService;

    //构造器注入:将创建好的AiService传给构造器并赋值给this.  等价于-> 字段注入@Autowired(无法添加final)
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * 对任意一段文字做总结:POST /api/ai/summarize
     * body 里放 {"text":"你想总结的内容"}
     */

    //专门用于接收POST请求
    @PostMapping("/summarize")

    /**
     * json是键值对结构
     * map是键值对集合
     */
                                         //将POST请求body中传过来的的json转为java对象
    public Map<String, String> summarize(@RequestBody Map<String, String> body) throws Exception {
        String text = body.get("text"); //从map里获取“text”对应的值

        String summary = aiService.summarize(text);
        return Map.of("summary", summary);
        /**
         * Map.of("summary", summary) 后续无法再修改数据
         * 等价于
         * Map<String, String> map = new HashMap<>();   可变Map
         * map.put("summary", summary);
         */
    }
}
