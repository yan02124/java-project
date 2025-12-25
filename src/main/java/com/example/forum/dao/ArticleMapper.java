package com.example.forum.dao;

import com.example.forum.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    int insert(Article row);

    int insertSelective(Article row);

    Article selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Article row);

    int updateByPrimaryKeyWithBLOBs(Article row);

    int updateByPrimaryKey(Article row);

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
    List<Article> selectAllByBoardId(@Param("boardId") Long boardId);


    /**
     * 根据帖子Id查询详情
     * @param id 帖子Id
     * @return 帖子详情
     */
    Article selectDetailById(@Param("id") Long id);

    /**
     *  根据用户Id查询帖子列表
     * @param userId 用户Id
     * @return 帖子列表
     */
    List<Article> selectByUserId(@Param("userId") Long userId);
}
