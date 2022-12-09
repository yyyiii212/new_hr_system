package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.vo.EmployeeInfoReq;
import com.example.new_hr_system.vo.EmployeeInfoRes;
import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

public interface HrSystemService {
	public EmployeeInfo createEmployeeInfo(EmployeeInfoReq req);

	public EmployeeInfo readEmployeeInfo(EmployeeInfoReq req);

	public EmployeeInfo updateEmployeeInfo(EmployeeInfoReq req);

	public EmployeeInfo deleteEmployeeInfo(EmployeeInfoReq req);

}
