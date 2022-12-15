package com.example.new_hr_system.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.new_hr_system.service.ifs.AbsenceSystemService;
import com.example.new_hr_system.vo.AbsenceSystemReq;
import com.example.new_hr_system.vo.AbsenceSystemRes;
import com.example.new_hr_system.vo.AbsenceSystemResList;

//@CrossOrigin
@RestController
public class AbsenceSystemController {
	
	@Autowired
	private AbsenceSystemService absenceSystemService;
	
	//���u�s�W���� 
	@PostMapping(value = "/api/addAbsence")
	public AbsenceSystemRes addAbsence(@RequestBody AbsenceSystemReq req) {
		
		return absenceSystemService.addAbsence(req);
		
	}
	
	//�R������
	@PostMapping(value = "/api/deleteAbsence")
	public AbsenceSystemRes deleteAbsence(@RequestBody AbsenceSystemReq req) {
		
		return absenceSystemService.deleteAbsence(req);
		
	}
	
	//���u��ܦۤv������
	@PostMapping(value = "/api/getAbsenceByEmployeeCode")
	public AbsenceSystemResList getAbsenceByEmployeeCode(HttpSession httpSession) {
		
		return absenceSystemService.getAbsenceByEmployeeCode(httpSession);
		
	}
	
	//�D����ܦP�������u������
	@PostMapping(value = "/api/getAbsenceBySectionAndLevel")
	public AbsenceSystemResList getAbsenceBySectionAndLevel(HttpSession httpSession) {
		
		return absenceSystemService.getAbsenceBySectionAndLevel(httpSession);
		
	}
	
	//�D�ާ�㰲��
	@PostMapping(value = "/api/checkYesOrNo")
	public AbsenceSystemRes checkYesOrNo(@RequestBody AbsenceSystemReq req) {
		
		return absenceSystemService.checkYesOrNo(req);
		
	}
	
	//�P�_���u����(�p��2�����I)
	@PostMapping(value = "/api/checkEmployeeLevel")
	public boolean checkEmployeeLevel(HttpSession httpSession) {
		
		return absenceSystemService.checkEmployeeLevel(httpSession);
		
	}

}