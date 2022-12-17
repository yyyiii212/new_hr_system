package com.example.new_hr_system.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.new_hr_system.constants.EmployeeInfoRtnCode;
import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.service.ifs.EmployeeInfoService;
import com.example.new_hr_system.vo.EmployeeInfoReq;
import com.example.new_hr_system.vo.EmployeeInfoRes;

@RestController
public class EmployeeInfoController {

	
	@Autowired
	private EmployeeInfoService employeeInfoService;

	// 檢查輸入的值是否正確
	public EmployeeInfoRes checkParamEmployeeInfo(EmployeeInfoReq req) {

		// 判斷有無輸入值，沒有輸入值就回傳訊息
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_CODE_REQUIRED.getMessage());
		} else if (!StringUtils.hasText(req.getName())) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_NAME_REQUIRED.getMessage());
		} else if (!StringUtils.hasText(req.getId())) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_ID_REQUIRED.getMessage());
		} else if (!StringUtils.hasText(req.getEmployeeEmail())) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_EMAIL_REQUIRED.getMessage());
		} else if (!StringUtils.hasText(req.getSection())) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_SECTION_REQUIRED.getMessage());
		} else if (!StringUtils.hasText(req.getSituation())) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_SITUATION_REQUIRED.getMessage());
		}

		// 判斷身分證輸入正確與否
		String partternId = "[A-Z][1-2]\\d{8}";
		if (req.getId().matches(partternId) == false) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_ID_REQUIRED.getMessage());
		}

		// 判斷員工信箱輸入正確與否
		String partternEmail = "^[A-Za-z0-9]+@gmail.com";
		if (req.getEmployeeEmail().matches(partternEmail) == false) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_EMAIL_REQUIRED.getMessage());
		}

		return null;
	}
	
	@PostMapping(value = "/api/login_judgment")
	public EmployeeInfoRes loginJudgment(@RequestBody EmployeeInfoReq req, HttpSession httpSession) {
		
		//防呆
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_CODE_REQUIRED.getMessage());
		}else if (!StringUtils.hasText(req.getId())) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_ID_REQUIRED.getMessage());
		}
		String partternCode = "[A-Z]\\d{3}";
		if (req.getEmployeeCode().matches(partternCode) == false) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_CODE_REQUIRED.getMessage());
		}
		String partternId = "[A-Z][1-2]\\d{8}";
		if (req.getId().matches(partternId) == false) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_ID_REQUIRED.getMessage());
		}
		
		//登入畫面(員工編號與身分證)判斷
		EmployeeInfo employeeInfo = employeeInfoService.loginJudgment(req);
		if(employeeInfo == null) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.ERROR.getMessage());
		}
		
		//暫存員工編號
		httpSession.setAttribute("employee_code", req.getEmployeeCode());
		
		return new EmployeeInfoRes(employeeInfo,  EmployeeInfoRtnCode.SUCCESSFUL.getMessage());
	}

	@PostMapping(value = "/api/create_employee_info")
	public EmployeeInfoRes createEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		
		EmployeeInfoRes check = checkParamEmployeeInfo(req);
		if (check != null) {
			return check;
		}
		// 判斷員工編號輸入正確與否
		String partternCode = "\\d{3}";
		if (req.getEmployeeCode().matches(partternCode) == false) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_CODE_REQUIRED.getMessage());
		}

		EmployeeInfo employeeInfo = employeeInfoService.createEmployeeInfo(req);

		// 資料有重複就回傳錯誤訊息
		if (employeeInfo == null) {
			return  new EmployeeInfoRes(EmployeeInfoRtnCode.ERROR.getMessage());
		}

		return new EmployeeInfoRes(employeeInfo,  EmployeeInfoRtnCode.SUCCESSFUL.getMessage());
	}

	@PostMapping(value = "/api/read_employee_info")
	public EmployeeInfoRes readEmployeeInfo(@RequestBody EmployeeInfoReq req) {

		// 找無資料就回傳錯誤訊息
		List<EmployeeInfo> employeeInfoList = employeeInfoService.readEmployeeInfo(req);
		if (employeeInfoList == null) {
			return  new EmployeeInfoRes(EmployeeInfoRtnCode.ERROR.getMessage());
		}
		
		
		return new EmployeeInfoRes(employeeInfoList,  EmployeeInfoRtnCode.SUCCESSFUL.getMessage());
	}
	
	@PostMapping(value = "/api/read_one_employee_info")
	public EmployeeInfoRes readOneEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_CODE_REQUIRED.getMessage());
		} 
		
		EmployeeInfo employeeInfo = employeeInfoService.readOneEmployeeInfo(req);
		if(employeeInfo == null) {
			return  new EmployeeInfoRes(EmployeeInfoRtnCode.ERROR.getMessage());
		}
		
		return new EmployeeInfoRes(employeeInfo,  EmployeeInfoRtnCode.SUCCESSFUL.getMessage());
	}

	@PostMapping(value = "/api/update_employee_info")
	public EmployeeInfoRes updateEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		
		EmployeeInfoRes check = checkParamEmployeeInfo(req);
		if (check != null) {
			return check;
		}
		// 判斷員工編號輸入正確與否
		String partternCode = "[A-Z]\\d{3}";
		if (req.getEmployeeCode().matches(partternCode) == false) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_CODE_REQUIRED.getMessage());
		}

		EmployeeInfo employeeInfo = employeeInfoService.updateEmployeeInfo(req);

		// 找無資料就回傳錯誤訊息
		if (employeeInfo == null) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.ERROR.getMessage());
		}

		return new EmployeeInfoRes(employeeInfo, EmployeeInfoRtnCode.SUCCESSFUL.getMessage());
	}

	@PostMapping(value = "/api/delete_employee_info")
	public EmployeeInfoRes deleteEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		
		// 判斷有無輸入值，沒有輸入值就回傳訊息
		if (!StringUtils.hasText(req.getEmployeeCode())) { 
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_CODE_REQUIRED.getMessage());
		}
		
		String partternCode = "[A-Z]\\d{3}";
		if (req.getEmployeeCode().matches(partternCode) == false) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_CODE_REQUIRED.getMessage());
		}
		
		EmployeeInfo employeeInfo = employeeInfoService.deleteEmployeeInfo(req);

		// 找無資料就回傳錯誤訊息
		if (employeeInfo == null) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.ERROR.getMessage());
		}

		return new EmployeeInfoRes(EmployeeInfoRtnCode.SUCCESSFUL.getMessage());
	}

}
