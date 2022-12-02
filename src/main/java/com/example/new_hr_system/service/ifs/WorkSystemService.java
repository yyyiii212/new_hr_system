package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

public interface WorkSystemService {
	// ---WorkSystem上班打卡 c
	public WorkSystemRes punchToWork(WorkSystemReq req);

	// ---WorkSystem下班打卡 u
	public WorkSystemRes punchToOffWork(WorkSystemReq req);

	// ---WorkSystem搜尋上班狀況 r
	public WorkSystemRes searchWorkInfo(WorkSystemReq req) ;
}
