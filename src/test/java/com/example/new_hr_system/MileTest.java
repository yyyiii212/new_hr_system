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
		//�h�Ӧ���H�i�H��ArrayList��HashSet
		message.setTo("mickeychen8507@gmail.com", "a0973038822@gmail.com");
		message.setSubject("�D���G���մ���");
		message.setText("���_�h��");

		mailSender.send(message);
	}

}
