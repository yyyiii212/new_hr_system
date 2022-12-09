package com.example.new_hr_system.service.ifs;

import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

public interface WorkSystemService {

	// ---�W�Z���d c
	public WorkSystemRes punchToWork(WorkSystemReq req);

	// ---�U�Z���d u
	public WorkSystemRes punchToOffWork(WorkSystemReq req);

	// ---�j�M�W�Z���p r �����u��
	public WorkSystemRes searchWorkInfoForStaff(WorkSystemReq req);

	// ---�j�M�W�Z���p r ���D�ު�
	public WorkSystemRes searchWorkInfoForManager(WorkSystemReq req);

	// ---�R���W�Z���p (�ɶ��϶�)
	public WorkSystemRes deleteWorkInfoByDateBetween(WorkSystemReq req);

	// ---�s�W�m¾�欰 (���D��key)
	public WorkSystemRes creatAbsenteeismForManager(WorkSystemReq req);

	// ---�R��<�m¾>�欰 (���D��key)
	public WorkSystemRes deleteAbsenteeismForManager(WorkSystemReq req);

}
