package com.example.new_hr_system.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.new_hr_system.entity.WorkSystem;
import com.example.new_hr_system.service.ifs.WorkSystemService;
import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

//@CrossOrigin
@RestController
public class WorkSystemController {
	@Autowired
	private WorkSystemService workSystemService;

	// ---登出時刪除綁定員工帳號
	@PostMapping(value = "/api/httpSessionEmployeeCodeOut")
	public WorkSystemRes employeeCodeLoginOut(HttpSession httpSession) {
		httpSession.removeAttribute("EmployeeCode");
		return new WorkSystemRes("登出成功");
	}

	// ---綁定登入員工帳號
	@PostMapping(value = "/api/employeeCodeLogin")
	public WorkSystemRes employeeCodeLogin(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		WorkSystemRes res = workSystemService.employeeCodeLogin(req);
		if (res == null) {
			res = new WorkSystemRes();
			res.setMessage("查無該員工");
			return new WorkSystemRes(res.getMessage());
		}
		httpSession.setAttribute("EmployeeCode", req.getEmployeeCode());
		return res;

	}

	// ---打卡上班<v>
	@PostMapping(value = "/api/punchToWork")
	public WorkSystemRes punchToWork(@RequestBody WorkSystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("請輸入自己的員工編號HttpSession");
//		}
		
		return workSystemService.punchToWork(req);
	}

	// ---下班打卡<v>
	@PostMapping(value = "/api/punchToOffWork")
	public WorkSystemRes punchToOffWork(@RequestBody WorkSystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("請輸入自己的員工編號");
//		}
		return workSystemService.punchToOffWork(req);
	}

	// ---給員工的搜尋
	@PostMapping(value = "/api/searchWorkInfoForStaff")
	public WorkSystemRes searchWorkInfoForStaff(@RequestBody WorkSystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("請輸入自己的員工編號");
//		}
		return workSystemService.searchWorkInfoForStaff(req);
	}

	// ---給主管的搜尋
	@PostMapping(value = "/api/searchWorkInfoForManager")
	public WorkSystemRes searchWorkInfoForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.searchWorkInfoForManager(req);
	}

	// ---給主管的刪除 (用於清空數據庫，過滿時會消耗效能)
	@PostMapping(value = "/api/deleteWorkInfoByDateBetween")
	public WorkSystemRes deleteWorkInfoByDateBetween(@RequestBody WorkSystemReq req) {
		return workSystemService.deleteWorkInfoByDateBetween(req);
	}

	// ---給主管的新增曠職行為
	@PostMapping(value = "/api/creatAbsenteeismForManager")
	public WorkSystemRes creatAbsenteeismForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.creatAbsenteeismForManager(req);
	}

	// ---給主管的刪除<曠職>行為
	@PostMapping(value = "/api/deleteAbsenteeismForManager")
	public WorkSystemRes deleteAbsenteeismForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.deleteAbsenteeismForManager(req);
	}

	// ---打印出打卡資訊getWorkInfoListToday
	@PostMapping(value = "/api/getWorkInfoListToday")
	public WorkSystemRes getWorkInfoListToday(@RequestBody WorkSystemReq req) {
		return workSystemService.getWorkInfoListToday(req);
	}

}
