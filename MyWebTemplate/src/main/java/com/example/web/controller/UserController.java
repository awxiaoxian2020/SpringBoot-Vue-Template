package com.example.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.web.controller.net.NetUtil;
import com.example.web.controller.net.ResponsePacket;
import com.example.web.entity.UserAccount;
import com.example.web.entity.UserInfo;
import com.example.web.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@Api(value = "获取用户信息",tags = "用户信息接口")
@Controller
@RequestMapping("/api/user")
public class UserController {

    @Resource
    UserService service;
    @Resource
    NetUtil util;

    /**
     * 获取用户信息，包含用户的名称、邮箱等信息。
     * @param session 会话
     * @return 用户信息
     */
    @ApiOperation("获取当前登陆用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public UserInfo info(@ApiIgnore HttpSession session){
        UserAccount account = util.getUserAccount(session);
        return service.getDetail(account);
    }

    @Value("${server.images}")
    String imageTempPath;

    /**
     * 上传用户头像、背景，图片会保存在指定文件夹中。
     * @param session 会话
     * @param type 类型
     * @param file 文件
     * @return 结果
     */
    @ApiOperation("上传用户图片信息（头像、背景）")
    @RequestMapping(value = "/upload/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePacket uploadImg(@ApiIgnore HttpSession session,
                                    @ApiParam("图片类型（header/background）") @PathVariable("type") String type,
                                    @ApiParam("图片文件") @RequestParam(value = "file") MultipartFile file){
        ResponsePacket packet = new ResponsePacket();
        if (file.isEmpty()){
            packet.msg("错误：文件为空！");
            return packet;
        }
        if (!type.equals("header") && !type.equals("background")) {
            packet.msg("上传类型错误！");
            return packet;
        }
        if(file.getSize() > 1024 * 512){
            packet.msg("文件大小超过512K，无法上传！");
            return packet;
        }
        String fileName = file.getOriginalFilename();
        if(fileName == null){
            packet.msg("文件名称不能为空！");
            return packet;
        }
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        if(suffixName.equals(".png") || suffixName.equals(".jpg")){
            UserAccount account = util.getUserAccount(session);
            String filePath = imageTempPath+account.getId()+"/";
            fileName = type + suffixName;
            File dest = new File(filePath + fileName);
            if (dest.getParentFile().exists() || dest.getParentFile().mkdirs()) {
                try {
                    file.transferTo(dest);
                    packet.success();
                    packet.msg("上传成功！");
                } catch (IOException e) {
                    packet.msg("内部错误，无法完成上传操作！");
                    e.printStackTrace();
                }
            }
        }else {
            packet.msg("文件格式错误，只能为png或jpg类型！");
        }
        return packet;
    }

    /**
     * 获取用户头像、背景图片
     * @param response 返回的结果
     * @param session 会话
     * @param type 图片类型
     */
    @ApiOperation("获取用户的图片（头像、背景）")
    @RequestMapping(value = "/images/{type}", method = RequestMethod.GET)
    @ResponseBody
    public void userImage(@ApiIgnore HttpServletResponse response,
                          @ApiIgnore HttpSession session,
                          @ApiParam("图片类型（header/background）") @PathVariable("type") String type){
        UserAccount account = util.getUserAccount(session);
        String filePath = imageTempPath+account.getId()+"/";
        File dir = new File(filePath);
        if(dir.exists()){
            File[] files = dir.listFiles();
            if(files == null) return;
            for(File file : files){
                if(file.getName().contains(type)){
                    try {
                        String suffix = file.getName().split("\\.")[1];
                        response.setContentType("image/png");
                        OutputStream os = response.getOutputStream();
                        BufferedImage image = ImageIO.read(file);
                        ImageIO.write(image, suffix, os);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @ApiOperation("保存新的用户信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePacket saveUserInfo(@ApiIgnore HttpSession session,
                                       @ApiParam("用户信息数据") @RequestBody JSONObject data){
        UserAccount account = util.getUserAccount(session);
        ResponsePacket packet = new ResponsePacket();
        UserInfo info = new UserInfo();
        info.setName(data.getString("name"));
        info.setNote(data.getString("note"));
        info.setPhone(data.getString("phone"));
        if(service.saveUserInfo(info, account.getEmail())){
            packet.success();
            packet.msg("保存成功！");
        }else {
            packet.msg("内部错误，无法完成保存！");
        }
        return packet;
    }

    @ApiOperation("登陆状态下快速重置密码（只需正确输入原密码即可）")
    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePacket resetPassword(@ApiIgnore HttpSession session,
                                        @ApiParam("旧密码") @RequestParam("password_old") String old,
                                        @ApiParam("新密码") @RequestParam("password") String pwd){
        UserAccount account = util.getUserAccount(session);
        ResponsePacket packet = new ResponsePacket();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(encoder.matches(old, account.getPassword())){
            if(service.resetPasswordByMail(pwd, account.getEmail())){
                packet.success();
                packet.msg("密码重置成功！");
            }else {
                packet.msg("未知错误，无法完成密码重置成操作！");
            }
        }else {
            packet.msg("原密码输入错误，请重新输入！");
        }
        return packet;
    }
}
