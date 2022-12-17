package com.example.new_hr_system.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.new_hr_system.entity.SalarySystem;
import com.example.new_hr_system.entity.WorkSystem;
import com.example.new_hr_system.service.ifs.SalarySystemService;
import com.example.new_hr_system.vo.SalarySystemReq;
import com.example.new_hr_system.vo.SalarySystemRes;
import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

//@CrossOrigin
@RestController
public class SalarySystemController {
	@Autowired
	private SalarySystemService salarySystemService;

	// -------------------------------------------------
	// ---�j�w�n�J���u�b��
	@PostMapping(value = "/api/salarySystemEmployeeCodeLogin") // �n�J�j�w�K�X<�����޿�> �A�h�����ط|�T�{�O�_���ӭ��u
	public SalarySystemRes employeeCodeLogin(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		httpSession.setAttribute("EmployeeCode", req.getEmployeeCode());
		return new SalarySystemRes("�n�J���\");

	}

	// -------------------------------------------------

	@PostMapping(value = "/api/creatSalarySystem")
	public SalarySystemRes creatSalarySystem(@RequestBody SalarySystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new SalarySystemRes("�п�J�ۤv�����u�s��HttpSession");
//		}
		return salarySystemService.creatSalarySystem(req);
	}

	// -------------------------------------------------

	@PostMapping(value = "/api/updateSalarySystem")
	public SalarySystemRes updateSalarySystem(@RequestBody SalarySystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new SalarySystemRes("�п�J�ۤv�����u�s��HttpSession");
//		}
		return salarySystemService.updateSalarySystem(req);
	}
	// -------------------------------------------------

	@PostMapping(value = "/api/searchSalarySystemForStaff")
	public SalarySystemRes searchSalarySystemForStaff(@RequestBody SalarySystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new SalarySystemRes("�п�J�ۤv�����u�s��HttpSession");
//		}
		return salarySystemService.searchSalarySystemForStaff(req);
	}

	// -------------------------------------------------
	@PostMapping(value = "/api/searchSalarySystemForManager") // �ݭn�K�X
	public SalarySystemRes searchSalarySystemForManager(@RequestBody SalarySystemReq req, HttpSession httpSession) {
//		httpSession.setAttribute("�K�X", req.getPwd());
//		httpSession.setMaxInactiveInterval(3600);// ����3600�� = �@�p��
		return salarySystemService.searchSalarySystemForManager(req);
	}

}
