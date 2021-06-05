package com.example.web.controller.net;

import com.example.web.entity.UserAccount;
import com.example.web.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Component
public class NetUtil {
    @Resource
    UserService service;

    /**
     * 获取用户实体（包含关键信息）
     * @param session 会话
     * @return 用户实体
     */
    public UserAccount getUserAccount(HttpSession session){
        UserAccount account = (UserAccount) session.getAttribute("user");
        if(account == null){
            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails){
                UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                account = service.getAccountByNameOrMail(principal.getUsername());
                session.setAttribute("user", account);
            }
        }
        return account;
    }
}
