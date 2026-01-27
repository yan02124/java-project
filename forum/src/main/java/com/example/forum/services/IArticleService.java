package com.example.forum.services;

import com.example.forum.model.Article;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleService {

    /**
     * 发布帖子
     * @param article 要发布的帖子
     */
    @Transactional //当前方法中的执行过程会被事务管理起来
    void create(Article article);

    /**
     * 查询所有帖子列表
     * @return
     */
    List<Article> selectAll();

    /**
     * 根据板块Id查询所有帖子列表
     * @param boardId 板块Id
     * @return
     */
    List<Article> selectAllByBoardId(Long boardId);

    /**
     *  根据用户Id查询帖子列表
     * @param userId 用户Id
     * @return 帖子列表
     */
    List<Article> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据帖子Id查询详情
     * @param id 帖子Id
     * @return 帖子详情
     */
    Article selectDetailById(Long id);

    /**
     * 根据帖子Id查询记录
     * @param id 帖子Id
     * @return
     */
    Article selectById(Long id);

    /**
     * 编辑帖子
     * @param id 帖子
     * @param title 帖子标题
     * @param content 帖子正文
     */
    void modify(Long id,String title,String content);

    /**
     * 点赞帖子
     * @param id 帖子Id
     */
    void thumbsUpById(Long id);

    /**
     * 根据删除帖子
     * @param id 帖子Id
     */
    @Transactional //事务管理
    void deleteById(Long id);

    /**
     * 文章回复数量 + 1
     * @param id 板块Id
     */
    void addOneReplyCountById(Long id);

    /**
     * 根据用户昵称和帖子标题查询帖子
     * @param nickname 用户昵称
     * @param title 帖子标题
     * @return 帖子信息
     */
    Article selectByNicknameAndTitle(String nickname, String title);

    /**
     * 根据关键字搜索帖子
     * 搜索范围包括帖子标题和帖子内容
     * @param keyword 搜索关键字
     * @return 匹配的帖子列表
     */
    List<Article> searchByKeyword(String keyword);

    /**
     * 高级查询
     */
    List<Article> advancedSearch(String title, String content, String author,
                                Long boardId, String startDate, String endDate,
                                String searchType);


}
