package com.sztu.websocket;

import com.alibaba.fastjson.JSONArray;
import com.sztu.config.WebSocketConfig;
import com.sztu.context.SpringBeanContext;
import com.sztu.dto.MsgDto;
import com.sztu.entity.ChatDetails;
import com.sztu.listener.XfXhWebSocketListener;
import com.sztu.model.XfXhStreamClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{studentId}/{chatHistoryId}", configurator = WebSocketConfig.class )
public class MyWebSocket {


    /** 当前在线客户端数量（线程安全的） */
    private static AtomicInteger onlineClientNumber = new AtomicInteger(0);

    /** 当前在线客户端集合（线程安全的）：以键值对方式存储，key是连接的编号，value是连接的对象 */
    private static Map<String , Session> onlineClientMap = new ConcurrentHashMap<>();



    /**
     * 客户端与服务端连接成功
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("studentId") String studentId, @PathParam("chatHistoryId") Long chatHistoryId){
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
            // 使用讯飞星火客户端
            XfXhStreamClient xfXhStreamClient = SpringBeanContext.getBean(XfXhStreamClient.class);
            XfXhWebSocketListener listener = new XfXhWebSocketListener();
            listener.setSession(session);
            // 可选：保存聊天详情
            ChatDetails chatDetails = new ChatDetails();
            chatDetails.setChatHistoryId(chatHistoryId);
            listener.setChatDetails(chatDetails);

            xfXhStreamClient.sendMsg(studentId, msgDtoList, listener);
        } catch (Exception e) {
            log.error("处理消息时出错：", e);
            session.getBasicRemote().sendText("系统错误，请稍后重试。");
        }
    }
}

