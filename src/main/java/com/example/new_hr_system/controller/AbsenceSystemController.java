package com.example.new_hr_system.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.new_hr_system.service.ifs.AbsenceSystemService;
import com.example.new_hr_system.vo.AbsenceSystemReq;
import com.example.new_hr_system.vo.AbsenceSystemRes;
import com.example.new_hr_system.vo.AbsenceSystemResList;
import com.example.new_hr_system.vo.EmployeeInfoRes;

@RestController
public class AbsenceSystemController {

	@Autowired
	private AbsenceSystemService absenceSystemService;

	// 員工新增假單(單筆)
	@PostMapping(value = "/api/addAbsence")
	public AbsenceSystemRes addAbsence(@RequestBody AbsenceSystemReq req, HttpSession httpSession) {

		return absenceSystemService.addAbsence(req, httpSession);

	}
	
	// 員工新增假單(多筆)(15:26)
		@PostMapping(value = "/api/addAbsences")
		public AbsenceSystemRes addAbsences(@RequestBody AbsenceSystemReq req, HttpSession httpSession) {

			return absenceSystemService.addAbsences(req, httpSession);

		}

	// 刪除假單
	@PostMapping(value = "/api/deleteAbsence")
	public AbsenceSystemRes deleteAbsence(@RequestBody AbsenceSystemReq req) {

		return absenceSystemService.deleteAbsence(req);

	}

	// 員工顯示自己的假單
	@PostMapping(value = "/api/getAbsenceByEmployeeCode")
	public AbsenceSystemResList getAbsenceByEmployeeCode(HttpSession httpSession) {

		return absenceSystemService.getAbsenceByEmployeeCode(httpSession);

	}

	// 員工輸入年月查詢自己的假單
	@PostMapping(value = "/api/getAbsenceByEmployeeCodeAndDate")
	public AbsenceSystemResList getAbsenceByEmployeeCodeAndDate(@RequestBody AbsenceSystemReq req,
			HttpSession httpSession) {

		return absenceSystemService.getAbsenceByEmployeeCodeAndDate(req, httpSession);

	}

	// 主管顯示同部門員工的假單
	@PostMapping(value = "/api/getAbsenceBySectionAndLevel")
	public AbsenceSystemResList getAbsenceBySectionAndLevel(HttpSession httpSession) {

		return absenceSystemService.getAbsenceBySectionAndLevel(httpSession);

	}

	// 主管批准假單
	@PostMapping(value = "/api/checkYesOrNo")
	public AbsenceSystemRes checkYesOrNo(@RequestBody AbsenceSystemReq req) {

		return absenceSystemService.checkYesOrNo(req);

	}

	// 判斷員工階級(小於2不能點)
	@PostMapping(value = "/api/checkEmployeeLevel")
	public boolean checkEmployeeLevel(HttpSession httpSession) {

		return absenceSystemService.checkEmployeeLevel(httpSession);

	}

	// 更新員工假單
	@PostMapping(value = "/api/updateAbsence")
	public AbsenceSystemRes updateAbsence(@RequestBody AbsenceSystemReq req) {

		return absenceSystemService.updateAbsence(req);

	}

	// 員工顯示自己部門主管的email
	@PostMapping(value = "/api/getManagerEmailByEmployeeCode")
	public EmployeeInfoRes getManagerEmailByEmployeeCode(HttpSession httpSession) {

		return absenceSystemService.getManagerEmailByEmployeeCode(httpSession);

	}

}
