# Java 基础概念总结(大白话版)

配合 `ai-note` 这个项目看,每一条都能在代码里找到对应。

---

## 1. 这个项目整体在干嘛?

你在浏览器/终端发一个请求(比如「帮我总结这段话」),程序跑了一圈,返回一个结果。

**一次请求的完整旅程**(以「总结某条笔记」为例):

```
浏览器 POST /api/notes/1/summarize
      ↓
NoteController 收到请求(它只负责「接客」,自己不干活)
      ↓
叫 AiService 去调 DeepSeek(它负责「干活」)
      ↓
AiService 发一个 HTTP 请求给 DeepSeek,拿到 AI 的回答
      ↓
把回答填回笔记,一层层返回给浏览器
```

记住这个「**一条线从上往下走**」的感觉,分层就懂了。

---

## 2. Spring Boot 是什么?

一句话:**一个帮你把 Java 程序变成「网站后端」的框架**。

- 不用它:你得自己写一堆代码去「监听 8080 端口」「解析 HTTP 请求」,累死。
- 用它:你写几个类、标几个注解,它自动帮你启动内嵌的 Tomcat(一个 Web 服务器),自动把你的方法暴露成网址。

苍穹外卖用的就是 Spring Boot,所以这项目和它是一家人。

---

## 3. 分层:Controller / Service / Model

把代码分成三堆,各干各的:

| 层 | 类 | 职责 | 类比 |
|----|----|------|------|
| Controller | `NoteController` `AiController` | 收 HTTP 请求、返回结果 | 前台接待 |
| Service | `NoteService` `AiService` | 干真正的业务逻辑 | 后厨大厨 |
| Model | `Note` | 描述「一条笔记长什么样」 | 菜单上的菜 |

**为什么要分层?**
- 前台(Controller)只负责「接客 + 上菜」,不管菜怎么做;
- 后厨(Service)只负责「做菜」,不管谁点单;
- 哪天想把「存笔记」从内存换成数据库,只改 Service,Controller 一行不用动。

这就是面试常说的「**职责单一、解耦**」。

---

## 4. 注解是什么?

`@` 开头的都是注解。你可以理解成**贴在类/方法上的「标签」**,Spring 看到标签就知道该干什么。

| 注解 | 作用 | 在哪个文件 |
|------|------|-----------|
| `@SpringBootApplication` | 告诉 Spring「这是启动类」 | `AiNoteApplication` |
| `@RestController` | 这个类是「接 HTTP 请求」的 Controller | 两个 Controller |
| `@RequestMapping("/api/notes")` | 这类的所有接口,网址都以 `/api/notes` 开头 | `NoteController` |
| `@GetMapping` / `@PostMapping` | 这个方法响应 GET / POST 请求 | Controller 里的方法 |
| `@RequestBody` | 把请求里的 JSON 自动转成 Java 对象 | 方法参数前 |
| `@PathVariable` | 把网址里的 `{id}` 取出来,塞进参数 | `summarize(Long id)` |
| `@Service` | 标记「这是 Service 层的类」,让 Spring 管理它 | 两个 Service |
| `@Value("${deepseek.api-key}")` | 把配置文件里的值,注入到这个字段 | `AiService` |

> **关于 `@Value` 的一个常见困惑**:`@Value("${deepseek.api-key}")` 括号里写的不是「值」,而是「名字」。真正的值(那个 `sk-xxx`)只写在 `application.properties` 里一次。代码这句的意思是「去配置里,把名叫 `deepseek.api-key` 的值拿过来塞给我」—— 你不是写了两遍,值只出现一次。为什么不用 `private String apiKey = "sk-xxx"` 写死?因为要「配置和代码分离」:换 key 不改代码、key 不随代码泄露到 git。

---

## 5. 依赖注入(为什么很少看到 new?)

看 `NoteController` 的构造器:

```java
public NoteController(NoteService noteService, AiService aiService) {
    this.noteService = noteService;
    this.aiService = aiService;
}
```

你没写 `new NoteService()`,但 `noteService` 居然能用 —— 因为 **Spring 帮你 new 好、再塞进来**,这叫**依赖注入(DI)**。

- 你只要用 `@Service` 标了类,Spring 就把它「放进仓库」;
- 谁需要,就在构造器里声明,Spring 自动「送货上门」。

好处:Controller 不关心 Service 怎么造的,换实现也方便。

> 老的写法是用 `@Autowired` 直接标在字段上,效果一样;现在更推荐**构造器注入**(就是上面这种),因为更安全、更好测试。面试问起就说:「我用的构造器注入,Spring 官方现在推荐这种方式。」

---

## 6. HTTP 请求 / 响应是什么?

浏览器(或 curl)和服务器之间「对话」的格式。

**请求**分几部分:
- 方法:`GET`(要数据)、`POST`(提交数据)……
- 地址:`http://localhost:8080/api/notes`
- 头(headers):比如 `Authorization` 放你的 key、`Content-Type` 说明是 JSON
- 体(body):POST 时真正带的数据(JSON)

**响应**最关键的是**状态码**:
- `200` 成功
- `401` 没权限(比如 key 错了,你项目里见到的 `Authentication Fails` 就是它)
- `404` 找不到
- `500` 服务器自己出错了

`AiService` 里那句 `if (response.statusCode() != 200) throw ...` 就是在检查状态码。

---

## 7. JSON 是什么?

**JSON 就是一种「把数据写成文字」的格式**,让不同程序之间能交换数据。

```json
{ "title": "测试", "content": "今天学了 Spring Boot" }
```

- 大括号包对象,key 加引号,值可以是字符串/数字/数组/对象。
- Java 里对应的工具是 **Jackson**(`ObjectMapper`)：
  - 对象 → JSON 字符串:`objectMapper.writeValueAsString(obj)`
  - JSON 字符串 → 对象:`objectMapper.readValue(str, Note.class)`

你发给 DeepSeek 的是 JSON,它回给你的也是 JSON,`ChatRequest` / `ChatResponse` 就是这两个 JSON 的「Java 影子」。

> **踩坑提醒(这个项目真的踩到过)**:第三方接口返回的 JSON 字段往往比你需要的多很多 —— DeepSeek 除了 `choices`,还返回 `id`、`object`、`created`、`index`、`finish_reason` 等。Jackson 默认遇到「没声明的字段」会直接报 `UnrecognizedPropertyException`。解决办法是在对应的类上标 `@JsonIgnoreProperties(ignoreUnknown = true)`,让它忽略多余字段。本项目 `ChatResponse`(包括它内部嵌套的 `Choice`、`Message`)都加了这个注解。

---

## 8. DTO 是什么?

DTO = Data Transfer Object,**专门用来「搬运数据」的类**。

- `Note` 是「一条笔记」这个业务对象;
- `ChatRequest` / `ChatResponse` 不是业务对象,只是「发给大模型 / 大模型返回」的数据壳子,所以叫 DTO,放 `dto` 包。

---

## 9. Maven 和 pom.xml 是什么?

- **Maven** 是个「构建工具」:帮你下载依赖、编译、打包。IDEA 里内置了,你不用单独装。
- **pom.xml** 是 Maven 的「购物清单」:声明项目要用哪些库。

我们只列了一个依赖 `spring-boot-starter-web`,但它是「全家桶」,会连带拉来 Jackson、Tomcat 等一整套。这就是为什么几行配置就能跑起来。

---

## 10. 内存存储 vs 数据库

`NoteService` 里用的是:

```java
private final List<Note> notes = new ArrayList<>();
```

笔记存在**内存**里,**程序一关就没了**。这是故意这么做的 —— 为了省掉装数据库的时间,让你先跑通全流程。

真实项目会换成 MySQL(苍穹外卖就是)。等你时间够了,把 `NoteService` 里的 List 换成 MyBatis 访问数据库即可,Controller 不用改 —— 这就是分层的威力。
