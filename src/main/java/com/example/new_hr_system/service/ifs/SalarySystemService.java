package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.vo.SalarySystemReq;
import com.example.new_hr_system.vo.SalarySystemRes;

public interface SalarySystemService {
	/*--------------------(�D��)�s�W�~�����*/
	public SalarySystemRes creatSalarySystem(SalarySystemReq req);

	/*--------------------(�D��)�ק��~�����*/
	public SalarySystemRes updateSalarySystem(SalarySystemReq req);

	/*--------------------(���u)�d���~�����*/
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req);

	/*--------------------(�D��)�d���~�����*/
	public SalarySystemRes searchSalarySystemForManager(SalarySystemReq req);

}
