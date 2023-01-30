package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.vo.SalarySystemReq;
import com.example.new_hr_system.vo.SalarySystemRes;

public interface SalarySystemService {
	/*--------------------(主管)新增薪水資料*/ //1-2 ps.有判斷階級
	public SalarySystemRes creatSalarySystem(SalarySystemReq req);

	/*--------------------(主管)修改薪水資料*/ //2
	public SalarySystemRes updateSalarySystem(SalarySystemReq req);

	/*--------------------(員工)查詢薪水資料*/ 
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req);

	/*--------------------(主管)查詢薪水資料*/ //2
	public SalarySystemRes searchSalarySystemForManager(SalarySystemReq req);

}
