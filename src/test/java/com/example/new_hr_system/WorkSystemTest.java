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
			workSystems = new WorkSystem(UUID.randomUUID(), "�U�l", LocalDateTime.now(), LocalDateTime.now(), "??", 10);
		}
		System.out.println(workSystems.getEmployeeCode()+"�h��87");
		System.out.println(workSystems.getWorkTime());
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm");
		String timeString = LocalDateTime.now().format(format);
		System.out.println(timeString+"����");
//		LocalDateTime s = LocalDateTime.parse(timeString,format);
//		System.out.println(s+"����");
		String strNow =  now.format(format);
		System.out.println(strNow+"�����r��");
		String ssss =strNow.replace('T', ' ');
		LocalDateTime date22 = LocalDateTime.parse(ssss, format);
		System.out.println(date22+"�r����ɶ�");
	}
}
