package com.example.server.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 웹소켓 핸들러 - 세션 구분 X
 */
@Controller
public class WebSocketHandler extends TextWebSocketHandler {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage greeting(Message message) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }
}