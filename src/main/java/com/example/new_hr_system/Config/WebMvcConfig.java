package com.example.new_hr_system.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//��SPRINGBOOT�����o�O�@�ӳ]�w��
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") //�������| /** =>��ܩҦ�
		.allowedOriginPatterns("*") //���@CrossOrigin (SpringBoot<v2.4>����n�ϥΦ�allowedOriginPatterns�� ps.�t�@�ӨS��Patterns)
		.allowCredentials(true)
		.allowedMethods("POST")//�h�ӥγr�����j("post","get") ps.post����
				.allowedHeaders("*");
	}
	
	//------------------------------------------------
	
	//�H�U���n => implements WebMvcConfigurer
	//���ۭq�q�d�I����(Filter)��

//=> �w�q��Bean
	
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
