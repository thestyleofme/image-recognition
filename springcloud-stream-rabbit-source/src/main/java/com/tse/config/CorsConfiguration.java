package com.tse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @description: 跨域资源共享配置
 * @author: xl
 * @create: 2017-10-22 04:53:28
 **/
@Configuration
public class CorsConfiguration extends WebMvcConfigurerAdapter{

    /**
     * @description: 跨域资源共享配置
     * @param:  [registry]
     * @retrun: void
     * @author: xielei
     * @create: 2018-4-8 01:37:22
     *
     **/
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }
}
