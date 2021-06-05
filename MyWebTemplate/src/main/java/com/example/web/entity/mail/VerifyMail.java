package com.example.web.entity.mail;

public class VerifyMail extends Mail {
    public VerifyMail(String dist, int verifyCode){
        super(dist);
        this.title = "网站注册的验证邮件";
        this.context = "您的验证码为："+verifyCode+"，请勿告诉他人！";
    }
}
