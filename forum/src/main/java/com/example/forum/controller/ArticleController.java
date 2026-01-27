package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.model.Article;
import com.example.forum.model.Board;
import com.example.forum.model.User;
import com.example.forum.services.IArticleService;
import com.example.forum.services.IBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
@Api(tags = "文章接口")
@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private IArticleService articleService;
    @Resource
    private IBoardService boardService;

    /**
     * 发布新帖子
     * @param boardId 板块Id
     * @param title 文章标题
     * @param content 文章内容
     * @return
     */
    @ApiOperation("发布新贴")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @ApiParam("板块Id") @RequestParam("boardId") @NonNull Long boardId,
                            @ApiParam("文章标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("文章内容") @RequestParam("content") @NonNull String content) {

        //校验用户是否禁言
        HttpSession session = request.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        if(user.getState() == 1){
            //用户已禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //板块的校验
        Board board = boardService.selectById(boardId.longValue());
        if(board == null || board.getDeleteState() == 1
                || board.getState() == 1){
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            //返回响应
            return AppResult.failed(ResultCode.FAILED_CREATE);
        }
        //封装文章对象
        Article article = new Article();
        article.setTitle(title); //标题
        article.setContent(content); //正文
        article.setBoardId(boardId); //板块Id
        article.setUserId(user.getId()); //作者Id
        //调用Service
        articleService.create(article);
        //响应
        return AppResult.success();
    }

    @ApiOperation("获取帖子列表")
    @GetMapping("/getAllByBoardId")
    public AppResult<List<Article>> getAllByBoardId(@ApiParam("板块Id") @RequestParam(value = "boardId",required = false) Long boardId) {

        //定义返回的集合
        List<Article> articles;
        if(boardId == null){
            //查询所有
            articles = articleService.selectAll();
        }else{
            articles = articleService.selectAllByBoardId(boardId);
        }

        //结果是否为空
        if(articles == null){
            //如果结合集为空,那么创建上个空集合
            articles = new ArrayList<>();
        }
        //响应结果
        return AppResult.success(articles);
    }
    
    @ApiOperation("根据帖子Id获取详情")
    @GetMapping("/details")
    public AppResult<Article> getDetails(HttpServletRequest request,
                                         @ApiParam("帖子Id") @RequestParam("id")
                                         @NonNull Long id){
        //从 session 中获取当前登录的用户（游客模式下可能为空）
        HttpSession session = request.getSession(false);
        User user = null;
        if(session != null){
            user = (User)session.getAttribute(AppConfig.USER_SESSION);
        }
        //调用Service,获取帖子详情
        Article article = articleService.selectDetailById(id);
        //判断结果是否为空
        if(article == null){
            //返回错误信息
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        //判断当前用户是否为作者（游客模式下user为null，不是作者）
        if(user != null && user.getId().equals(article.getUserId())){
            //标识为作者
            article.setOwn(true);
        }
        //返回结果
        return AppResult.success(article);
    }

    @ApiOperation("修改帖子")
    @PostMapping("/modify")
    public AppResult modify(HttpServletRequest request,
                            @ApiParam("帖子Id") @RequestParam("id") @NonNull Long id,
                            @ApiParam("帖子标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("帖子正文") @RequestParam("content") @NonNull String content){
        // 获取当前登录的用户
        HttpSession session = request.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        //校验用户状态
        if(user.getState() == 1){
            //返回错误描述
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 查询帖子详情
        Article article = articleService.selectById(id);
        //校验帖子是否有效
        if(article == null){
            //返回错误描述
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        // 判断用户是不是作者
        if(user.getId() != article.getUserId()){
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        //判断帖子的状态 - 已归档
        if(article.getState() == 1 || article.getDeleteState() == 1){
            //返回错误描述
            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }
        //调用Service
        articleService.modify(id, title, content);
        //打印日志
        log.info("帖子更新成功. Article id = " + id + " User id = " + user.getId() + ".");
        //返回正确的结果
        return AppResult.success();
    }

    @ApiOperation("点赞")
    @PostMapping("/thumbsUp")
    public AppResult thumbsUp (HttpServletRequest request,
                               @ApiParam("帖子Id") @RequestParam("id") @NonNull Long id){
        // 校验用户的状态
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //判断用户是否被禁言
        if(user.getState() == 1){
            //返回结果
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 调用Service
        articleService.thumbsUpById(id);
        //返回结果
        return AppResult.success();
    }

    @ApiOperation("删除帖子")
    @PostMapping("/delete")
    public AppResult deleteById(HttpServletRequest request,
                                @ApiParam("帖子Id") @RequestParam("id") @NonNull Long id){
        //校验用户状态
        HttpSession session = request.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        if(user.getState() == 1){
            //表示用户禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //查询帖子详情
        Article article = articleService.selectById(id);
        //校验帖子状态
        if(article == null || article.getDeleteState() == 1){
            //帖子已删除
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        //校验当前登录的用户是不是作者或管理员
        boolean isAdmin = user.getIsAdmin() != null && user.getIsAdmin() == 1;
        if(!isAdmin && !user.getId().equals(article.getUserId())){
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        //调用Service
        articleService.deleteById(id);
        //返回操作成功
        return AppResult.success();
    }

    @ApiOperation("获取用户的帖子列表")
    @GetMapping("/getAllByUserId")
    public AppResult<List<Article>> getAllByUserId(HttpServletRequest request,
                                    @ApiParam("用户Id") @RequestParam(value = "userId",required = false) Long userId){
        //如果UserId为空,那么从session中获取当前登录的用户Id
        if(userId == null){
            //获取Session
            HttpSession session = request.getSession(false);
            //获取User对象
            User user = (User)session.getAttribute(AppConfig.USER_SESSION);
            userId = user.getId();
        }
        //调用Service
        List<Article> articles = articleService.selectByUserId(userId);
        //返回结果
        return AppResult.success(articles);

    }

    @ApiOperation("管理员删除帖子")
    @PostMapping("/adminDelete")
    public AppResult adminDeleteById(HttpServletRequest request,
                                    @ApiParam("用户昵称") @RequestParam("nickname") @NonNull String nickname,
                                    @ApiParam("帖子标题") @RequestParam("title") @NonNull String title) {
        // 获取当前登录用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 校验是否为管理员
        if (user.getIsAdmin() == null || user.getIsAdmin() != 1) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        // 根据昵称和标题查询帖子
        Article article = articleService.selectByNicknameAndTitle(nickname, title);
        // 校验帖子是否存在
        if (article == null) {
            return AppResult.failed("未找到该帖子，请检查昵称和标题是否正确");
        }
        // 调用Service删除
        articleService.deleteById(article.getId());
        log.info("管理员删除帖子成功. Article id = " + article.getId() + ", Admin id = " + user.getId());
        return AppResult.success("帖子删除成功");
    }

    /**
     * 搜索帖子接口
     * 根据关键字搜索帖子标题和内容
     * @param keyword 搜索关键字
     * @return 匹配的帖子列表
     */
    @ApiOperation("搜索帖子")
    @GetMapping("/search")
    public AppResult<List<Article>> search(@ApiParam("搜索关键字") @RequestParam("keyword") String keyword) {
        // 如果关键字为空或只有空格，返回空列表
        if (keyword == null || keyword.trim().isEmpty()) {
            return AppResult.success(new ArrayList<>());
        }
        // 调用Service层执行搜索，去除关键字前后空格
        List<Article> articles = articleService.searchByKeyword(keyword.trim());
        // 如果结果为空，返回空列表
        if (articles == null) {
            articles = new ArrayList<>();
        }
        // 返回搜索结果
        return AppResult.success(articles);
    }

    @ApiOperation("高级查询")
    @GetMapping("/advancedSearch")
    public AppResult<List<Article>> advancedSearch(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "boardId", required = false) Long boardId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "searchType", defaultValue = "and") String searchType) {
        
        List<Article> articles = articleService.advancedSearch(title, content, author,
                                                            boardId, startDate, endDate,
                                                            searchType);
        return AppResult.success(articles);
    }


}

