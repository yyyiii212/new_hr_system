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
	// ---綁定登入員工帳號
	@PostMapping(value = "/api/salarySystemEmployeeCodeLogin") // 登入綁定密碼<不用邏輯> ，逸翔那裏會確認是否有該員工
	public SalarySystemRes employeeCodeLogin(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		httpSession.setAttribute("EmployeeCode", req.getEmployeeCode());
		return new SalarySystemRes("登入成功");

	}

	// -------------------------------------------------

	@PostMapping(value = "/api/creatSalarySystem")
	public SalarySystemRes creatSalarySystem(@RequestBody SalarySystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new SalarySystemRes("請輸入自己的員工編號HttpSession");
//		}
		return salarySystemService.creatSalarySystem(req);
	}

	// -------------------------------------------------

	@PostMapping(value = "/api/updateSalarySystem")
	public SalarySystemRes updateSalarySystem(@RequestBody SalarySystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new SalarySystemRes("請輸入自己的員工編號HttpSession");
//		}
		return salarySystemService.updateSalarySystem(req);
	}
	// -------------------------------------------------

	@PostMapping(value = "/api/searchSalarySystemForStaff")
	public SalarySystemRes searchSalarySystemForStaff(@RequestBody SalarySystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new SalarySystemRes("請輸入自己的員工編號HttpSession");
//		}
		return salarySystemService.searchSalarySystemForStaff(req);
	}

	// -------------------------------------------------
	@PostMapping(value = "/api/searchSalarySystemForManager") // 需要密碼
	public SalarySystemRes searchSalarySystemForManager(@RequestBody SalarySystemReq req, HttpSession httpSession) {
//		httpSession.setAttribute("密碼", req.getPwd());
//		httpSession.setMaxInactiveInterval(3600);// 限時3600秒 = 一小時
		return salarySystemService.searchSalarySystemForManager(req);
	}

}
