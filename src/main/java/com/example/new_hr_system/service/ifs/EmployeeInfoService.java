package com.example.new_hr_system.service.ifs;

import java.util.List;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.vo.EmployeeInfoReq;

public interface EmployeeInfoService {
	
	//登入畫面帳密的判斷
	public EmployeeInfo loginJudgment(EmployeeInfoReq req);
	
	//新增員工資料
	public EmployeeInfo createEmployeeInfo(EmployeeInfoReq req);
	
	//搜尋全部員工資料
	public List<EmployeeInfo> readEmployeeInfo(EmployeeInfoReq req);
	
	//搜尋單一員工資料
	public EmployeeInfo readOneEmployeeInfo(EmployeeInfoReq req);
	
	//修改員工資料
	public EmployeeInfo updateEmployeeInfo(EmployeeInfoReq req);
	
	//刪除員工資料
	public EmployeeInfo deleteEmployeeInfo(EmployeeInfoReq req);
}
