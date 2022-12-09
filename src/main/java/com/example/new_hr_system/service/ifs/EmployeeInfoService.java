package com.example.new_hr_system.service.ifs;

import java.util.List;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.vo.EmployeeInfoReq;

public interface EmployeeInfoService {
	
	//�n�J�e���b�K���P�_
	public EmployeeInfo loginJudgment(EmployeeInfoReq req);
	//�s�W���u���
	public EmployeeInfo createEmployeeInfo(EmployeeInfoReq req);
	
	//�j�M���u���
	public List<EmployeeInfo> readEmployeeInfo(EmployeeInfoReq req);
	
	//�ק���u���
	public EmployeeInfo updateEmployeeInfo(EmployeeInfoReq req);
	
	//�R�����u���
	public EmployeeInfo deleteEmployeeInfo(EmployeeInfoReq req);
}
