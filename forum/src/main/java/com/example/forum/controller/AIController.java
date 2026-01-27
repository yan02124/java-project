package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.model.Article;
import com.example.forum.model.User;
import com.example.forum.services.IAIService;
import com.example.forum.services.IArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * AI助手控制器
 * 提供AI回复和摘要功能的接口
 */
@Api(tags = "AI助手接口")
@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {

    @Resource
    private IAIService aiService;
    
    @Resource
    private IArticleService articleService;

    /**
     * 根据帖子ID生成AI回复
     * @param request HTTP请求
     * @param articleId 帖子ID
     * @return AI生成的回复内容
     */
    @ApiOperation("AI生成回复")
    @GetMapping("/generateReply")
    public AppResult<String> generateReply(HttpServletRequest request,
                                           @ApiParam("帖子ID") @RequestParam("articleId") Long articleId) {
        // 验证用户是否登录
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AppConfig.USER_SESSION) == null) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        
        // 获取帖子信息
        Article article = articleService.selectById(articleId);
        if (article == null) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        
        // 调用AI服务生成回复
        String reply = aiService.generateReply(article.getTitle(), article.getContent());
        log.info("AI为帖子[{}]生成回复成功", articleId);
        
        return AppResult.success("AI回复生成成功", reply);

    }

    /**
     * 对帖子内容生成摘要
     * @param request HTTP请求
     * @param articleId 帖子ID
     * @return AI生成的摘要
     */
    @ApiOperation("AI生成摘要")
    @GetMapping("/generateSummary")
    public AppResult<String> generateSummary(HttpServletRequest request,
                                             @ApiParam("帖子ID") @RequestParam("articleId") Long articleId) {
        // 验证用户是否登录
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AppConfig.USER_SESSION) == null) {
            return AppResult.failed(ResultCode.FAILED_UNAUTHORIZED);
        }
        
        // 获取帖子信息
        Article article = articleService.selectById(articleId);
        if (article == null) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        
        // 调用AI服务生成摘要
        String summary = aiService.generateSummary(article.getContent());
        log.info("AI为帖子[{}]生成摘要成功", articleId);
        
        return AppResult.success("AI摘要生成成功", summary);


    }
}
