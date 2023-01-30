package com.example.new_hr_system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.new_hr_system.entity.WorkSystem;

@SpringBootTest
public class WorkSystemTest {
	@Test
	public void managerPunchWork() {
		WorkSystem workSystems = new WorkSystem();
		if (5 > 1) {
			workSystems = new WorkSystem(UUID.randomUUID(), "猴子", LocalDateTime.now(), LocalDateTime.now(), "??", 10);
		}
		System.out.println(workSystems.getEmployeeCode()+"逸翔87");
		System.out.println(workSystems.getWorkTime());
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm");
		String timeString = LocalDateTime.now().format(format);
		System.out.println(timeString+"哈哈");
//		LocalDateTime s = LocalDateTime.parse(timeString,format);
//		System.out.println(s+"哈哈");
		String strNow =  now.format(format);
		System.out.println(strNow+"日期轉字串");
		String ssss =strNow.replace('T', ' ');
		LocalDateTime date22 = LocalDateTime.parse(ssss, format);
		System.out.println(date22+"字串轉時間");
	}
}
