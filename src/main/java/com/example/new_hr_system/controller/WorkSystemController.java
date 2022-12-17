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

	// ---�n�X�ɧR���j�w���u�b��
	@PostMapping(value = "/api/httpSessionEmployeeCodeOut")
	public WorkSystemRes employeeCodeLoginOut(HttpSession httpSession) {
		httpSession.removeAttribute("EmployeeCode");
		return new WorkSystemRes("�n�X���\");
	}

	// ---�j�w�n�J���u�b��
	@PostMapping(value = "/api/employeeCodeLogin")//�n�J�j�w�K�X<�����޿�> �A�h�����ط|�T�{�O�_���ӭ��u
	public WorkSystemRes employeeCodeLogin(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		httpSession.setAttribute("EmployeeCode", req.getEmployeeCode());
		return new WorkSystemRes("�n�J���\");

	}

	// ---���d�W�Z
	@PostMapping(value = "/api/punchToWork")
	public WorkSystemRes punchToWork(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("EmployeeCode");
		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
			return new WorkSystemRes("�п�J�ۤv�����u�s��HttpSession");
		}

		return workSystemService.punchToWork(req);
	}

	// ---�U�Z���d<v>
	@PostMapping(value = "/api/punchToOffWork")
	public WorkSystemRes punchToOffWork(@RequestBody WorkSystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("�п�J�ۤv�����u�s��");
//		}
		return workSystemService.punchToOffWork(req);
	}

	// ---�����u���j�M
	@PostMapping(value = "/api/searchWorkInfoForStaff")
	public WorkSystemRes searchWorkInfoForStaff(@RequestBody WorkSystemReq req, HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("�п�J�ۤv�����u�s��");
//		}
		return workSystemService.searchWorkInfoForStaff(req);
	}

	// ---���D�ު��j�M
	@PostMapping(value = "/api/searchWorkInfoForManager")
	public WorkSystemRes searchWorkInfoForManager(@RequestBody WorkSystemReq req,HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getManagerEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("�п�J�ۤv���D�޽s��");
//		}
		return workSystemService.searchWorkInfoForManager(req,httpSession);
	}

	// ---���̤j��boss�R�� (�Ω�M�żƾڮw�A�L���ɷ|���Ӯį�)<�D�K�X>
	@PostMapping(value = "/api/deleteWorkInfoByDateBetween")
	public WorkSystemRes deleteWorkInfoByDateBetween(@RequestBody WorkSystemReq req) {
//		if(!req.getPassword().equals("aaa")) {
//			return new WorkSystemRes("�K�X���~");
//		}
		return workSystemService.deleteWorkInfoByDateBetween(req);
	}

	// ---���D�ު��s�W�m¾�欰
	@PostMapping(value = "/api/creatAbsenteeismForManager")
	public WorkSystemRes creatAbsenteeismForManager(@RequestBody WorkSystemReq req,HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getManagerEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("�п�J�ۤv���D�޽s��");
//		}
		return workSystemService.creatAbsenteeismForManager(req);
	}

	// ---���D�ު��R��<�m¾>�欰
	@PostMapping(value = "/api/deleteAbsenteeismForManager")
	public WorkSystemRes deleteAbsenteeismForManager(@RequestBody WorkSystemReq req,HttpSession httpSession) {
//		httpSession.setAttribute("EmployeeCode", "a99");//�n�J
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getManagerEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("�п�J�ۤv���D�޽s��");
//		}
		return workSystemService.deleteAbsenteeismForManager(req);
	}

	// ---���L�X���d��TgetWorkInfoListToday (���d�U�Z)
	@PostMapping(value = "/api/getWorkInfoListToday")
	public WorkSystemRes getWorkInfoListToday(@RequestBody WorkSystemReq req) {
		return workSystemService.getWorkInfoListToday(req);
	}

	// ---���L�X���d��TgetWorkInfoListAbsenteeism (�R���m¾)
	@PostMapping(value = "/api/getWorkInfoListAbsenteeism")
	public WorkSystemRes getWorkInfoListAbsenteeism(@RequestBody WorkSystemReq req,HttpSession httpSession) {
//		Object employeeCode = httpSession.getAttribute("EmployeeCode");
//		String employeeCodeString = httpSession.getAttribute("EmployeeCode").toString();
//		if (!employeeCodeString.equals(req.getManagerEmployeeCode()) || employeeCode == null) {
//			return new WorkSystemRes("�п�J�ۤv���D�޽s��");
//		}
		return workSystemService.getWorkInfoListAbsenteeism(req);
	}

}
