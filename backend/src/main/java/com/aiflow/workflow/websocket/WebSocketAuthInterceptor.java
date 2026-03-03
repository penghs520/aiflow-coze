package com.aiflow.workflow.websocket;

import com.aiflow.workflow.util.AdminJwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * WebSocket 认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final AdminJwtUtil adminJwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 从请求头获取 token
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                if (adminJwtUtil.validateToken(token)) {
                    Long adminId = adminJwtUtil.getAdminIdFromToken(token);

                    // 将管理员 ID 存入 WebSocket session
                    accessor.setUser(new SimplePrincipal(adminId.toString()));
                    log.info("WebSocket 连接认证成功: adminId={}", adminId);
                    return message;
                }
            }

            log.warn("WebSocket 连接认证失败: token 无效");
            throw new IllegalArgumentException("Invalid token");
        }

        return message;
    }

    /**
     * 简单的 Principal 实现
     */
    private static class SimplePrincipal implements Principal {
        private final String name;

        public SimplePrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
