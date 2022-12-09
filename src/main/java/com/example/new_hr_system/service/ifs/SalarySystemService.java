package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.vo.SalarySystemReq;
import com.example.new_hr_system.vo.SalarySystemRes;

public interface SalarySystemService {
	// ---�s�W�~����� c
	public SalarySystemRes creatSalarySystem(SalarySystemReq req);

	// ---�ק��~����� u
	public SalarySystemRes updateSalarySystem(SalarySystemReq req);

	// ---�d���~����� r �����u��
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req);

	// ---�d���~����� r ���D�ު�
	public SalarySystemRes searchSalarySystemForManager(SalarySystemReq req);

}
