package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

public interface WorkSystemService {

	// ---上班打卡 c
	public WorkSystemRes punchToWork(WorkSystemReq req);

	// ---下班打卡 u
	public WorkSystemRes punchToOffWork(WorkSystemReq req);

	// ---搜尋上班狀況 r 給員工的
	public WorkSystemRes searchWorkInfoForStaff(WorkSystemReq req);

	// ---搜尋上班狀況 r 給主管的
	public WorkSystemRes searchWorkInfoForManager(WorkSystemReq req);

	// ---刪除上班狀況 (時間區間)
	public WorkSystemRes deleteWorkInfoByDateBetween(WorkSystemReq req);

	// ---新增曠職行為 (給主管key)
	public WorkSystemRes creatAbsenteeismForManager(WorkSystemReq req);

	// ---刪除<曠職>行為 (給主管key)
	public WorkSystemRes deleteAbsenteeismForManager(WorkSystemReq req);

}
