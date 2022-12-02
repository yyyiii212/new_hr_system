package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

public interface WorkSystemService {
	// ---WorkSystem�W�Z���d c
	public WorkSystemRes punchToWork(WorkSystemReq req);

	// ---WorkSystem�U�Z���d u
	public WorkSystemRes punchToOffWork(WorkSystemReq req);

	// ---WorkSystem�j�M�W�Z���p r
	public WorkSystemRes searchWorkInfo(WorkSystemReq req) ;
}
