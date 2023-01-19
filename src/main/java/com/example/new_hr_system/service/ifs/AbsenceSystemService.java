package com.example.new_hr_system.service.ifs;

import javax.servlet.http.HttpSession;

import com.example.new_hr_system.vo.AbsenceSystemReq;
import com.example.new_hr_system.vo.AbsenceSystemRes;
import com.example.new_hr_system.vo.AbsenceSystemResList;
import com.example.new_hr_system.vo.EmployeeInfoRes;

public interface AbsenceSystemService {

	// 員工新增假單(單筆)
	// 輸入:員編,假別,日期(Date), email 輸出:假單(uuid,員編,假別,日期(Date)), 成功訊息, email
	// 功能:員工新增假單,按下新增鍵後創建假單並寄送email通知主管批假
	public AbsenceSystemRes addAbsence(AbsenceSystemReq req, HttpSession httpSession);

	// 員工新增假單(多筆)
	// 輸入:員編,假別,日期(Date), email 輸出:假單(uuid,員編,假別,日期(Date)), 成功訊息, email
	// 功能:員工新增假單,按下新增鍵後創建假單並寄送email通知主管批假
	public AbsenceSystemRes addAbsences(AbsenceSystemReq req, HttpSession httpSession);

	// 刪除假單
	// 輸入:uuid 輸出:成功訊息
	// 功能:依照前端傳回來的uuid刪除假單
	public AbsenceSystemRes deleteAbsence(AbsenceSystemReq req);

	// 員工顯示自己的假單
	public AbsenceSystemResList getAbsenceByEmployeeCode(HttpSession httpSession);

	public AbsenceSystemResList getAbsenceByEmployeeCodeAndDate(AbsenceSystemReq req, HttpSession httpSession);

	// 主管顯示同部門員工的假單
	// 輸入:主管員編 輸出:請假員工的員編,姓名,部門,假別,日期(Date)
	// 功能:依照前端傳回的主管員編,顯示與該主管相同部門且等級以下的員工假單
	public AbsenceSystemResList getAbsenceBySectionAndLevel(HttpSession httpSession);

	// 主管批准假單
	// 輸入:uuid,1或2,請假日期 輸出:新增未來出勤(uuid, 員編, 出勤日(DateTime), 出情狀況(請假), 出勤時數(0)), 成功訊息,
	// email
	// 功能:判斷前端批准假單的按鍵如果是批准假單(1)則新增未來出勤與寄送email提醒員工, 若不批准(2)則只寄送email
	public AbsenceSystemRes checkYesOrNo(AbsenceSystemReq req);

	// 判斷員工階級(小於2不能點)
	public boolean checkEmployeeLevel(HttpSession httpSession);

	public AbsenceSystemRes updateAbsence(AbsenceSystemReq req);

	// 員工顯示自己部門主管的email
	public EmployeeInfoRes getManagerEmailByEmployeeCode(HttpSession httpSession);

}
