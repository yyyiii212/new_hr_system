package com.example.new_hr_system.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//跟SPRINGBOOT講說這是一個設定檔
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") //對應路徑 /** =>表示所有
		.allowedOriginPatterns("*") //對照@CrossOrigin (SpringBoot<v2.4>之後要使用有allowedOriginPatterns的 ps.另一個沒有Patterns)
		.allowCredentials(true)
		.allowedMethods("POST")//多個用逗號分隔("post","get") ps.post型式
				.allowedHeaders("*");
	}
	
	//------------------------------------------------
	
	//以下不要 => implements WebMvcConfigurer
	//有自訂義攔截器時(Filter)用

//=> 定義成Bean
	
//	@Bean 
//	public CorsFilter corsFilter() {
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", corsConfig());
//		return new CorsFilter(source);
//		
//	}
//	
//	private CorsConfiguration corsConfig() {
//		CorsConfiguration corsConfiguration = new CorsConfiguration();
//		corsConfiguration.addAllowedOriginPattern("*");
//		corsConfiguration.addAllowedHeader("*");
//		corsConfiguration.addAllowedMethod("*");
//		corsConfiguration.setAllowCredentials(true);
//		return corsConfiguration;
//	}
}
