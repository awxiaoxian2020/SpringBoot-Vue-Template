package com.example.web.service;

import com.alibaba.fastjson.JSONObject;
import com.example.web.dao.UserMapper;
import com.example.web.entity.UserAccount;
import com.example.web.entity.mail.Mail;
import com.example.web.entity.mail.VerifyMail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService implements UserDetailsService {
    @Resource
    UserMapper dao;
    @Value("${spring.mail.username}")
    private String MAIL_SENDER;
    @Resource
    private JavaMailSender mailSender;

    public UserDetails loadUserByUsername(String text) throws UsernameNotFoundException {
        UserAccount entity = dao.getAccountByNameOrEmail(text);
        if(entity == null){
            throw new UsernameNotFoundException(text);
        }else {
            return User.withUsername(entity.getUserName()).password(entity.getPassword()).roles("user").build();
        }
    }

    public void logoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        JSONObject object = new JSONObject();
        object.put("success", true);
        httpServletResponse.getWriter().write(object.toJSONString());
    }

    public void loginSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        JSONObject object = new JSONObject();
        object.put("success", true);
        httpServletResponse.getWriter().write(object.toJSONString());
    }

    public void loginFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        JSONObject object = new JSONObject();
        object.put("success", false);
        httpServletResponse.getWriter().write(object.toJSONString());
    }

    /**
     * 密码强度检车
     * @param pwd 密码
     * @return true表示不通过
     */
    private boolean checkPwdStrength(String pwd){
        String regex = "^.*(?=.{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[\\\\.!@#$%^&*? ]).*$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(pwd);
        return !matcher.matches();
    }

    /**
     * 创建账户
     * @param account 账户
     * @return 是否成功
     */
    public boolean creatNewAccount(UserAccount account){
        try{
            if(checkPwdStrength(account.getPassword())) return false;
            account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));

            return dao.registerBasicAccount(account) > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通过邮箱重置密码
     * @param password 密码
     * @param email 邮箱
     * @return 是否重置成功
     */
    public boolean resetPasswordByMail(String password, String email){
        if(checkPwdStrength(password)) return false;
        return dao.resetPasswordByEmail(new BCryptPasswordEncoder().encode(password), email) > 0;
    }

    /**
     * 通过邮箱重置邮箱
     * @param newMail 新邮箱
     * @param email 邮箱
     * @return 是否重置成功
     */
    public boolean resetMailByMail(String newMail, String email){
        return dao.resetEmailByEmail(newMail, email) > 0;
    }

    //邮件验证请求锁，冷却完成自动开锁，每个Session都会有60秒的邮件发送冷却时间
    List<HttpSession> locker = new CopyOnWriteArrayList<>();

    /**
     * 发送一封带有验证码的验证邮件
     * @param session 当前会话
     * @param dist 目标邮箱
     * @param ignore 是否忽略已注册的账户
     * @return 0 表示仍在冷却，1 表示邮件格式错误，2 表示发送过程中出现异常, 3 表示已经注册，否则返回一串生成的6位数字验证码
     */
    public int sendVerifyEmail(HttpSession session, String dist, boolean ignore){
        if(!locker.contains(session)){
            locker.add(session);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    locker.remove(session);
                }
            }, 60000);
            if(!ignore && dao.getAccountByNameOrEmail(dist) != null) return 3;
            Random random = new Random();
            int code = random.nextInt(899999)+100000;
            VerifyMail mail = new VerifyMail(dist, code);
            int res = this.sendMail(mail);
            if(res == 0){
                return code;
            }else {
                return res;
            }
        }else {
            return 0;
        }
    }

    /**
     * 发送一封邮件
     * @param mail 邮件
     * @return 邮件错误码
     */
    private short sendMail(Mail mail){
        String regex = "^[A-Za-zd0-9]+([-_.][A-Za-zd]+)*@([A-Za-zd0-9]+[-.])+[A-Za-zd]{2,5}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(mail.getDestination());
        if(!matcher.matches()) return 1;
        try{
            SimpleMailMessage mailMessage= new SimpleMailMessage();
            mailMessage.setFrom(MAIL_SENDER);
            mailMessage.setTo(mail.getDestination());
            mailMessage.setSubject(mail.getTitle());
            mailMessage.setText(mail.getContext());
            mailSender.send(mailMessage);
            return 0;
        }catch (MailException e){
            e.printStackTrace();
            return 2;
        }
    }
}
