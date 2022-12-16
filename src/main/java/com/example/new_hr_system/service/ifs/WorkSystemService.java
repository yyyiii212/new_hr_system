package com.example.new_hr_system.service.ifs;

import javax.servlet.http.HttpSession;

import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

public interface WorkSystemService {

	// ���o���U�n�J�ɸj��EmployeeCode 
	public WorkSystemRes employeeCodeLogin(WorkSystemReq req);

	// ---�W�Z���d c
	public WorkSystemRes punchToWork(WorkSystemReq req);

	// ---�U�Z���d u
	public WorkSystemRes punchToOffWork(WorkSystemReq req);

	// ---�j�M�W�Z���p r �����u��
	public WorkSystemRes searchWorkInfoForStaff(WorkSystemReq req);

	// ---�j�M�W�Z���p r ���D�ު�
	public WorkSystemRes searchWorkInfoForManager(WorkSystemReq req);

	// ---�R���W�Z���p (�ɶ��϶�)
	public WorkSystemRes deleteWorkInfoByDateBetween(WorkSystemReq req);

	// ---�s�W�m¾�欰 (���D��key)
	public WorkSystemRes creatAbsenteeismForManager(WorkSystemReq req);

	// ---�R��<�m¾>�欰 (���D��key)
	public WorkSystemRes deleteAbsenteeismForManager(WorkSystemReq req);

	// ---���d�U�Z�ɡA�s�XList���ͥXbutton���Τ�i�ǥѫ��s���ouuid
	public WorkSystemRes getWorkInfoListToday(WorkSystemReq req);

	// ---�R���m¾�ɡA�s�XList���ͥXbutton���Τ�i�ǥѫ��s���ouuid
	public WorkSystemRes getWorkInfoListAbsenteeism(WorkSystemReq req);

}
