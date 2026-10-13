# AI 智能笔记

一个超小的 Spring Boot 项目,用来学\*\*「Java 怎么调用大模型」\*\*。 能存笔记,能对笔记调用 DeepSeek 自动生成一句总结。

> 核心就一句话:**调用 AI = 发一次普通的 HTTP 请求**。没有黑魔法。

## 项目结构

```
ai-note/
├── pom.xml                 # 依赖清单(只引了 spring-boot-starter-web)
└── src/main/
    ├── resources/
    │   ├── application.properties  # 配置(DeepSeek 的 key 填这里,由 .example 复制而来)
    │   └── static/index.html       # 前端页面
    └── java/com/example/ainote/
    ├── AiNoteApplication.java   # 启动入口
    ├── controller/
    │   ├── NoteController.java  # 笔记接口
    │   └── AiController.java    # AI 接口
    ├── service/
    │   ├── NoteService.java     # 存笔记(内存)
    │   └── AiService.java       # ★ 调 DeepSeek
    └── model/
        ├── Note.java
        └── dto/                 # 发/收大模型的 JSON 结构
            ├── ChatRequest.java
            └── ChatResponse.java
```

## 跑起来(3 步)

1. **填 key**:打开 `src/main/resources/application.properties`,把 `deepseek.api-key=sk-在这里填你的key` 换成你自己的 key。 (去 https://platform.deepseek.com 注册 → 充值几块钱 → 创建 API key)

2. **打开项目**:IntelliJ IDEA → `File → Open` → 选 `ai-note` 这个文件夹。 IDEA 会自动识别成 Maven 项目、用内置 Maven 下载依赖。

3. **运行**:打开 `AiNoteApplication.java`,点左边的绿色三角形(或右键 → Run)。 看到 `Tomcat started on port 8080` 就成功了。

启动成功后,打开浏览器访问 **http://localhost:8080/**,就能看到网页界面,直接点按钮、输文字,不用 curl 了。

## 接口怎么测(4 个)

| 作用 | 方法 + 地址 |
| --- | --- |
| 列出所有笔记 | `GET /api/notes` |
| 创建笔记 | `POST /api/notes` |
| 对任意文字做 AI 总结 | `POST /api/ai/summarize` |
| 对某条笔记做 AI 总结 | `POST /api/notes/{id}/summarize` |

浏览器能直接测 GET;POST 用 curl(终端里敲)最方便:

```bash
# 创建一条笔记
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"title":"测试","content":"今天学了 Spring Boot 的 Controller 和 Service"}'

# 对任意文字做 AI 总结(最能体现 AI 功能的接口)
curl -X POST http://localhost:8080/api/ai/summarize \
  -H "Content-Type: application/json" \
  -d '{"text":"Spring Boot 会自动启动内嵌 Tomcat,Controller 收 HTTP 请求,Service 干业务逻辑。"}'

# 对 id=1 的笔记做 AI 总结(总结会存回这条笔记)
curl -X POST http://localhost:8080/api/notes/1/summarize
```

## 看代码的顺序(推荐)

`AiService.java` → `AiController.java` → `NoteController.java` → `NoteService.java` → `Note.java`。 看不懂的地方,看 `docs/Java基础概念总结.md`。

## Q

- 1.依赖注入
- 2.final