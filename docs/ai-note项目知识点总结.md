# ai-note 项目知识点总结

## 一、项目整体架构

```
浏览器/Postman 发请求
    ↓
NoteController    ← 接收 HTTP 请求，调 Service
    ↓
NoteService       ← 业务逻辑，调 Mapper
    ↓
NoteMapper        ← 写 SQL，操作数据库
    ↓
MySQL             ← 存数据
```

**为什么要分层？**
- Controller 只管收请求、返回结果，不写 SQL
- Service 只管业务逻辑，不知道请求从哪来
- Mapper 只管操作数据库，不知道业务逻辑
- 改其中一层不影响其他层（比如把 MySQL 换成 PostgreSQL，Controller 完全不用动）

---

## 二、Spring Boot 核心概念

### 2.1 注解

| 注解 | 作用 | 例子 |
|------|------|------|
| `@SpringBootApplication` | 标记启动类 | `AiNoteApplication` |
| `@RestController` | 标记控制器，返回 JSON | `NoteController` |
| `@RequestMapping("/api/notes")` | 统一路径前缀 | 类上加 |
| `@GetMapping` | 处理 GET 请求（查询） | `list()` |
| `@PostMapping` | 处理 POST 请求（新增） | `create()` |
| `@PutMapping` | 处理 PUT 请求（修改） | `update()` |
| `@DeleteMapping` | 处理 DELETE 请求（删除） | `deleteNote()` |
| `@RequestBody` | 把请求体 JSON 转成 Java 对象 | 方法参数上 |
| `@PathVariable` | 从 URL 路径里取参数 | `/{id}` |
| `@Service` | 标记 Service 类 | `NoteService` |
| `@Mapper` | 标记 Mapper 接口 | `NoteMapper` |

### 2.2 依赖注入（DI）

```java
// Spring 自动创建 NoteService 实例，传给 Controller
public NoteController(NoteService noteService, AiService aiService) {
    this.noteService = noteService;
    this.aiService = aiService;
}
```

**原理：** Spring 启动时扫描所有带 `@Service`、`@Mapper` 等注解的类，自动创建实例（叫 Bean），需要的地方自动传进去。

**你不用写 `new NoteService()`**，Spring 全帮你搞定了。

### 2.3 Jackson 序列化/反序列化

```
浏览器发 JSON → @RequestBody → Jackson → Java 对象（反序列化）
Java 对象 → return → Jackson → JSON 返回给浏览器（序列化）
```

**字段映射规则：** JSON 的 key 和 Java 对象的字段名一一对应。

**多余字段：** 默认忽略，不报错。
**缺少字段：** 默认填 null。

---

## 三、MyBatis

### 3.1 是什么

MyBatis 是一个 SQL 框架，让你用**注解或 XML** 写 SQL，不用手写 JDBC 代码。

### 3.2 注解写法（本项目用的）

```java
@Mapper
public interface NoteMapper {

    // 查：@Select
    @Select("SELECT * FROM note WHERE id = #{id}")
    Note findById(Long id);

    // 增：@Insert + @Options 回填 id
    @Insert("INSERT INTO note (title, content) VALUES (#{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Note note);

    // 改：@Update
    @Update("UPDATE note SET title=#{title}, content=#{content} WHERE id=#{id}")
    void update(Note note);

    // 删：@Delete
    @Delete("DELETE FROM note WHERE id = #{id}")
    void deleteById(Long id);
}
```

### 3.3 `#{}` 是什么

```sql
VALUES (#{title}, #{content})
```

`#{title}` = 从传入对象取 `title` 属性值，**安全地**填到 SQL 里（防 SQL 注入）。

底层等价于：
```java
pstmt.setString(1, note.getTitle());
```

### 3.4 驼峰映射

```properties
mybatis.configuration.map-underscore-to-camel-case=true
```

数据库字段 `ai_summary` → 自动映射到 Java 字段 `aiSummary`。

### 3.5 useGeneratedKeys 回填 id

```java
@Options(useGeneratedKeys = true, keyProperty = "id")
```

- `useGeneratedKeys = true`：告诉 MyBatis 要拿数据库自动生成的主键
- `keyProperty = "id"`：把主键值塞到对象的 `id` 字段
- 插入后 `note.getId()` 就能拿到值了

---

## 四、MySQL 连接配置

### 4.1 application.properties

```properties
# 数据库连接地址
spring.datasource.url=jdbc:mysql://localhost:3306/ai_note?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8

# 用户名密码
spring.datasource.username=root
spring.datasource.password=你的密码

# 驱动（不用记，固定写法）
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**url 拆解：**
```
jdbc:mysql://localhost:3306/ai_note?参数
     协议    地址      端口  库名
```

### 4.2 密码安全

**不能提交到 GitHub 的东西：**
- `application.properties`（含密码和 API key）
- `.env` 文件
- 任何包含密钥的文件

**正确做法：**
- 提交 `application.properties.example`（模板，不含真实密码）
- 真实配置加入 `.gitignore`
- 其他人克隆后复制 `.example` 文件，填自己的密码

---

## 五、RESTful 接口规范

| 操作 | HTTP 方法 | 路径 | 示例 |
|------|-----------|------|------|
| 查询所有 | GET | /api/notes | 获取笔记列表 |
| 查询单个 | GET | /api/notes/{id} | 获取 id=1 的笔记 |
| 新增 | POST | /api/notes | body 传 JSON |
| 修改 | PUT | /api/notes/{id} | body 传新数据 |
| 删除 | DELETE | /api/notes/{id} | 删除 id=1 的笔记 |

**核心思想：** 用 HTTP 方法表示操作类型，用路径表示资源。

---

## 六、Git 基础

### 6.1 三个区域

```
工作区（你写的代码）
    ↓ git add
暂存区（准备提交的文件）
    ↓ git commit
本地仓库（已提交的版本）
    ↓ git push
GitHub（云端备份）
```

### 6.2 常用命令

```bash
git init                    # 初始化仓库
git status                  # 查看状态（哪些文件改了、哪些没跟踪）
git add .                   # 把所有改动加入暂存区
git add 文件名              # 把指定文件加入暂存区
git commit -m "提交说明"    # 提交到本地仓库
git log                     # 查看提交历史
git push origin main        # 推到 GitHub
git pull origin main        # 从 GitHub 拉取最新代码
```

### 6.3 .gitignore

告诉 Git 哪些文件**不要管**：

```gitignore
target/              # 编译产物
.idea/               # IDEA 配置
*.log                # 日志文件
application.properties  # 含密码的配置
```

---

## 七、项目文件结构

```
ai-note/
├── pom.xml                              ← Maven 配置，管理依赖
├── .gitignore                           ← Git 排除规则
├── src/main/
│   ├── java/com/example/ainote/
│   │   ├── AiNoteApplication.java       ← 启动类
│   │   ├── controller/
│   │   │   ├── NoteController.java      ← 笔记接口
│   │   │   └── AiController.java        ← AI 接口
│   │   ├── service/
│   │   │   ├── NoteService.java         ← 笔记业务逻辑
│   │   │   └── AiService.java           ← AI 调用逻辑
│   │   ├── mapper/
│   │   │   └── NoteMapper.java          ← 数据库操作
│   │   └── model/
│   │       ├── Note.java                ← 笔记实体类
│   │       └── dto/
│   │           ├── ChatRequest.java     ← DeepSeek 请求体
│   │           └── ChatResponse.java    ← DeepSeek 响应体
│   └── resources/
│       ├── application.properties       ← 配置文件（不提交）
│       ├── application.properties.example ← 配置模板（提交）
│       └── static/
│           └── index.html               ← 前端页面
└── docs/                                ← 学习文档
    ├── Java基础概念总结.md
    ├── MySQL基础知识总结.md
    ├── ai-note项目知识点总结.md        ← 你正在看的
    └── 面试怎么讲.md
```

---

## 八、常见问题

### Q1: 启动报错 "Access denied"
密码不对，检查 `application.properties` 里的 `spring.datasource.password`。

### Q2: 启动报错 "Unknown database"
数据库不存在，在 MySQL 里执行 `CREATE DATABASE ai_note;`。

### Q3: 启动报错 "utf8mb4"
JDBC url 里 `characterEncoding` 写 `utf8`，不要写 `utf8mb4`。

### Q4: 查询返回空但数据库有数据
检查 Mapper 的 SQL 表名、字段名是否和数据库一致。

### Q5: 新增后 getId() 返回 null
Mapper 的 `@Insert` 有没有加 `@Options(useGeneratedKeys = true, keyProperty = "id")`。
