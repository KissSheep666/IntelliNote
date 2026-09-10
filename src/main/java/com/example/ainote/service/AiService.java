package com.example.ainote.service;

import com.example.ainote.model.dto.ChatRequest;
import com.example.ainote.model.dto.ChatResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * AI调用逻辑
 * ★ 整个项目的灵魂:负责调用 DeepSeek 大模型。
 *
 * 记住一句话:「调用 AI 本质上就是一次普通的 HTTP 请求」。
 * 你把一段文字用 JSON 发给 DeepSeek 的网址,它返回一段 JSON,里面是结果。
 * 这里没有黑魔法,就四步:
 *   1. 拼 JSON 请求体(对象 -> JSON 字符串)
 *   2. 构造 HTTP 请求(带 Authorization 头放你的 key)
 *   3. 发请求、拿响应 JSON
 *   4. 解析响应,取出 AI 的回答
 */
@Service    //spring启动时扫描并创建实例 不需要手动new对象
public class AiService {

    // 1. @Value 会把 application.properties 里的配置值注入到这些字段
    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    @Value("${deepseek.model}")
    private String model;

    // 2.创建对象映射器
    // Jackson 的 ObjectMapper(对象映射器):负责「Java 对象 <-> JSON 字符串」互转
        //new...: 普通构造器 调用类的构造方法创建对象
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 3.创建JDK 自带的 HttpClient对象
    // 负责发 HTTP 请求(不用额外引依赖 无需在pom.xml文件里添加dependency)
        //HttpClient.new...: 静态工厂方法 内部创建对象
    private final HttpClient httpClient = HttpClient.newHttpClient();


    /**
     * 把一段文字发给 DeepSeek,让它用一句话总结,返回总结内容。
     */
    public String summarize(String text) throws Exception {
        // ---- 第 1 步:拼请求体 ----
        // 1.message: 用户输入内容
        ChatRequest.Message message = new ChatRequest.Message();
        message.setRole("user");
        message.setContent("请用一句话总结下面的内容:\n" + text);

        // 2.request请求体: 外层请求对象
        ChatRequest request = new ChatRequest();
        request.setModel(model);
        request.setMessages(List.of(message));  //快速创建不可变集合

        // 3.Java对象转json字符串
        String requestJson = objectMapper.writeValueAsString(request);

        // ---- 第 2 步:构造 HTTP 请求 ----
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))   //接口地址: 字符串转网址
                .timeout(Duration.ofSeconds(60))    //设置最大等待时间防止卡死: 创建时间
                .header("Content-Type", "application/json") //设置请求格式: 字符串转json
                .header("Authorization", "Bearer " + apiKey) // 你的 key 放这里(身份认证)
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        // ---- 第 3 步:发送请求、拿响应 ----
        HttpResponse<String> response =
                // .send发送请求    .bodyHandlers.ofString把响应内容转为字符串
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        /**
         * response包含: statusCode响应吗 body响应体(服务器返回的json字符串)
         */
        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "DeepSeek 调用失败,状态码:" + response.statusCode() + ", 返回:" + response.body());
        }

        // ---- 第 4 步:解析响应,取出 choices[0].message.content ----
        ChatResponse chatResponse =
                // objectMapper.readValue(json字符串, 转成什么类型)
                //.readValue将json字符串转为java对象
                objectMapper.readValue(response.body(), ChatResponse.class);    //.class获取这个类本身
        return chatResponse.getChoices().get(0).getMessage().getContent();
    }


    /**
     * 根据内容AI生成一个标题
     */
    public String generateTitle(String text) throws IOException, InterruptedException {
        //1.创建请求体ChatRequest对象 并给字段赋值(传参text = 字段content)
        ChatRequest.Message message = new ChatRequest.Message();    //内部类
        message.setRole("user");
        message.setContent("请根据下面的内容生成一个简短的标题:\n" + text);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel(model);
        chatRequest.setMessages(List.of(message));  //等价于List<Message> list = new ArrayList<>(); list.add(message)

        //2.Java对象转Json字符串 构造HttpRequest对象
        String requestJson = objectMapper.writeValueAsString(chatRequest);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))   //接口地址
                .timeout(Duration.ofSeconds(60))    //最大等待时间
                .header("Content-Type", "application/json")   //权限格式
                .header("Authorization", "Bearer " + apiKey)    //key
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        //3.发送http请求并解析响应
        /**
         * HttpResponse<String> response = httpClient.send(httpRequest,  响应对象
         *   HttpResponse.BodyHandlers.ofString());   固定写法 响应类型(字符串)
         */
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ChatResponse chatResponse = objectMapper.readValue(response.body(), ChatResponse.class); //?解析响应为chatresponse类型

        //4.获取目标字段并返回结果
        return chatResponse.getChoices().get(0).getMessage().getContent();
    }
}
