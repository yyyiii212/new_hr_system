package com.example.new_hr_system.service.ifs;

import javax.servlet.http.HttpSession;

import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

public interface WorkSystemService {

	/*--------------------(���u)���d�W�Z*/
	public WorkSystemRes punchToWork(WorkSystemReq req);

	/*--------------------(���u)���d�U�Z*/
	public WorkSystemRes punchToOffWork(WorkSystemReq req);

	/*--------------------(���u)�j�M���d���*/
	public WorkSystemRes searchWorkInfoForStaff(WorkSystemReq req);

	/*--------------------(�D��)�j�M���d���*/
	public WorkSystemRes searchWorkInfoForManager(WorkSystemReq req);

	/*--------------------(�j����)�R���ɶ��϶������d���*/
	public WorkSystemRes deleteWorkInfoByDateBetween(WorkSystemReq req);

	/*--------------------(�D��)�s�W�m¾�欰*/
	public WorkSystemRes creatAbsenteeismForManager(WorkSystemReq req);

	/*--------------------(�D��)�R���m¾�欰*/
	public WorkSystemRes deleteAbsenteeismForManager(WorkSystemReq req);

	/*--------------------(���u)�L�X���d��T���U�Z���d��*/
	public WorkSystemRes getWorkInfoListToday(WorkSystemReq req);

	/*--------------------(�D��)�L�X���d��T���R���m¾�欰��*/
	public WorkSystemRes getWorkInfoListAbsenteeism(WorkSystemReq req);

	/*--------------------(�D��)���u�S���d�U�Z��D�޸ɥ��d*/
	public WorkSystemRes updeateWorkOffTimeForManager(WorkSystemReq req);

}
