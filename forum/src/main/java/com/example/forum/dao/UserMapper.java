package com.example.forum.dao;

import com.example.forum.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insert(User row);

    int insertSelective(User row);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User row);

    int updateByPrimaryKey(User row);

    User selectByUserName (@Param("username") String username);
    /**
     * 根据昵称查询用户
     * @param nickname 昵称
     * @return 用户信息
     */
    User selectByNickname(@Param("nickname") String nickname);

    /**
     * 查询所有用户
     * @return 用户列表
     */
    java.util.List<User> selectAll();

}