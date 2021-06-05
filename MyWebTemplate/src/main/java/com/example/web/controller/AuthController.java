package com.example.web.controller;

import com.example.web.controller.net.NetUtil;
import com.example.web.controller.net.ResponsePacket;
import com.example.web.entity.UserAccount;
import com.example.web.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Timer;
import java.util.TimerTask;

@Api(value = "包含注册和重置密码",tags = "账户操作接口")
@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    AuthService service;
    @Resource
    NetUtil util;

    /**
     * 通过邮件方式发送验证码，验证码有效时间只有5分钟。
     * @param session 会话
     * @param mail 目的邮箱
     * @return 发送结果
     */
    @ApiOperation("通过邮件发送敏感操作验证码")
    @RequestMapping(value = "/mail-send-code", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePacket mailRegCode(@ApiIgnore HttpSession session,
                                  @ApiParam("目的电子邮件地址") @RequestParam("email") String mail,
                                  @ApiParam("需要进行的敏感操作类型" +
                                          "（register, reset-password, reset-email）") @RequestParam("type") String type){
        int code = service.sendVerifyEmail(session, mail, !type.equals("register"));
        ResponsePacket packet = new ResponsePacket();
        switch (code){
            case 0:
                packet.msg("您的请求频率太快，请稍后再试！");
                break;
            case 1:
                packet.msg("邮件格式有误，请重新输入！");
                break;
            case 2:
                packet.msg("无法发送邮件到指定邮箱，如有问题请联系管理员！");
                break;
            case 3:
                packet.msg("该邮箱已被注册！");
                break;
            default:
                packet.success();
                packet.msg("注册邮件发送成功，请查收！有效期5分钟。");
                session.setAttribute("ver-code", code);
                session.setAttribute("type", type);
                session.setAttribute("email", mail);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Object ver = session.getAttribute("ver-code");
                        if(ver != null && ver.equals(code)) session.removeAttribute("ver-code");
                    }
                }, 300000);
                break;
           }
           return packet;
    }

    /**
     * 测试验证码是否正确（需要先请求验证码生成session属性），若正确则可以进行敏感操作。
      * @param session 会话
     * @param code 验证码
     * @return 验证结果
     */
    @ApiOperation("验证当前的敏感操作验证码")
    @RequestMapping(value = "/ver-code", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePacket verCode(@ApiIgnore HttpSession session,
                              @ApiParam("敏感操作验证码") @RequestParam("code") String code){
        ResponsePacket packet = new ResponsePacket();
        Object ver = session.getAttribute("ver-code");
        if(ver == null) {
            packet.msg("状态异常，请重试！");
        }else {
            if(ver.toString().equals(code)){
                packet.success();
                packet.msg("验证成功！");
                session.removeAttribute("ver-code");
                session.setAttribute("ver", true);
            }else {
                packet.msg("验证码错误！请重新填写。");
            }
        }
        return packet;
    }

    /**
     * 执行注册操作（需要提前进行验证码通过）
     * @param session 会话
     * @param name 用户名
     * @param password 密码
     * @return 注册结果
     */
    @ApiOperation("进行注册操作（需要提前进行敏感验证码验证）")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePacket register(@ApiIgnore HttpSession session,
                               @ApiParam("用户名") @RequestParam("name") String name,
                               @ApiParam("密码") @RequestParam("password") String password){
        ResponsePacket packet = new ResponsePacket();
        String type = (String) session.getAttribute("type");
        if(type == null || !type.equals("register")) {
            packet.msg("状态异常，不应该进行注册操作！");
        }else {
            Boolean ver = (Boolean) session.getAttribute("ver");
            if(ver == null) {
                packet.msg("验证失败，请重新进行敏感验证！");
            }else {
                String email = (String) session.getAttribute("email");
                UserAccount account = null;
                if(email != null){
                    account = new UserAccount(0, name, password, email);
                }
                if(account != null && service.creatNewAccount(account)){
                    packet.success();
                    packet.msg("注册成功！");
                }else {
                    packet.msg("创建账号失败，请重试！");
                }
                session.removeAttribute("type");
                session.removeAttribute("ver");
                session.removeAttribute("email");
            }
        }
        return packet;
    }

    @ApiOperation("进行密码重置操作（需要提前进行敏感验证码验证）")
    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePacket resetPassword(@ApiIgnore HttpSession session,
                                        @ApiParam("密码") @RequestParam("password") String password){
        ResponsePacket packet = new ResponsePacket();
        String type = (String) session.getAttribute("type");
        if(type == null || !type.equals("reset-password")) {
            packet.msg("状态异常，不应该进行密码重置操作！");
        }else {
            Boolean ver = (Boolean) session.getAttribute("ver");
            if(ver == null) {
                packet.msg("验证失败，请重新进行敏感验证！");
            }else {
                String email = (String) session.getAttribute("email");
                if(email != null){
                    if(service.resetPasswordByMail(password, email)){
                        packet.success();
                        packet.msg("密码重置成功！");
                    }else {
                        packet.msg("密码修改失败，请重试！");
                    }
                    session.removeAttribute("type");
                    session.removeAttribute("ver");
                    session.removeAttribute("email");
                }
            }
        }
        return packet;
    }

    /**
     * 邮箱重置操作要求用户在登陆状态下进行（同样需要进行敏感操作验证）
     * @param session 会话
     * @return 重置结果
     */
    @ApiOperation("进行邮箱重置操作（需要提前进行敏感验证码验证）")
    @RequestMapping(value = "/reset-email", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePacket resetEmail(@ApiIgnore HttpSession session){
        ResponsePacket packet = new ResponsePacket("需要在登陆状态下进行邮件重置操作！");
        UserAccount account = util.getUserAccount(session);
        if(account == null) return packet;
        String type = (String) session.getAttribute("type");
        if(type == null || !type.equals("reset-email")) {
            packet.msg("状态异常，不应该进行密码重置操作！");
        }else {
            Boolean ver = (Boolean) session.getAttribute("ver");
            if(ver == null) {
                packet.msg("验证失败，请重新进行敏感验证！");
            }else {
                String email = (String) session.getAttribute("email");
                if(email != null){
                    if(service.resetMailByMail(email, account.getEmail())){
                        packet.success();
                        packet.msg("邮箱重置成功！");
                        account.setEmail(email);
                    }else {
                        packet.msg("邮箱修改失败，请重试！");
                    }
                    session.removeAttribute("type");
                    session.removeAttribute("ver");
                    session.removeAttribute("email");
                }
            }
        }
        return packet;
    }
}
