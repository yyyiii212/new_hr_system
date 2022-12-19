package com.example.new_hr_system;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.new_hr_system.entity.AbsenceSystem;
import com.example.new_hr_system.service.ifs.AbsenceSystemService;
import com.example.new_hr_system.vo.AbsenceSystemReq;
import com.example.new_hr_system.vo.AbsenceSystemRes;
import com.example.new_hr_system.vo.AbsenceSystemResList;

@SpringBootTest
public class AbsenceTest {
	
	@Autowired
	private AbsenceSystemService absenceSystemService;
	
//	@Test
//	public void addAbsenceTest() {
//		AbsenceSystemReq req = new AbsenceSystemReq();
//		LocalDate date = LocalDate.parse("2023-01-01");
//		req.setEmployeeCode("A02");
//		req.setAbsenceReason("公假");
//		req.setAbsenceDate(date);
//		req.setEmail("kennymax22581997@gmail.com");
//		absenceSystemService.addAbsence(req);
//		
//	}
	
	@Test
	public void deleteAbsenceTest() {
		AbsenceSystemReq req = new AbsenceSystemReq();
		req.setUuid("12c65fbc-067e-47b9-b652-e0bd83f15d33");
		absenceSystemService.deleteAbsence(req);
		
	}
	
	@Test
	//session要測的話要做Test Controller
	public void getAbsenceByEmployeeCodeTest(HttpSession httpSession) {
		
		httpSession.setAttribute("employeeCode", "A02");
		
		 AbsenceSystemResList absenceList = absenceSystemService.getAbsenceByEmployeeCode(httpSession);
		 
		 System.out.println(absenceList.getMessage());
		
		for(AbsenceSystem item :  absenceList.getAbsenceSystemList()) {
			System.out.println("=========");
			System.out.println(item.getUuid());
			System.out.println(item.getEmployeeCode());
			System.out.println(item.getAbsenceDate());
			System.out.println(item.getAbsenceReason());
			System.out.println(item.getYesOrNo());
		}
		
		
	}

}
