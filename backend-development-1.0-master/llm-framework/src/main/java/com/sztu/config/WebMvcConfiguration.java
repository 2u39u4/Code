package com.sztu.config;

import com.sztu.interceptor.JwtVerifyInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@Slf4j

public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private JwtVerifyInterceptor jwtVerifyInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtVerifyInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/user/**") // 兜底放行 user 模块免登录接口
                .excludePathPatterns("/websocket");
    }

    /**
     * 允许前端 http://localhost:8083 跨域访问后端 8080。
     * 如需放开全部来源可改为 allowedOrigins("*")（不建议在生产环境）。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8083")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
