package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.vo.SalarySystemReq;
import com.example.new_hr_system.vo.SalarySystemRes;

public interface SalarySystemService {
	// ---新增薪水資料 c
	public SalarySystemRes creatSalarySystem(SalarySystemReq req);

	// ---修改薪水資料 u
	public SalarySystemRes updateSalarySystem(SalarySystemReq req);

	// ---查詢薪水資料 r 給員工的
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req);

	// ---查詢薪水資料 r 給主管的
	public SalarySystemRes searchSalarySystemForManager(SalarySystemReq req);

}
