package com.example.new_hr_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	//�Ψӳs���e�ݻP���(�Ȧshttpsession)�ҥΤ�k
		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**")
			.allowedOriginPatterns("*")
			.allowCredentials(true)
			.allowedMethods("POST")
			.allowedHeaders("*");
		}

}
