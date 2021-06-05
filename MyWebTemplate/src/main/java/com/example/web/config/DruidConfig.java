package com.example.web.config;

import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class DruidConfig {

    @Bean
    public ServletRegistrationBean<StatViewServlet> state(){
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("loginUsername", "admin");
        properties.put("loginPassword", "1234567");
        properties.put("allow", "");
        properties.put("resetEnable", "false");
        bean.setInitParameters(properties);
        return bean;
    }
}
