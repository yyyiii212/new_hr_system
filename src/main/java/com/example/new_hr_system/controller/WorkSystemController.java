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
	// -------------------------------------------------

	// ---登出時刪除綁定員工帳號 (可以刪掉)
	@PostMapping(value = "/api/httpSessionEmployeeCodeOut")
	public WorkSystemRes employeeCodeLoginOut(HttpSession httpSession) {
		httpSession.removeAttribute("EmployeeCode");
		return new WorkSystemRes("登出成功");
	}

	// ---綁定登入員工帳號(可以刪掉)
	@PostMapping(value = "/api/employeeCodeLogin") // 登入綁定密碼<不用邏輯> ，逸翔那裏會確認是否有該員工
	public WorkSystemRes employeeCodeLogin(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		httpSession.setAttribute("EmployeeCode", req.getEmployeeCode());
		return new WorkSystemRes("登入成功");

	}

//==========================================================================	

	/*--------------------(員工)打卡上班*/
	@PostMapping(value = "/api/punchToWork")
	public WorkSystemRes punchToWork(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("嘗試重新登入");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode())) {
			return new WorkSystemRes("請輸入自己的員工編號打卡");
		}
		return workSystemService.punchToWork(req);
	}

	/*--------------------(員工)打卡下班*/
	@PostMapping(value = "/api/punchToOffWork")
	public WorkSystemRes punchToOffWork(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("嘗試重新登入");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode())) {
			return new WorkSystemRes("請輸入自己的員工編號打卡下班");
		}
		return workSystemService.punchToOffWork(req);
	}

	/*--------------------(員工)搜尋打卡資料*/
	@PostMapping(value = "/api/searchWorkInfoForStaff")
	public WorkSystemRes searchWorkInfoForStaff(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("請輸入自己的員工編號搜尋或者嘗試重新登入");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode())) {
			return new WorkSystemRes("請輸入自己的員工編號");
		}
		return workSystemService.searchWorkInfoForStaff(req);
	}

	/*--------------------(主管)搜尋打卡資料*/ //1-2
	@PostMapping(value = "/api/searchWorkInfoForManager")
	public WorkSystemRes searchWorkInfoForManager(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("嘗試重新登入");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("請輸入自己的員工編號");
		}
		return workSystemService.searchWorkInfoForManager(req);
	}

	/*--------------------(大老闆)刪除時間區間的打卡資料*/  //2
	@PostMapping(value = "/api/deleteWorkInfoByDateBetween")
	public WorkSystemRes deleteWorkInfoByDateBetween(@RequestBody WorkSystemReq req) {
		if (!req.getPassword().equals("aaa")) {
			return new WorkSystemRes("密碼錯誤");
		}
		return workSystemService.deleteWorkInfoByDateBetween(req);
	}

	/*--------------------(主管)新增曠職行為*///1-2
	@PostMapping(value = "/api/creatAbsenteeismForManager")
	public WorkSystemRes creatAbsenteeismForManager(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("嘗試重新登入");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("請輸入自己的員工編號");
		}
		return workSystemService.creatAbsenteeismForManager(req);
	}

	/*--------------------(主管)刪除曠職行為*///1-2
	@PostMapping(value = "/api/deleteAbsenteeismForManager")
	public WorkSystemRes deleteAbsenteeismForManager(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("嘗試重新登入");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("請輸入自己的員工編號");
		}
		return workSystemService.deleteAbsenteeismForManager(req);
	}

	/*--------------------(員工)印出打卡資訊給下班打卡用*/
	@PostMapping(value = "/api/getWorkInfoListToday")
	public WorkSystemRes getWorkInfoListToday(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("嘗試重新登入");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode())) {
			return new WorkSystemRes("請輸入自己的員工編號");
		}
		return workSystemService.getWorkInfoListToday(req);
	}

	/*--------------------(主管)印出打卡資訊給刪除曠職行為用*///1-2
	@PostMapping(value = "/api/getWorkInfoListAbsenteeism")
	public WorkSystemRes getWorkInfoListAbsenteeism(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("嘗試重新登入");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("請輸入自己的員工編號");
		}
		return workSystemService.getWorkInfoListAbsenteeism(req);
	}

	/*--------------------(主管)員工沒打卡下班找主管補打下班卡*///2
	@PostMapping(value = "/api/updeateWorkOffTimeForManager")
	public WorkSystemRes updeateWorkOffTimeForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.updeateWorkOffTimeForManager(req);
	}

	/*--------------------(主管)員工沒打卡下班找主管補打卡*///2
	@PostMapping(value = "/api/forgotToPunchCard")
	public WorkSystemRes forgotToPunchCard(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("嘗試重新登入");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("請輸入自己的員工編號");
		}
		return workSystemService.forgotToPunchCard(req);
	}

}
