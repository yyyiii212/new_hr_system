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
	
	// ---綁定登入員工帳號 (可以刪掉)
	@PostMapping(value = "/api/salarySystemEmployeeCodeLogin") // 登入綁定密碼<不用邏輯> ，逸翔那�媟|確認是否有該員工
	public SalarySystemRes employeeCodeLogin(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		httpSession.setAttribute("EmployeeCode", req.getEmployeeCode());
		return new SalarySystemRes("登入成功");

	}

//==========================================================================

	/*--------------------(主管)新增薪水資料*/

	@PostMapping(value = "/api/creatSalarySystem")
	public SalarySystemRes creatSalarySystem(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getSalaryEmployeeCode()) || employeeCode == null) {
			return new SalarySystemRes("請輸入自己的員工編號新增薪水資料或者嘗試重新登入");
		}
		return salarySystemService.creatSalarySystem(req);
	}

	/*--------------------(主管)修改薪水資料*/

	@PostMapping(value = "/api/updateSalarySystem")
	public SalarySystemRes updateSalarySystem(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (employeeCode == null || !employeeCodeString.equals(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("請輸入自己的員工編號更新薪水資料或者嘗試重新登入");
		}
		return salarySystemService.updateSalarySystem(req);
	}

	/*--------------------(員工)查詢薪水資料*/
	@PostMapping(value = "/api/searchSalarySystemForStaff")
	public SalarySystemRes searchSalarySystemForStaff(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (employeeCode == null || !employeeCodeString.equals(req.getEmployeeCode())) {
			return new SalarySystemRes("員工請輸入自己的編號查詢資料或者嘗試重新登入");
		}
		return salarySystemService.searchSalarySystemForStaff(req);
	}

	/*--------------------(主管)查詢薪水資料*/
	@PostMapping(value = "/api/searchSalarySystemForManager")
	public SalarySystemRes searchSalarySystemForManager(@RequestBody SalarySystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (employeeCode == null || !employeeCodeString.equals(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("請輸入自己的員工編號查詢或者嘗試重新登入");
		}
		return salarySystemService.searchSalarySystemForManager(req);
	}

}
