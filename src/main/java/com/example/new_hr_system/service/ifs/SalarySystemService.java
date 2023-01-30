package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.vo.SalarySystemReq;
import com.example.new_hr_system.vo.SalarySystemRes;

public interface SalarySystemService {
	/*--------------------(�D��)�s�W�~�����*/ //1-2 ps.���P�_����
	public SalarySystemRes creatSalarySystem(SalarySystemReq req);

	/*--------------------(�D��)�ק��~�����*/ //2
	public SalarySystemRes updateSalarySystem(SalarySystemReq req);

	/*--------------------(���u)�d���~�����*/ 
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req);

	/*--------------------(�D��)�d���~�����*/ //2
	public SalarySystemRes searchSalarySystemForManager(SalarySystemReq req);

}
