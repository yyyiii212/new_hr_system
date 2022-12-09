package com.example.new_hr_system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
public class MileTest {

	@Autowired
	private JavaMailSender mailSender;

	@Test
	public void sendSimpleMail(){
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("kennymax22581997@gmail.com");
		//多個收件人可以用ArrayList或HashSet
		message.setTo("mickeychen8507@gmail.com", "a0973038822@gmail.com");
		message.setSubject("主旨：測試測試");
		message.setText("神奇逸翔");

		mailSender.send(message);
	}

}
