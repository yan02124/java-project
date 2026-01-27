package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Board;
import com.example.forum.services.IBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.forum.config.AppConfig;
import com.example.forum.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "板块接口")
@RestController
@RequestMapping("/board")
public class BoardController {

    //从配置文件中读取值,如果没有配置,默认值为9
    @Value("${bit-forum.index.board-num:9}")
    private Integer indexBoardNum;

    @Resource
    private IBoardService boardService;

    /**
     * 查询首页板块列表
     * @return
     */
    @ApiOperation("获取首页板块列表")
    @GetMapping("/topList")
    public AppResult<List<Board>> topList() {
        log.info("首页板块个数为：" + indexBoardNum);
        //调用Service查询结果
        List<Board> boards = boardService.selectByNum(indexBoardNum);
        if(boards == null){
            boards = new ArrayList<>();
        }
        //返回结果
        return AppResult.success(boards);
    }

    @ApiOperation("获取板块信息")
    @GetMapping("/getById")
    public AppResult<Board> getById(@ApiParam("板块Id") @RequestParam("id") @NonNull Long id) {
        //调用Service
        Board board = boardService.selectById(id);
        //对查询结果进行校验
        if(board == null || board.getDeleteState() == 1){
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        //返回结果
        return AppResult.success(board);
    }

    @ApiOperation("新增板块")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @ApiParam("板块名称") @RequestParam("name") @NonNull String name) {
        // 获取当前登录用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 校验是否为管理员
        if (user.getIsAdmin() == null || user.getIsAdmin() != 1) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        // 创建板块对象
        Board board = new Board();
        board.setName(name);
        // 调用Service
        boardService.create(board);
        return AppResult.success("板块创建成功");
    }

    @ApiOperation("删除板块")
    @PostMapping("/delete")
    public AppResult delete(HttpServletRequest request,
                            @ApiParam("板块Id") @RequestParam("id") @NonNull Long id) {
        // 获取当前登录用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 校验是否为管理员
        if (user.getIsAdmin() == null || user.getIsAdmin() != 1) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        // 调用Service
        boardService.deleteById(id);
        return AppResult.success("板块删除成功");
    }


}
