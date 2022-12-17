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
	@PostMapping(value = "/api/employeeCodeLogin")//登入綁定密碼<不用邏輯> ，逸翔那裏會確認是否有該員工
	public WorkSystemRes employeeCodeLogin(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		httpSession.setAttribute("EmployeeCode", req.getEmployeeCode());
		return new WorkSystemRes("登入成功");

	}

	// ---打卡上班
	@PostMapping(value = "/api/punchToWork")
	public WorkSystemRes punchToWork(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("EmployeeCode");
		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
			return new WorkSystemRes("請輸入自己的員工編號HttpSession");
		}

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
	public WorkSystemRes searchWorkInfoForManager(@RequestBody WorkSystemReq req,HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getManagerEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("請輸入自己的主管編號");
//		}
		return workSystemService.searchWorkInfoForManager(req,httpSession);
	}

	// ---給最大的boss刪除 (用於清空數據庫，過滿時會消耗效能)<求密碼>
	@PostMapping(value = "/api/deleteWorkInfoByDateBetween")
	public WorkSystemRes deleteWorkInfoByDateBetween(@RequestBody WorkSystemReq req) {
//		if(!req.getPassword().equals("aaa")) {
//			return new WorkSystemRes("密碼錯誤");
//		}
		return workSystemService.deleteWorkInfoByDateBetween(req);
	}

	// ---給主管的新增曠職行為
	@PostMapping(value = "/api/creatAbsenteeismForManager")
	public WorkSystemRes creatAbsenteeismForManager(@RequestBody WorkSystemReq req,HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getManagerEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("請輸入自己的主管編號");
//		}
		return workSystemService.creatAbsenteeismForManager(req);
	}

	// ---給主管的刪除<曠職>行為
	@PostMapping(value = "/api/deleteAbsenteeismForManager")
	public WorkSystemRes deleteAbsenteeismForManager(@RequestBody WorkSystemReq req,HttpSession httpSession) {
//		httpSession.setAttribute("EmployeeCode", "a99");//登入
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getManagerEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("請輸入自己的主管編號");
//		}
		return workSystemService.deleteAbsenteeismForManager(req);
	}

	// ---打印出打卡資訊getWorkInfoListToday (打卡下班)
	@PostMapping(value = "/api/getWorkInfoListToday")
	public WorkSystemRes getWorkInfoListToday(@RequestBody WorkSystemReq req) {
		return workSystemService.getWorkInfoListToday(req);
	}

	// ---打印出打卡資訊getWorkInfoListAbsenteeism (刪除曠職)
	@PostMapping(value = "/api/getWorkInfoListAbsenteeism")
	public WorkSystemRes getWorkInfoListAbsenteeism(@RequestBody WorkSystemReq req,HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getManagerEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("請輸入自己的主管編號");
//		}
		return workSystemService.getWorkInfoListAbsenteeism(req);
	}

}
