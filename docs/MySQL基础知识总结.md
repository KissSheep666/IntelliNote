# MySQL 基础知识总结

## 1. 连接与退出

```bash
# 登录（推荐，密码不可见）
mysql -u root -p

# 退出
EXIT;
# 或
QUIT;
```

## 2. 数据库操作

```sql
-- 查看所有数据库
SHOW DATABASES;

-- 创建数据库（utf8mb4 支持中文和 emoji）
CREATE DATABASE ai_note DEFAULT CHARACTER SET utf8mb4;

-- 切换数据库
USE ai_note;

-- 删除数据库（慎用！）
DROP DATABASE ai_note;

-- 查看当前在哪个数据库
SELECT DATABASE();
```

## 3. 表操作

### 3.1 建表

```sql
CREATE TABLE note (
    id        BIGINT       PRIMARY KEY AUTO_INCREMENT,  -- 主键，自增
    title     VARCHAR(255) NOT NULL,                     -- 标题，不能为空
    content   TEXT,                                      -- 内容，长文本
    ai_summary TEXT,                                     -- AI 总结
    created_at DATETIME    DEFAULT CURRENT_TIMESTAMP,    -- 创建时间，自动填
    updated_at DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 更新时间，自动更新
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**常用字段类型：**

| 类型 | 用途 | 示例 |
|------|------|------|
| `INT` / `BIGINT` | 整数 | id、数量 |
| `VARCHAR(n)` | 短文本（最长 n 字符） | 名称、标题 |
| `TEXT` | 长文本 | 内容、描述 |
| `DECIMAL(m,n)` | 精确小数 | 价格 `DECIMAL(10,2)` |
| `DATETIME` | 日期时间 | 2026-09-09 12:00:00 |
| `BOOLEAN` | 布尔值 | true / false |

**常用约束：**

| 约束 | 含义 |
|------|------|
| `PRIMARY KEY` | 主键，唯一标识一行 |
| `AUTO_INCREMENT` | 自动递增 |
| `NOT NULL` | 不能为空 |
| `UNIQUE` | 不能重复 |
| `DEFAULT 值` | 默认值 |
| `FOREIGN KEY` | 外键，关联另一张表 |

### 3.2 查看表

```sql
-- 查看当前库所有表
SHOW TABLES;

-- 查看表结构
DESC note;

-- 查看建表语句
SHOW CREATE TABLE note;
```

### 3.3 改表

```sql
-- 加一列
ALTER TABLE note ADD COLUMN status INT DEFAULT 0;

-- 改列类型
ALTER TABLE note MODIFY COLUMN title VARCHAR(500) NOT NULL;

-- 删一列
ALTER TABLE note DROP COLUMN status;

-- 删表（慎用！）
DROP TABLE note;
```

## 4. 增删改查（CRUD）

### 4.1 插入数据 INSERT

```sql
-- 插入一条
INSERT INTO note (title, content) VALUES ('第一条笔记', 'Hello MySQL');

-- 插入多条
INSERT INTO note (title, content) VALUES
    ('Java基础', '面向对象三大特征'),
    ('Spring Boot', '自动配置原理'),
    ('MySQL', '增删改查');
```

### 4.2 查询数据 SELECT（最重要！）

```sql
-- 查所有列
SELECT * FROM note;

-- 查指定列
SELECT id, title FROM note;

-- 条件查询 WHERE
SELECT * FROM note WHERE id = 1;
SELECT * FROM note WHERE title LIKE '%Java%';    -- 模糊匹配
SELECT * FROM note WHERE id IN (1, 2, 3);        -- 多值匹配
SELECT * FROM note WHERE created_at IS NULL;      -- 判空
SELECT * FROM note WHERE id > 1 AND title LIKE '%Spring%';  -- AND
SELECT * FROM note WHERE id > 3 OR title LIKE '%Java%';     -- OR

-- 排序 ORDER BY
SELECT * FROM note ORDER BY created_at DESC;      -- 倒序（新在前）
SELECT * FROM note ORDER BY id ASC;               -- 正序（默认）

-- 限制条数 LIMIT
SELECT * FROM note LIMIT 3;                       -- 只取前 3 条
SELECT * FROM note LIMIT 0, 10;                   -- 从第 0 条开始取 10 条（分页）

-- 去重 DISTINCT
SELECT DISTINCT title FROM note;

-- 聚合函数
SELECT COUNT(*) FROM note;                        -- 总条数
SELECT MAX(id) FROM note;                         -- 最大值
SELECT MIN(id) FROM note;                         -- 最小值

-- 分组 GROUP BY + 聚合
SELECT title, COUNT(*) as cnt FROM note GROUP BY title;
```

### 4.3 更新数据 UPDATE

```sql
-- 更新指定行
UPDATE note SET title = '修改后的标题' WHERE id = 1;

-- 更新多个字段
UPDATE note SET title = '新标题', content = '新内容' WHERE id = 1;

-- ⚠️ 不加 WHERE 会更新所有行！
UPDATE note SET status = 1;  -- 所有记录都被改了，危险！
```

### 4.4 删除数据 DELETE

```sql
-- 删除指定行
DELETE FROM note WHERE id = 1;

-- ⚠️ 不加 WHERE 会删除所有行！
DELETE FROM note;  -- 清空表，危险！
```

## 5. 连表查询 JOIN（重点难点）

假设有两张表：

```sql
-- 用户表
CREATE TABLE user (
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- 笔记表（有外键指向 user）
CREATE TABLE note (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    title   VARCHAR(255) NOT NULL,
    content TEXT,
    user_id BIGINT,                              -- 外键
    FOREIGN KEY (user_id) REFERENCES user(id)    -- 关联用户表
);
```

```sql
-- INNER JOIN：只返回两表都匹配的行
SELECT note.title, user.name
FROM note
INNER JOIN user ON note.user_id = user.id;

-- LEFT JOIN：返回左表所有行，右表没匹配的显示 NULL
SELECT note.title, user.name
FROM note
LEFT JOIN user ON note.user_id = user.id;

-- 起别名（简化写法）
SELECT n.title, u.name
FROM note n
JOIN user u ON n.user_id = u.id;
```

**JOIN 类型对比：**

| 类型 | 含义 |
|------|------|
| `INNER JOIN` | 两表都有的才返回 |
| `LEFT JOIN` | 左表全返，右表没有的填 NULL |
| `RIGHT JOIN` | 右表全返，左表没有的填 NULL |

## 6. 事务（了解即可）

```sql
START TRANSACTION;           -- 开始事务
UPDATE account SET balance = balance - 100 WHERE id = 1;
UPDATE account SET balance = balance + 100 WHERE id = 2;
COMMIT;                      -- 提交（两个都成功才生效）
-- 如果出错：
ROLLBACK;                    -- 回滚（两个都撤销）
```

## 7. Java 开发常用套路

在 Spring Boot 项目中，通常不直接写 SQL，而是用 **MyBatis** 操作数据库：

```java
// 对应 SQL: SELECT * FROM note WHERE id = #{id}
@Select("SELECT * FROM note WHERE id = #{id}")
Note findById(Long id);

// 对应 SQL: INSERT INTO note (title, content) VALUES (#{title}, #{content})
@Insert("INSERT INTO note (title, content) VALUES (#{title}, #{content})")
void insert(Note note);
```

## 8. 常见错误

| 错误 | 原因 | 解决 |
|------|------|------|
| `Unknown column` | 列名写错了 | `DESC 表名` 看一下 |
| `Table doesn't exist` | 表名写错了或没切库 | `USE 数据库名` |
| `Duplicate entry` | 主键或唯一键重复 | 检查数据 |
| `Cannot be null` | 该字段设了 NOT NULL | 给个值 |
| `Access denied` | 密码错了 | 检查用户名密码 |
