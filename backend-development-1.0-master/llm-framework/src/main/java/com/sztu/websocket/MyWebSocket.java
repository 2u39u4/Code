package com.sztu.websocket;

import com.alibaba.fastjson.JSONArray;
import java.net.URL;
import com.sztu.config.WebSocketConfig;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import com.sztu.context.SpringBeanContext;
import com.sztu.dto.MsgDto;
import com.sztu.entity.ChatDetails;
import com.sztu.listener.XfXhWebSocketListener;
import com.sztu.model.XfXhStreamClient;
import com.sztu.properties.XfXhProperties;
import com.sztu.service.ChatDetailsService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.springframework.stereotype.Component;
import java.util.*;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{studentId}/{chatHistoryId}", configurator = WebSocketConfig.class )
public class MyWebSocket {

    private XfXhStreamClient xfXhStreamClient;



    private  XfXhProperties xfXhProperties;

    private ChatDetailsService chatDetailsService;

    /** 当前在线客户端数量（线程安全的） */
    private static AtomicInteger onlineClientNumber = new AtomicInteger(0);

    /** 当前在线客户端集合（线程安全的）：以键值对方式存储，key是连接的编号，value是连接的对象 */
    private static Map<String , Session> onlineClientMap = new ConcurrentHashMap<>();



    /**
     * 客户端与服务端连接成功
     * @param session
     * @param
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("studentId") String studentId, @PathParam("chatHistoryId") Long chatHistoryId){
        /*
            do something for onOpen
            与当前客户端连接成功时
         */
        xfXhStreamClient = SpringBeanContext.getBean(XfXhStreamClient.class);
        xfXhProperties = SpringBeanContext.getBean(XfXhProperties.class);
        chatDetailsService = SpringBeanContext.getBean(ChatDetailsService.class);
        onlineClientNumber.incrementAndGet();
        onlineClientMap.put(session.getId(),session);
        log.info("与客户端连接成功");
    }

    /**
     * 客户端与服务端连接关闭
     * @param session
     * @param
     */
    @OnClose
    public void onClose(Session session, @PathParam("studentId") String studentId){
        /*
            do something for onClose
            与当前客户端连接关闭时
         */
        log.info("与客户端用户：{}断联", studentId);
        onlineClientNumber.decrementAndGet();
        onlineClientMap.remove(session.getId());


    }

    /**
     * 客户端与服务端连接异常
     * @param error
     * @param session
     * @param
     */
    @OnError
    public void onError(Throwable error,Session session, @PathParam("studentId") String studentId) {
        log.error("与客户端连接失败");
    }

    /**
     * 客户端向服务端发送消息
     * @param
     * @param
     * @throws IOException
     */
    @OnMessage
    public void onMsg(Session session, String questions,
                      @PathParam("studentId") String studentId,
                      @PathParam("chatHistoryId") Long chatHistoryId) throws IOException {

        log.info("收到来自用户：{} 的消息内容：{}", studentId, questions);

        try {
            List<MsgDto> msgDtoList = JSONArray.parseArray(questions, MsgDto.class);
            List<Map<String, String>> messages = new ArrayList<>();
            for (MsgDto msg : msgDtoList) {
                messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("messages", messages);
            payload.put("studentId", studentId);
            payload.put("chatHistoryId", chatHistoryId);

            // 将 payload 转成 JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(payload);

            // 使用 HttpURLConnection 流式读取
            URL url = new URL("http://localhost:8000/chat/ask");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    session.getBasicRemote().sendText(line); // 每行作为一次增量推送
                }
            }
            session.getBasicRemote().sendText("DONE");
        } catch (Exception e) {
            log.error("处理消息时出错：", e);
            session.getBasicRemote().sendText("系统错误，请稍后重试。");
        }
    }
}

