package com.example.web.config.addition;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * 登陆接口的Swagger文档注册
 */
@Component
public class SwaggerAddition implements ApiListingScannerPlugin {
    /**
     * 实现此方法可手动添加ApiDescriptions
     *
     * @param context - Documentation context that can be used infer documentation context
     * @return List of {@link ApiDescription}
     * @see ApiDescription
     */
    @Override
    public List<ApiDescription> apply(DocumentationContext context) {
        Operation usernamePasswordOperation = new OperationBuilder(new CachingOperationNameGenerator())
                .method(HttpMethod.POST)
                .summary("用户名密码登录")
                .notes("id/password登录")
                .consumes(this.set(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) // 接收参数格式
                .produces(this.set(MediaType.APPLICATION_JSON_VALUE)) // 返回参数格式
                .tags(this.set("登陆验证"))
                .parameters(Arrays.asList(
                        new ParameterBuilder().description("用户名/邮箱")
                                .type(new TypeResolver().resolve(String.class))
                                .name("id")
                                .parameterType("query")
                                .parameterAccess("access")
                                .required(true)
                                .modelRef(new ModelRef("string"))
                                .order(0)
                                .build(),
                        new ParameterBuilder()
                                .description("密码")
                                .type(new TypeResolver().resolve(String.class))
                                .name("password")
                                .parameterType("query")
                                .parameterAccess("access")
                                .required(true)
                                .modelRef(new ModelRef("string"))
                                .order(1)
                                .build(),
                        new ParameterBuilder()
                                .description("记住我")
                                .type(new TypeResolver().resolve(String.class))
                                .name("remember-me")
                                .parameterType("query")
                                .parameterAccess("access")
                                .required(true)
                                .modelRef(new ModelRef("string"))
                                .order(3)
                                .build()
                ))
                .responseMessages(Collections.singleton(
                        new ResponseMessageBuilder().code(200).message("请求成功")
                                .responseModel(new ModelRef(
                                        "ResponsePacket")
                                ).build()))
                .build();
        ApiDescription loginApiDescription = new ApiDescription("login", "/api/auth/login", "登录接口",
                Collections.singletonList(usernamePasswordOperation), false);
        Operation logoutOperation = new OperationBuilder(new CachingOperationNameGenerator())
                .method(HttpMethod.POST)
                .summary("退出当前登陆账户")
                .notes("退出登录")
                .consumes(this.set(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) // 接收参数格式
                .produces(this.set(MediaType.APPLICATION_JSON_VALUE)) // 返回参数格式
                .tags(this.set("登陆验证"))
                .responseMessages(Collections.singleton(
                        new ResponseMessageBuilder().code(200).message("请求成功")
                                .responseModel(new ModelRef(
                                        "ResponsePacket")
                                ).build()))
                .build();
        ApiDescription logoutApiDescription = new ApiDescription("login", "/api/auth/logout", "退出登陆接口",
                Collections.singletonList(logoutOperation), false);
        return Arrays.asList(loginApiDescription, logoutApiDescription);
    }

    /**
     * 是否使用此插件
     *
     * @param documentationType swagger文档类型
     * @return true 启用
     */
    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.SWAGGER_2.equals(documentationType);
    }

    private <T> HashSet<T> set(T t){
        HashSet<T> set = new HashSet<>();
        set.add(t);
        return set;
    }
}
