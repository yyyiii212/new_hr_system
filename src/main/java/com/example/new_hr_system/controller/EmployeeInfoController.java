package com.example.new_hr_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.new_hr_system.constants.EmployeeInfoRtnCode;
import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.service.ifs.EmployeeInfoService;
import com.example.new_hr_system.vo.EmployeeInfoReq;
import com.example.new_hr_system.vo.EmployeeInfoRes;

public class EmployeeInfoController {
	@Autowired
	private EmployeeInfoService employeeInfoService;

	// �ˬd��J���ȬO�_���T
	public EmployeeInfoRes checkParamEmployeeInfo(EmployeeInfoReq req) {

		// �P�_���L��J�ȡA�S����J�ȴN�^�ǰT��
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
		} else if (req.getLevel() == null || req.getLevel() < 0 || req.getLevel() >= 3) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_LEVEL_REQUIRED.getMessage());
		} else if (req.getSeniority() == null || req.getSeniority() < 0) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_SENIORITY_REQUIRED.getMessage());
		}

		// �P�_�����ҿ�J���T�P�_
		String partternId = "[A-Z][1-2]\\d{8}";
		if (req.getId().matches(partternId) == false) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_ID_REQUIRED.getMessage());
		}

		// �P�_���u�s����J���T�P�_
		String partternCode = "[A-Z]\\d{3}";
		if (req.getId().matches(partternCode) == false) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_CODE_REQUIRED.getMessage());
		}
		
		// �P�_���u�H�c��J���T�P�_
		String partternEmail = "^[A-Za-z0-9]+@gmail.com";
		if (req.getId().matches(partternEmail) == false) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.Employee_CODE_REQUIRED.getMessage());
		}

		return null;
	}

	@PostMapping(value = "/api/create_employee_info")
	public EmployeeInfoRes createEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		EmployeeInfoRes check = checkParamEmployeeInfo(req);
		if (check != null) {
			return check;
		}

		EmployeeInfo employeeInfo = employeeInfoService.createEmployeeInfo(req);

		// ��Ʀ����ƴN�^�ǿ��~�T��
		if (employeeInfo == null) {
			return  new EmployeeInfoRes(EmployeeInfoRtnCode.ERROR.getMessage());
		}

		return new EmployeeInfoRes(employeeInfo,  EmployeeInfoRtnCode.SUCCESSFUL.getMessage());
	}

	@PostMapping(value = "/api/read_employee_info")
	public EmployeeInfoRes readEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		// �P�_���L��J�ȡA�S����J�ȴN�^�ǰT��
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new EmployeeInfoRes();
		}
		EmployeeInfo employeeInfo = employeeInfoService.readEmployeeInfo(req);

		// ��L��ƴN�^�ǿ��~�T��
		if (employeeInfo == null) {
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

		EmployeeInfo employeeInfo = employeeInfoService.updateEmployeeInfo(req);

		// ��L��ƴN�^�ǿ��~�T��
		if (employeeInfo == null) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.ERROR.getMessage());
		}

		return new EmployeeInfoRes(employeeInfo, EmployeeInfoRtnCode.SUCCESSFUL.getMessage());
	}

	@PostMapping(value = "/api/delete_employee_info")
	public EmployeeInfoRes deleteEmployeeInfo(@RequestBody EmployeeInfoReq req) {
		if (!StringUtils.hasText(req.getEmployeeCode())) { // �P�_���L��J�ȡA�S����J�ȴN�^�ǰT��
			return new EmployeeInfoRes();
		}
		EmployeeInfo employeeInfo = employeeInfoService.deleteEmployeeInfo(req);

		// ��L��ƴN�^�ǿ��~�T��
		if (employeeInfo == null) {
			return new EmployeeInfoRes(EmployeeInfoRtnCode.ERROR.getMessage());
		}

		return new EmployeeInfoRes(EmployeeInfoRtnCode.SUCCESSFUL.getMessage());
	}

}
