package com.sztu.config;

import com.sztu.context.BaseContext;
import com.sztu.context.SpringBeanContext;
import com.sztu.entity.User;
import com.sztu.exception.JwtVerifyException;
import com.sztu.properties.JwtProperties;
import com.sztu.utils.JwtUtil;
import com.sztu.web.UserController;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;
import org.springframework.web.client.RestTemplate;

@EnableWebSocket
@Configuration
@Slf4j
public class WebSocketConfig extends ServerEndpointConfig.Configurator {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * 从 query 或 Sec-WebSocket-Protocol 获取 token，并校验。
     * 兼容前端 query 传递与子协议传递两种方式。
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response){
        String token = null;

        // 1) Query 参数
        String query = request.getQueryString();
        if (query != null && query.contains("token=")) {
            for (String p : query.split("&")) {
                if (p.startsWith("token=")) {
                    token = p.substring(6);
                    try { token = java.net.URLDecoder.decode(token, "UTF-8"); } catch (Exception ignored) {}
                    break;
                }
            }
        }

        // 2) Sec-WebSocket-Protocol（兼容旧方式）
        if ((token == null || token.isEmpty())) {
            List<String> list = request.getHeaders().get("Sec-WebSocket-Protocol");
            if (list != null && !list.isEmpty()) {
                token = list.get(0);
                response.getHeaders().put("Sec-WebSocket-Protocol", list); // echo 回去
            }
        }

        if (token == null || token.isEmpty()) {
            log.info("token为空");
            throw new JwtVerifyException("token为空");
        }

        try {
            JwtProperties jwtProperties = SpringBeanContext.getBean(JwtProperties.class);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long id = Long.valueOf(claims.get("USER_ID").toString());
            log.info("WebSocket连接验证成功，用户id: {}", id);
            BaseContext.setCurrentId(id);
        } catch (Exception e) {
            log.error("WebSocket JWT校验失败: {}", e.getMessage(), e);
            throw new JwtVerifyException("JWT校验失败");
        }

        super.modifyHandshake(sec, request, response);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
