package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.vo.EmployeeInfoReq;

public interface EmployeeInfoService {
	
	//�s�W���u���
	public EmployeeInfo createEmployeeInfo(EmployeeInfoReq req);
	
	//�j�M���u���
	public EmployeeInfo readEmployeeInfo(EmployeeInfoReq req);
	
	//�ק���u���
	public EmployeeInfo updateEmployeeInfo(EmployeeInfoReq req);
	
	//�R�����u���
	public EmployeeInfo deleteEmployeeInfo(EmployeeInfoReq req);
}
