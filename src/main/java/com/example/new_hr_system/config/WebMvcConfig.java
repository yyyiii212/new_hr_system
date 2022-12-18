package com.example.new_hr_system.config;

import org.springframework.context.annotation.Configuration;



import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	//用來連接前端與後端(暫存httpsession)所用方法
		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**")
			.allowedOriginPatterns("*")
			.allowCredentials(true)
			.allowedMethods("POST")
			.allowedHeaders("*");
		}

}
