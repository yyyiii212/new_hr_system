package com.example.new_hr_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.new_hr_system.constants.HrSystemRtnCode;
import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.service.ifs.HrSystemService;
import com.example.new_hr_system.vo.EmployeeInfoReq;
import com.example.new_hr_system.vo.EmployeeInfoRes;

@RestController
public class HrSystemController {

	@Autowired
	private HrSystemService hrSystemService;

	// 檢查輸入的值是否正確
	public EmployeeInfoRes checkParamEmployeeInfo(EmployeeInfoReq req) {

		// 判斷有無輸入值，沒有輸入值就回傳訊息
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new EmployeeInfoRes(HrSystemRtnCode.Employee_CODE.getMessage());
		} else if (!StringUtils.hasText(req.getName())) {
			return new EmployeeInfoRes(HrSystemRtnCode.Employee_NAME.getMessage());
		} else if (!StringUtils.hasText(req.getId())) {
			return new EmployeeInfoRes(HrSystemRtnCode.Employee_ID.getMessage());
		} else if (!StringUtils.hasText(req.getEmployeeEmail())) {
			return new EmployeeInfoRes(HrSystemRtnCode.Employee_EMAIL.getMessage());
		} else if (!StringUtils.hasText(req.getSection())) {
			return new EmployeeInfoRes(HrSystemRtnCode.Employee_SECTION.getMessage());
		} else if (!StringUtils.hasText(req.getSituation())) {
			return new EmployeeInfoRes(HrSystemRtnCode.Employee_SITUATION.getMessage());
		} else if (req.getLevel() == null || req.getLevel() < 0 || req.getLevel() >= 3) {
			return new EmployeeInfoRes(HrSystemRtnCode.Employee_LEVEL.getMessage());
		} else if (req.getSeniority() == null || req.getSeniority() < 0) {
			return new EmployeeInfoRes(HrSystemRtnCode.Employee_SENIORITY.getMessage());
		}

		// 判斷身分證輸入正確與否
		String parttern = "[A-Z][1-2]\\d{8}";
		if (req.getId().matches(parttern) == false) {
			return new EmployeeInfoRes(HrSystemRtnCode.Employee_ID.getMessage());
		}

		// 判斷員工編號輸入正確與否
		String parttern1 = "[A-Z]\\d{3}";
		if (req.getId().matches(parttern1) == false) {
			return new EmployeeInfoRes(HrSystemRtnCode.Employee_CODE.getMessage());
		}

		return null;
	}

	@PostMapping(value = "/api/create_employee_info")
	public EmployeeInfoRes createEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		EmployeeInfoRes check = checkParamEmployeeInfo(req);
		if (check != null) {
			return check;
		}

		EmployeeInfo employeeInfo = hrSystemService.createEmployeeInfo(req);

		// 資料有重複就回傳錯誤訊息
		if (employeeInfo == null) {
			return  new EmployeeInfoRes(HrSystemRtnCode.ERROR.getMessage());
		}

		return new EmployeeInfoRes(employeeInfo,  HrSystemRtnCode.SUCCESSFUL.getMessage());
	}

	@PostMapping(value = "/api/read_employee_info")
	public EmployeeInfoRes readEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		// 判斷有無輸入值，沒有輸入值就回傳訊息
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new EmployeeInfoRes();
		}
		EmployeeInfo employeeInfo = hrSystemService.readEmployeeInfo(req);

		// 找無資料就回傳錯誤訊息
		if (employeeInfo == null) {
			return  new EmployeeInfoRes(HrSystemRtnCode.ERROR.getMessage());
		}

		return new EmployeeInfoRes(employeeInfo,  HrSystemRtnCode.SUCCESSFUL.getMessage());
	}

	@PostMapping(value = "/api/update_employee_info")
	public EmployeeInfoRes updateEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		EmployeeInfoRes check = checkParamEmployeeInfo(req);
		if (check != null) {
			return check;
		}

		EmployeeInfo employeeInfo = hrSystemService.updateEmployeeInfo(req);

		// 找無資料就回傳錯誤訊息
		if (employeeInfo == null) {
			return new EmployeeInfoRes(HrSystemRtnCode.ERROR.getMessage());
		}

		return new EmployeeInfoRes(employeeInfo, HrSystemRtnCode.SUCCESSFUL.getMessage());
	}

	@PostMapping(value = "/api/delete_employee_info")
	public EmployeeInfoRes deleteEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		if (!StringUtils.hasText(req.getEmployeeCode())) { // 判斷有無輸入值，沒有輸入值就回傳訊息
			return new EmployeeInfoRes();
		}
		EmployeeInfo employeeInfo = hrSystemService.deleteEmployeeInfo(req);

		// 找無資料就回傳錯誤訊息
		if (employeeInfo == null) {
			return new EmployeeInfoRes(HrSystemRtnCode.ERROR.getMessage());
		}

		return new EmployeeInfoRes(HrSystemRtnCode.SUCCESSFUL.getMessage());
	}

}
