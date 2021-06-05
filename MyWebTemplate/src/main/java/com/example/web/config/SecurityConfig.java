package com.example.web.config;

import com.example.web.controller.net.ResponsePacket;
import com.example.web.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    DataSource dataSource;

    @Resource
    AuthService service;

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/auth/**", "/druid/**").permitAll()
                .antMatchers("/**").hasAnyRole("user").and()
            .formLogin()
                .loginProcessingUrl("/api/auth/login")
                .usernameParameter("id")
                .passwordParameter("password")
                .successHandler(service::loginSuccess)
                .failureHandler(service::loginFailure).and()
            .rememberMe()
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(3600)
                .userDetailsService(service).and()
            .logout()
                .logoutSuccessHandler(service::logoutSuccess)
                .logoutUrl("/api/auth/logout").and()
            .csrf()
                .ignoringAntMatchers("/druid/**", "/api/**").and()
            .cors()
                .configurationSource(corsConfigurationSource()).and()
            .exceptionHandling()
                .accessDeniedHandler(this::accessDeniedHandler)
                .authenticationEntryPoint(this::accessDeniedHandler);
    }

    /**
     * 未验证的情况下，返回未验证JSON信息
     * @param request 请求
     * @param resp 返回
     * @param e 异常类型
     * @throws IOException 外部处理
     */
    private void accessDeniedHandler(HttpServletRequest request, HttpServletResponse resp, Exception e) throws IOException{
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        ResponsePacket packet = new ResponsePacket();
        packet.msg("未验证，请先进行登陆操作！");
        out.write(new ObjectMapper().writeValueAsString(packet));
        out.flush();
        out.close();
    }

    //许可的跨域请求地址
    @Value("${server.cors}")
    private String origin;

    /**
     * Vue+SpringBoot 前后端分离跨域请求支持
     * @return 跨域设置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration=new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedOrigins(Collections.singletonList(origin));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        //所有的请求都允许跨域
        source.registerCorsConfiguration("/api/**",corsConfiguration);
        return  source;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
    }
}
