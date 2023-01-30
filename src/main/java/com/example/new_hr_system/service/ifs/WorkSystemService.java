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

	/*--------------------(���u)�L�X���d��T���U�Z���d��*/
	public WorkSystemRes getWorkInfoListToday(WorkSystemReq req);

	/*--------------------(�D��)�s�W�m¾�欰*/ // 1-2 ps.���P�_����
	public WorkSystemRes creatAbsenteeismForManager(WorkSystemReq req);

	/*--------------------(�D��)�L�X���d��T���R���m¾�欰��*/ // 1-2 ps.���P�_����
	public WorkSystemRes getWorkInfoListAbsenteeism(WorkSystemReq req);

	/*--------------------(�D��)�R���m¾�欰*/ // 1-2 ps.�W�����[���h�P�_�A�o�̴N���ݭn�{
	public WorkSystemRes deleteAbsenteeismForManager(WorkSystemReq req);

	/*--------------------(�D��)�j�M���d���*/ // 1-2
	public WorkSystemRes searchWorkInfoForManager(WorkSystemReq req);

	/*--------------------(�D��)���u�S���d�U�Z��D�޸ɥ��U�Z�d*/ // 2
	public WorkSystemRes updeateWorkOffTimeForManager(WorkSystemReq req);

	/*--------------------(�D��)���u�ѤF���d��D�޸ɥ��d*/ // 2
	public WorkSystemRes forgotToPunchCard(WorkSystemReq req);

	/*--------------------(�j����)�R���ɶ��϶������d���*/ // 2
	public WorkSystemRes deleteWorkInfoByDateBetween(WorkSystemReq req);
}
