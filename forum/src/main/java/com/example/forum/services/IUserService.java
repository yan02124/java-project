package com.example.forum.services;

import com.example.forum.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

/**
 * 用户接口
 */
public interface IUserService {
    /**
     * 创建一个普通用户
     * @param user 用户信息
     */
    void createNormalUser(User user);

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUserName (String username);

    /**
     * 处理用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);

    /**
     * 根据 Id 查询用户信息
     * @param id 用户Id
     * @return  User对象
     */
    User selectById(Long id);

    /**
     * 用户发帖数 + 1
     * @param id 用户Id
     */
    void addOneArticleCountById(Long id);

    /**
     * 用户发帖数 - 1
     * @param id 板块Id
     */
    void subOneArticleCountById(Long id);

    /**
     * 修改个人信息
     * @param user 要更新的对象
     */
    void modifyInfo(User user);

    /**
     * 修改密码
     * @param id 用户Id
     * @param newPassword 新密码
     * @param oldPassword 旧密码
     */
    
    void modifyPassword(Long id, String newPassword,String oldPassword);
    
    /**
     * 禁言用户（管理员）
     * @param id 用户Id
     */
    void setState(Long id, Byte state);

    /**
     * 根据昵称禁言/解禁用户（管理员）
     * @param nickname 用户昵称
     * @param state 状态：0正常 1禁言
     */
    void setStateByNickname(String nickname, Byte state);

    /**
     * 根据ID禁言/解禁用户（管理员）
     * @param id 用户ID
     * @param state 状态：0正常 1禁言
     */
    void setStateById(Long id, Byte state);

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    java.util.List<User> selectAll();


}
