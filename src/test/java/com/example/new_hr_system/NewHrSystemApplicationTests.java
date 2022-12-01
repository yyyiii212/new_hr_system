package com.example.new_hr_system;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.respository.EmployeeInfoDao;
import com.example.new_hr_system.service.ifs.HrSystemService;
import com.example.new_hr_system.vo.EmployeeInfoReq;

@WebAppConfiguration
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NewHrSystemApplicationTests {
	
	@Autowired
	private HrSystemService hrSystemService;
	
	@Autowired
	private EmployeeInfoDao employeeInfoDao;
	@Test
	public void createEmployeeInfoTest() {// test新增功能
		
		EmployeeInfoReq employeeInfo = new EmployeeInfoReq("A001", "josh", "E123465987", "d1234567@gmail.com", "資訊", 0,
				1, "正常");
		Assert.isTrue(employeeInfo.getEmployeeCode() != null, "Code cannot be null !!");
		Assert.isTrue(employeeInfo.getName() != null, "Name cannot be null !!");
		Assert.isTrue(employeeInfo.getId() != null, "Id cannot be null !!");
		Assert.isTrue(employeeInfo.getEmployeeEmail() != null, "EmployeeEmail cannot be null !!");
		Assert.isTrue(employeeInfo.getSection() != null, "Section cannot be null !!");
		Assert.isTrue(employeeInfo.getLevel() < 3 || employeeInfo.getLevel() >= 0, "Level cannot be null !!");
		Assert.isTrue(employeeInfo.getSeniority() >= 0, "Seniority cannot be null !!");
		Assert.isTrue(employeeInfo.getSituation() != null, "Situation cannot be null !!");
		
		hrSystemService.createEmployeeInfo(employeeInfo);
		System.out.println(employeeInfo.toString());
		employeeInfoDao.deleteById(employeeInfo.getEmployeeCode());
	}
	
	@Test
	public void readEmployeeInfoTest() {// test搜尋功能
		
		EmployeeInfo employeeInfo= employeeInfoDao.findById("A001").get();
		System.out.println(employeeInfo.getEmployeeCode()+"  "+employeeInfo.getJoinTime()+"  "+employeeInfo.getEmployeeEmail());
	}
	
	@Test
	public void updateEmployeeInfo() {// test修改功能
		
		EmployeeInfoReq employeeInfo =  new EmployeeInfoReq("A001", "josh", "E123465987", "D1567@gmail.com", "資訊", 0,
				1, "正常");
		hrSystemService.updateEmployeeInfo(employeeInfo);
		
		System.out.println(employeeInfo.getEmployeeCode()+"  "+employeeInfo.getEmployeeEmail());
	}
	
	@Test
	public void deleteEmployeeInfo() { // test刪除功能
		EmployeeInfoReq employeeInfo =  new EmployeeInfoReq();
		employeeInfo.setEmployeeCode("A001");
		
		hrSystemService.deleteEmployeeInfo(employeeInfo);
	}
}
