package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.exception.ApplicationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

@Api(tags = "测试类的相关接口")
//表示返回得结果是数据
@RestController
//定义一级映射路径
@RequestMapping("/test")
public class TestController {

    @ApiOperation("测是接口1.显示你好Spring boot")
    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring boot...";
    }

    @ApiOperation("测是接口4.按传入的姓名显示你好信息")
    @PostMapping("/helloByName")
    public String helloByName(@ApiParam("姓名") @RequestParam("name") String name){
        return "Hello : "+name;
    }

    @ApiOperation("测是接口2.显示抛出的异常信息")
    @GetMapping("/exception")
    public AppResult testException() throws Exception {
        throw new Exception("这是一个Exception....");
    }

    @ApiOperation("测是接口3.显抛出的自定义的异常信息")
    @GetMapping("/appException")
    public AppResult testApplicationException() {
        throw new ApplicationException("这是一个ApplicationException....");
    }
}
