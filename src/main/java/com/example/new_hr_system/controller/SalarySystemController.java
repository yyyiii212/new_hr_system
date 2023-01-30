package com.example.new_hr_system.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.new_hr_system.service.ifs.SalarySystemService;
import com.example.new_hr_system.vo.SalarySystemReq;
import com.example.new_hr_system.vo.SalarySystemRes;

//@CrossOrigin
@RestController
public class SalarySystemController {
	@Autowired
	private SalarySystemService salarySystemService;

	// -------------------------------------------------

	// ---�j�w�n�J���u�b�� (�i�H�R��)
	@PostMapping(value = "/api/salarySystemEmployeeCodeLogin") // �n�J�j�w�K�X<�����޿�> �A�h�����ط|�T�{�O�_���ӭ��u
	public SalarySystemRes employeeCodeLogin(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		httpSession.setAttribute("EmployeeCode", req.getEmployeeCode());
		return new SalarySystemRes("�n�J���\");

	}

//==========================================================================

	/*--------------------(�D��)�s�W�~�����*/

	@PostMapping(value = "/api/creatSalarySystem")
	public SalarySystemRes creatSalarySystem(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new SalarySystemRes("�й��խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("�п�J�ۤv�����u�s��");
		}
		return salarySystemService.creatSalarySystem(req);
	}

	/*--------------------(�D��)�ק��~�����*/

	@PostMapping(value = "/api/updateSalarySystem")
	public SalarySystemRes updateSalarySystem(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new SalarySystemRes("�й��խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("�п�J�ۤv�����u�s��");
		}
		return salarySystemService.updateSalarySystem(req);
	}

	/*--------------------(���u)�d���~�����*/
	@PostMapping(value = "/api/searchSalarySystemForStaff")
	public SalarySystemRes searchSalarySystemForStaff(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new SalarySystemRes("���խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode())) {
			return new SalarySystemRes("�п�J�ۤv�����u�s��");
		}
		return salarySystemService.searchSalarySystemForStaff(req);
	}

	/*--------------------(�D��)�d���~�����*/
	@PostMapping(value = "/api/searchSalarySystemForManager")
	public SalarySystemRes searchSalarySystemForManager(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new SalarySystemRes("�й��խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("�п�J�ۤv�����u�s��");
		}
		return salarySystemService.searchSalarySystemForManager(req);
	}

}
