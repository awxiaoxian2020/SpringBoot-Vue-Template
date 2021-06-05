package com.example.web.service;

import com.example.web.dao.UserMapper;
import com.example.web.entity.UserAccount;
import com.example.web.entity.UserInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {
    @Resource
    UserMapper dao;
    @Resource
    AuthService auth;

    /**
     * 通过邮箱或用户名获取用户实体
     * @param text 邮箱/用户名
     * @return 用户实体类
     */
    public UserAccount getAccountByNameOrMail(String text){
        return dao.getAccountByNameOrEmail(text);
    }

    /**
     * 获取用户详细信息
     * @param account 账号
     * @return 详细信息
     */
    public UserInfo getDetail(UserAccount account){
        if(account == null) return null;
        return dao.getUserDetailById(account.getId());
    }

    /**
     * 更新数据库用户信息
     * @param info 数据
     * @param email 邮箱
     * @return 是否成功
     */
    public boolean saveUserInfo(UserInfo info, String email){
        return dao.resetNameByEmail(info.getName(), email) > 0 &&
                dao.resetUserInfoByEmail(info, email) > 0;
    }

    /**
     * 通过邮箱重置密码
     * @param password 密码
     * @param email 邮箱
     * @return 是否重置成功
     */
    public boolean resetPasswordByMail(String password, String email){
        return auth.resetPasswordByMail(password, email);
    }
}
