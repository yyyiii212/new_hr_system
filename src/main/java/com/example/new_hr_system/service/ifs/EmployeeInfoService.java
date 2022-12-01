package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.vo.EmployeeInfoReq;

public interface EmployeeInfoService {
	
	//新增員工資料
	public EmployeeInfo createEmployeeInfo(EmployeeInfoReq req);
	
	//搜尋員工資料
	public EmployeeInfo readEmployeeInfo(EmployeeInfoReq req);
	
	//修改員工資料
	public EmployeeInfo updateEmployeeInfo(EmployeeInfoReq req);
	
	//刪除員工資料
	public EmployeeInfo deleteEmployeeInfo(EmployeeInfoReq req);
}
