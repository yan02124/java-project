package com.example.java_gobang.game;

import lombok.Data;


//这个类表示 落子请求
@Data
public class GameRequest {
    private String message;
    private int userId;
    private int row;
    private int col;

}
