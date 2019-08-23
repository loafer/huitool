package com.huitool.autoconfigure;

import com.huitool.web.ServletExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * <描述信息>
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({DispatcherServlet.class })
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class WebAutoConfiguration {

    @Bean
    public ServletExceptionHandler servletExceptionHandler(){
        return new ServletExceptionHandler();
    }
}
