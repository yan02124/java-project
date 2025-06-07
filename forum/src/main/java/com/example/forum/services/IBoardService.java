package com.example.forum.services;

import com.example.forum.model.Board;

import java.util.List;

public interface IBoardService {

    /**
     * 查询num条记录
     * @param num 要查询的条数
     * @return
     */
    List<Board> selectByNum (Integer num);

    /**
     * 根据板块Id查询板块信息
     * @param id 板块Id
     * @return
     */
    Board selectById(Long id);

    /**
     * 更新板块中的帖子数量 + 1
     * @param id 板块Id
     */
    void addOneArticleCountById(Long id);

    /**
     * 更新板块中的帖子数量 - 1
     * @param id 板块Id
     */
    void subOneArticleCountById(Long id);

}
