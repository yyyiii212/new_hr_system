package com.example.new_hr_system.service.ifs;

import javax.servlet.http.HttpSession;

import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

public interface WorkSystemService {

	/*--------------------(員工)打卡上班*/
	public WorkSystemRes punchToWork(WorkSystemReq req);

	/*--------------------(員工)打卡下班*/
	public WorkSystemRes punchToOffWork(WorkSystemReq req);

	/*--------------------(員工)搜尋打卡資料*/
	public WorkSystemRes searchWorkInfoForStaff(WorkSystemReq req);

	/*--------------------(員工)印出打卡資訊給下班打卡用*/
	public WorkSystemRes getWorkInfoListToday(WorkSystemReq req);

	/*--------------------(主管)新增曠職行為*/ // 1-2 ps.有判斷階級
	public WorkSystemRes creatAbsenteeismForManager(WorkSystemReq req);

	/*--------------------(主管)印出打卡資訊給刪除曠職行為用*/ // 1-2 ps.有判斷階級
	public WorkSystemRes getWorkInfoListAbsenteeism(WorkSystemReq req);

	/*--------------------(主管)刪除曠職行為*/ // 1-2 ps.上面有加階層判斷，這裡就不需要ㄌ
	public WorkSystemRes deleteAbsenteeismForManager(WorkSystemReq req);

	/*--------------------(主管)搜尋打卡資料*/ // 1-2
	public WorkSystemRes searchWorkInfoForManager(WorkSystemReq req);

	/*--------------------(主管)員工沒打卡下班找主管補打下班卡*/ // 2
	public WorkSystemRes updeateWorkOffTimeForManager(WorkSystemReq req);

	/*--------------------(主管)員工忘了打卡找主管補打卡*/ // 2
	public WorkSystemRes forgotToPunchCard(WorkSystemReq req);

	/*--------------------(大老闆)刪除時間區間的打卡資料*/ // 2
	public WorkSystemRes deleteWorkInfoByDateBetween(WorkSystemReq req);
}
