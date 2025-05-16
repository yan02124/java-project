package com.example.java_gobang.game;

import lombok.Data;

//这是表示一个 websocket 的匹配请求
@Data
public class MatchRequest {
    private String message = "";
}
