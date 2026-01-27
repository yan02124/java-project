package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.ArticleMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Article;
import com.example.forum.model.Board;
import com.example.forum.model.User;
import com.example.forum.services.IArticleService;
import com.example.forum.services.IBoardService;
import com.example.forum.services.IUserService;
import com.example.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements IArticleService {

    @Resource
    private ArticleMapper articleMapper;

    //用户和板块的操作
    @Resource
    private IUserService userService;

    @Resource
    private IBoardService boardService;

    @Override
    public void create(Article article) {
        //非空校验
        if(article == null || article.getTitle() == null
                || article.getBoardId() == null
                || StringUtil.isEmpty(article.getTitle())
                || StringUtil.isEmpty(article.getContent())){
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //设置默认值
        article.setVisitCount(0); //访问数
        article.setReplyCount(0); //回复数
        article.setLikeCount(0);  //点赞数
        article.setDeleteState((byte)0);
        article.setState((byte)0);
        Date date = new Date();
        article.setCreateTime(date);
        article.setUpdateTime(date);
        //写入数据库
        int articleRow = articleMapper.insertSelective(article);
        if(articleRow <= 0){
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        //获取用户信息
        User user = userService.selectById(article.getUserId());
        //没有找到指定的用户信息
        if(user == null){
            log.warn(ResultCode.FAILED_CREATE.toString() + ",发帖失败,user id = " + article.getUserId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        //更新用户的发帖数
        userService.addOneArticleCountById(user.getId());

        //获取板块信息
        Board board = boardService.selectById(article.getBoardId());
        //是否在数据库中有对应的板块
        if(board == null){
            log.warn(ResultCode.FAILED_CREATE.toString() + ",发帖失败,board id = " + article.getBoardId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        //更新板块中的帖子数量
        boardService.addOneArticleCountById(board.getId());

        //打印日志
        log.info(ResultCode.SUCCESS.toString() + ", user id = " + article.getUserId()
                + ", board id = " + article.getBoardId()
                + ", article id" + article.getId() + "发帖成功");
        //throw new ApplicationException(AppResult.failed("测试事务回滚"));
    }

    @Override
    public List<Article> selectAll() {
        //调用DAO
        List<Article> articles = articleMapper.selectAll();
        //返回结果
        return articles;
    }

    @Override
    public List<Article> selectAllByBoardId(Long boardId) {
        //非空校验
        if(boardId == null || boardId <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //校验板块是否存在
        Board board = boardService.selectById(boardId);
        if(board == null){
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString() + ",board id = " + boardId);
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        //调用DAO,查询
        List<Article> articles = articleMapper.selectAllByBoardId(boardId);
        //返回结果
        return articles;
    }

    @Override
    public List<Article> selectByUserId(Long userId) {
        //非空校验
        if(userId == null || userId <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //校验用户是否存在
        User user = userService.selectById(userId);
        if(user == null){
            //打印日志
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString() + ",userId id = " + userId);
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        //调用DAO
        List<Article> articles = articleMapper.selectByUserId(userId);
        //返回结果
        return articles;
    }

    @Override
    public Article selectDetailById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //调用DAO
        Article article = articleMapper.selectDetailById(id);
        //判断结果是否为空
        if(article == null){
            //打印日志
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        //更新帖子的访问次数
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setVisitCount(article.getVisitCount() + 1);
        //保存到数据库
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1){
            //打印日志
            log.warn(ResultCode.ERROR_SERVICES.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        //更新返回对象的访问次数
        article.setVisitCount(article.getVisitCount() + 1);
        //返回帖子详情
        return article;
    }

    @Override
    public Article selectById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //调用DAO
        Article article = articleMapper.selectByPrimaryKey(id);
        //返回结果
        return article;
    }

    @Override
    public void modify(Long id, String title, String content) {
        //非空校验
        if(id == null || id <= 0|| StringUtil.isEmpty(title)
                || StringUtil.isEmpty(content)) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //构建要更新的帖子对象
        Article updateArticle = new Article();
        updateArticle.setId(id); //id
        updateArticle.setTitle(title); //标题
        updateArticle.setContent(content); // 正文
        updateArticle.setUpdateTime(new Date()); // 更新时间
        // 调用 DAO
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1){
            //打印日志
            log.warn(ResultCode.ERROR_SERVICES.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void thumbsUpById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 获取帖子详情
        Article article = articleMapper.selectByPrimaryKey(id);
        //帖子不存在
        if(article == null || article.getDeleteState() == 1){
            //打印日志
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        //帖子状态异常
        if(article.getState() == 1){
            //打印日志
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        //构造要更新的对象
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setLikeCount(article.getLikeCount() + 1);
        updateArticle.setUpdateTime(new Date());

        //调用DAO
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1){
            //打印日志
            log.warn(ResultCode.ERROR_SERVICES.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void deleteById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //根据Id查询帖子信息
        Article article = articleMapper.selectByPrimaryKey(id);
        if(article == null || article.getDeleteState() == 1){
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString() + ",article id = " + id);
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        //构造一个更新对象
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setDeleteState((byte) 1);
        //调用DAO
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1){
            //打印日志
            log.warn(ResultCode.ERROR_SERVICES.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        //更新板块中的帖子数量
        boardService.subOneArticleCountById(article.getBoardId());
        //更新用户发贴数
        userService.subOneArticleCountById(article.getUserId());
        log.info("删除帖子成功，article id = " + article.getId() + ",user id = " + article.getUserId() + ".");
    }

    @Override
    public void addOneReplyCountById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //获取帖子记录
        Article article = articleMapper.selectByPrimaryKey(id);
        //校验帖子状态
        if(article == null || article.getDeleteState() == 1){
            //打印日志
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        //帖子已封贴
        if(article.getState() == 1){
            //打印日志
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        //构造更新对象
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        //回复数 = 原回复数 + 1
        updateArticle.setReplyCount(article.getReplyCount() + 1);
        updateArticle.setUpdateTime(new Date());
        //执行更新操作
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1){
            //打印日志
            log.warn(ResultCode.ERROR_SERVICES.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public Article selectByNicknameAndTitle(String nickname, String title) {
        if (StringUtil.isEmpty(nickname) || StringUtil.isEmpty(title)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        return articleMapper.selectByNicknameAndTitle(nickname, title);
    }

    /**
     * 根据关键字搜索帖子
     * 搜索范围包括帖子标题和帖子内容
     * @param keyword 搜索关键字
     * @return 匹配的帖子列表
     */
    @Override
    public List<Article> searchByKeyword(String keyword) {
        // 非空校验：如果关键字为空，返回空列表
        if (StringUtil.isEmpty(keyword)) {
            return Collections.emptyList();
        }
        // 调用DAO层执行搜索
        List<Article> articles = articleMapper.selectByKeyword(keyword);
        // 返回搜索结果
        return articles;
    }

    @Override
    public List<Article> advancedSearch(String title, String content, String author,
                                        Long boardId, String startDate, String endDate,
                                        String searchType) {
        return articleMapper.advancedSearch(title, content, author, boardId, 
                                            startDate, endDate, searchType);
    }



}
