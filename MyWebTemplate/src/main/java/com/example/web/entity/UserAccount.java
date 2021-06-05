package com.example.web.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAccount {
    int id;
    String userName;
    String email;
    String password;
}
