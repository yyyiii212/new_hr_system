package com.example.new_hr_system.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.entity.WorkSystem;
import com.example.new_hr_system.respository.AbsenceSystemDao;
import com.example.new_hr_system.respository.EmployeeInfoDao;
import com.example.new_hr_system.respository.SalarySystemDao;
import com.example.new_hr_system.respository.WorkSystemDao;
import com.example.new_hr_system.service.ifs.WorkSystemService;
import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

@Service
public class WorkSystemServiceImpl implements WorkSystemService {
	@Autowired
	private AbsenceSystemDao absenceSystemDao;

	@Autowired
	private EmployeeInfoDao employeeInfoDao;

	@Autowired
	private SalarySystemDao salarySystemDao;

	@Autowired
	private WorkSystemDao workSystemDao;
	@Autowired
	private HttpSession httpSession;

	// -------------------------------------------------------------------------------

	// =====1.上班打卡
	@Override
	public WorkSystemRes punchToWork(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			res.setMessage("參數值不能為空");
			return new WorkSystemRes(res.getMessage());
		}
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeInfoOp.isPresent()) {
			res.setMessage("找不到該員工");
			return new WorkSystemRes(res.getMessage());
		}
		// 藉由員工編號(不是主key)撈資料
		List<WorkSystem> staffInfo = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		// 年分、月份、日 都一樣時 代表打過卡了
		for (WorkSystem item : staffInfo) {
			LocalDate localDate = item.getWorkTime().toLocalDate();
			if (localDate.equals(LocalDate.now())) {
				res.setMessage("勿重複打卡");
				return new WorkSystemRes(res.getMessage());
			}
		}
		res.setMessage("上班打卡成功");
		WorkSystem workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), LocalDateTime.now(), null,
				null, 0);
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, res.getMessage());

	}

	// =====2.下班打卡
	@Override
	public WorkSystemRes punchToOffWork(WorkSystemReq req) {

		UUID uuid = UUID.fromString(req.getUuid());
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getUuid())) {
			res.setMessage("參數值不能為空");
			return new WorkSystemRes(res.getMessage());
		}
		// 請求亂碼uuid ， 因為上班時打過卡了，固會產生(前端顯示時，藉由"按鈕"取得亂碼)
		Optional<WorkSystem> workSystemOp = workSystemDao.findById(uuid);

		// 因為一定有，所以取該筆資料
		WorkSystem workSystem = workSystemOp.get();

		// 宗憲那邊的"請假"會在我這邊新增 (ps.這裡尚未確定)
		if ((workSystem.getAttendanceStatus() != null) || workSystem.getOffWorkTime() != null) {
			res.setMessage("請勿更改打卡內容");
			return new WorkSystemRes(res.getMessage());
		}

		// 其實這裡的防呆沒啥意義，因為前端會顯示的資料 ，資料庫一定有該uuid
		if (!workSystemOp.isPresent()) {
			res.setMessage("參數值錯誤");
			return new WorkSystemRes(res.getMessage());
		}

		// 確認年分、月份、天數 不相同代表不是當天打卡(ps.忘了打卡)
		LocalDate workDate = workSystem.getWorkTime().toLocalDate();
		if (!workDate.equals(LocalDate.now())) {
			res.setMessage("不能補打卡");
			return new WorkSystemRes(res.getMessage());
		}

		// 正常上下班的時數(現在時間-上班時間)
		int countOffWorkHours = LocalDateTime.now().getHour() - 9;

		// 遲到或早退時的計算時數 (下班-他來的時間)
		int countWorkLateOrLeaveTime = LocalDateTime.now().getHour() - workSystem.getWorkTime().getHour();

		// 字串更新狀況
		String attendanceStatusStr;

		// 第一種:遲到+早退 = 上班時間 >= 9:00 & 上班分鐘 >= 1 & 下班時數 < 8 (ps.計入時數為 : 遲到的時數)
		// 第二種:遲到 =上班小時 >= 9:00 & 上班分鐘 >= 1 (為了防9:00) 或是 上班小時 > 9:00 (ps.計入時數為 : 遲到的時數)
		// 第三種:早退 = (正常上下班的時數) < 8 (ps.計入時數為 : 下班時間-9點 的時數) # 早退沒有遲到
		// 第四種:正常 = 上述都不合 (ps.計入時數為 :正常的時數)

		if ((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				&& countOffWorkHours < 8) {
			attendanceStatusStr = "遲到+早退";
			workSystem.setOffWorkTime(LocalDateTime.now());
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);

		} else if ((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				|| workSystem.getWorkTime().getHour() > 9) {
			attendanceStatusStr = "遲到";
			workSystem.setOffWorkTime(LocalDateTime.now());
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);
		} else if (countOffWorkHours < 8) {
			attendanceStatusStr = "早退";
			if (countOffWorkHours <= 0) {
				countOffWorkHours = 0;
			}
			workSystem.setOffWorkTime(LocalDateTime.now());
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		} else {
			attendanceStatusStr = "正常";
			workSystem.setOffWorkTime(LocalDateTime.now());
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		}
		workSystemDao.save(workSystem);
		res.setMessage("下班打卡成功");
		return new WorkSystemRes(workSystem, res.getMessage());

	}

	// =====搜尋打卡資料(給員工的)
	@Override
	public WorkSystemRes searchWorkInfoForStaff(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());// 有員工編號true
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());// 有開始日期true
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());// 有結束日期true
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		// 員工只能搜索到自己的資料，故這邊要判斷
		if (!checkEmployeeCode || (!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate)) {
			return new WorkSystemRes("參數或員工編號不能為空");
		}
		// 有結束時間但沒有開始時間要防呆
		if (!checkSearchStartDate && checkSearchEndDate) {
			return new WorkSystemRes("輸入開始時間");
		}

		// 沒有輸入開始時間一定會掉過來
		if (!checkSearchStartDate) {
			List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeOrderByWorkTimeDesc(req.getEmployeeCode());
			if (workInfoList.isEmpty()) {
				return new WorkSystemRes("查無資料");
			}
			res.setWorkInfoList(workInfoList);
			return res;
		}

		// 開始、結束時間都有輸入
		if (checkSearchStartDate && checkSearchEndDate) {
			// 確認是否符合正規表達
			if (!req.getSearchStartDate().matches(checkDateString)
					|| !req.getSearchEndDate().matches(checkDateString)) {
				res.setMessage("日期格式錯誤 請輸入(yyyy年mm月dd日)");
				return new WorkSystemRes(res.getMessage());
			}
			// 往下走代表可以轉日期了
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				return new WorkSystemRes("結束時間不可小於開始時間");
			}

			// 因為worktime的格式是LocalDateTime 所以要將LocalDate轉型成LocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atStartOfDay();
			// 撈出符合資格的資料
			List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(
					req.getEmployeeCode(), startDateTime, endDateTime);
			if (workInfoList.isEmpty()) {
				return new WorkSystemRes("查無資料");
			}
			res.setWorkInfoList(workInfoList);
			return res;
		}
		// 上面都沒擋掉代表有員工編號、開始日期，沒有結束日期
		if (!req.getSearchStartDate().matches(checkDateString)) {
			res.setMessage("日期格式錯誤 請輸入(yyyy年mm月dd日)");
			return new WorkSystemRes(res.getMessage());
		}
		// 只要將開始日期轉行即可LocalDate
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		// 結束時間用今天
		LocalDate endDate = LocalDate.now();
		if (endDate.isBefore(startDate)) {
			res.setMessage("今天時間不可小於開始時間");
			return new WorkSystemRes(res.getMessage());
		}
		// 因為worktime的格式是LocalDateTime 所以要將LocalDate轉型成LocalDateTime
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atStartOfDay();
		List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(
				req.getEmployeeCode(), startDateTime, endDateTime);
		if (workInfoList.isEmpty()) {
			res.setMessage("查無資料");
			return new WorkSystemRes(res.getMessage());
		}
		res.setWorkInfoList(workInfoList);
		return res;
	}

	// =====搜尋打卡資料(給主管的)
	@Override
	public WorkSystemRes searchWorkInfoForManager(WorkSystemReq req, HttpSession httpSession) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("請輸入您的主管編號");
		}
		Optional<EmployeeInfo> employeeInfoManagerOp = employeeInfoDao.findById(req.getManagerEmployeeCode());// 1
		if (!employeeInfoManagerOp.isPresent()) {
			return new WorkSystemRes("請輸入您的正確編號");
		}
		EmployeeInfo employeeManagerInfo = employeeInfoManagerOp.get();// 2
		List<WorkSystem> workInfoAllList = new ArrayList<>();// 3

		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());// 有員工編號true
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());// 有開始日期true
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());// 有結束日期true
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		// 三者都沒輸入 回傳所有資訊給主管看
		if ((!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate)) {
			List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();
			for (var item : workInfoList) {// 4
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
					workInfoAllList.add(item);// 8

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("該部門沒有資料，請確認該部門是否有該員工");
			}
			return new WorkSystemRes(workInfoAllList, " 部門 : " + employeeManagerInfo.getSection());
		}

		// 有結束日期，但沒有開始日期的防呆
		if (checkSearchEndDate && !checkSearchStartDate) {
			return new WorkSystemRes("輸入開始日期");
		}

		// 有開始、結束日期的判斷，正規表達防呆
		if (checkSearchStartDate && checkSearchEndDate) {
			if (!req.getSearchStartDate().matches(checkDateString)
					|| !req.getSearchEndDate().matches(checkDateString)) {
				res.setMessage("日期格式錯誤 請輸入(yyyy年mm月dd日)");
				return new WorkSystemRes(res.getMessage());
			}
		}
		// 有開始日期的判斷，正規表達防呆
		if (checkSearchStartDate) {
			if (!req.getSearchStartDate().matches(checkDateString)) {
				res.setMessage("日期格式錯誤 請輸入(yyyy年mm月dd日)");
				return new WorkSystemRes(res.getMessage());
			}
		}

		// 判斷有輸入員工編號，但沒有輸入開始日期
		if (checkEmployeeCode && !checkSearchStartDate) {
			List<WorkSystem> workInfoListByEmployeeCode = workSystemDao
					.findByEmployeeCodeOrderByWorkTimeDesc(req.getEmployeeCode());
			if (workInfoListByEmployeeCode.isEmpty()) {
				return new WorkSystemRes("查無資料");
			}
			for (var item : workInfoListByEmployeeCode) {// 4
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
					workInfoAllList.add(item);// 8

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("該部門沒有資料，請確認該部門是否有該員工");
			}
			return new WorkSystemRes(workInfoAllList, " 部門 : " + employeeManagerInfo.getSection());
		}

		// 三者都有輸入 員工編號、開始、結束日期
		if (checkEmployeeCode && checkSearchStartDate && checkSearchEndDate) {
			// 轉型
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				res.setMessage("結束時間不小於開始時間");
				return new WorkSystemRes(res.getMessage());
			}
			// 為了放進JPA方法，轉成LocalDateTime，因為工作時間是LocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atStartOfDay();
			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(req.getEmployeeCode(), startDateTime,
							endDateTime);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				return new WorkSystemRes("查無資料");
			}
			for (var item : workInfoListByEmployeeCodeAndDate) {// 4
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
					workInfoAllList.add(item);// 8

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("該部門沒有資料，請確認該部門是否有該員工");
			}

			return new WorkSystemRes(workInfoAllList, " 部門 : " + employeeManagerInfo.getSection());

		}
		// 判斷只有員工編號、開始日期
		if (checkEmployeeCode && checkSearchStartDate) {
			// 只要轉開始日期即可
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			if (LocalDate.now().isBefore(startDate)) {
				res.setMessage("今天不可小於開始時間");
				return new WorkSystemRes(res.getMessage());
			}
			// 為了放進JPA方法，轉成LocalDateTime，因為工作時間是LocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDate endDateNow = LocalDate.now();
			LocalDateTime endDateTimeNow = endDateNow.atStartOfDay();
			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(req.getEmployeeCode(), startDateTime,
							endDateTimeNow);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				res.setMessage("查無資料");
				return new WorkSystemRes(res.getMessage());
			}
			for (var item : workInfoListByEmployeeCodeAndDate) {// 4
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
					workInfoAllList.add(item);// 8

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("該部門沒有資料，請確認該部門是否有該員工");
			}
			return new WorkSystemRes(workInfoAllList, " 部門 : " + employeeManagerInfo.getSection());
		}
		// 上面都沒擋掉，代表一定沒有員工編號 (搜尋時間區間的所有員工)
		if (checkSearchStartDate && checkSearchEndDate) {
			// 將開始、結束日期轉型
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			// 為了放進JPA方法，轉成LocalDateTime，因為工作時間是LocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atStartOfDay();
			if (endDate.isBefore(startDate)) {
				res.setMessage("結束時間不小於開始時間");
				return new WorkSystemRes(res.getMessage());
			}
			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByWorkTimeBetweenOrderByWorkTimeDesc(startDateTime, endDateTime);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				return new WorkSystemRes("查無資料");
			}
			for (var item : workInfoListByEmployeeCodeAndDate) {// 4
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
					workInfoAllList.add(item);// 8

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("該部門沒有資料，請確認該部門是否有該員工");
			}
			return new WorkSystemRes(workInfoAllList, " 部門 : " + employeeManagerInfo.getSection());
		}
		// 上面都沒擋掉代表沒有員工編號、結束日期
		// 只要轉開始日期即可
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		if (LocalDate.now().isBefore(startDate)) {
			res.setMessage("今天時間不可小於開始時間");
			return new WorkSystemRes(res.getMessage());
		}
		// 為了放進JPA方法，轉成LocalDateTime，因為工作時間是LocalDateTime
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDate endDateNow = LocalDate.now();
		LocalDateTime endDateTimeNow = endDateNow.atStartOfDay();
		List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
				.findByWorkTimeBetweenOrderByWorkTimeDesc(startDateTime, endDateTimeNow);
		if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
			return new WorkSystemRes("查無資料");
		}
		for (var item : workInfoListByEmployeeCodeAndDate) {// 4
			Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
			EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
			if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
				workInfoAllList.add(item);// 8

			}
		}
		if (workInfoAllList.isEmpty()) {
			return new WorkSystemRes("該部門沒有資料，請確認該部門是否有該員工");
		}
		return new WorkSystemRes(workInfoAllList, " 部門 : " + employeeManagerInfo.getSection());

	}

	// =====刪除打卡資料(給主管的)=>刪除時間區間的資料
	@Override
	public WorkSystemRes deleteWorkInfoByDateBetween(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getSearchStartDate()) || !StringUtils.hasText(req.getSearchEndDate())) {
			List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();
			return new WorkSystemRes(workInfoList,"刪除前先看看吧");
		}
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		if (!req.getSearchStartDate().matches(checkDateString) || !req.getSearchEndDate().matches(checkDateString)) {
			res.setMessage("日期格式錯誤 請輸入(yyyy年mm月dd日)");

			return new WorkSystemRes(res.getMessage());
		}
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
		if (endDate.isBefore(startDate)) {
			res.setMessage("開始時間不可大於結束時間");
			return new WorkSystemRes( res.getMessage());
		}
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atStartOfDay();
		workSystemDao.deleteByWorkTimeBetween(startDateTime, endDateTime);
		List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();
		return new WorkSystemRes(workInfoList, "刪除成功");
	}

	// =====新增曠職資料(給主管的)
	@Override
	public WorkSystemRes creatAbsenteeismForManager(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getEmployeeCode()) || !StringUtils.hasText(req.getAbsenteeismDate())
				|| !StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("參數值不能為空");
		}
		Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getEmployeeCode());// 5
		Optional<EmployeeInfo> employeeManagerOp = employeeInfoDao.findById(req.getManagerEmployeeCode());// 5
		if (!employeeStaffOp.isPresent() || !employeeManagerOp.isPresent()) {
			return new WorkSystemRes("請輸入正確編號");
		}
		EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
		EmployeeInfo employeeManagerInfo = employeeManagerOp.get();// 6

		if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
			return new WorkSystemRes("您與該員工不同部門");
		}
		// 日期ㄉ正規表達
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		if (!req.getAbsenteeismDate().matches(checkDateString)) {
			res.setMessage("日期格式錯誤 請輸入(yyyy年mm月dd日)");
			return new WorkSystemRes(res.getMessage());
		}
		LocalDate absenteeismDate = LocalDate.parse(req.getAbsenteeismDate(), formatDate);
		// 因為不能讓主管隨意記曠職，要確認員工今天沒來才能記
		if (absenteeismDate.isAfter(LocalDate.now())) {
			res.setMessage(req.getAbsenteeismDate() + "過後才能登錄曠職");
			return new WorkSystemRes(res.getMessage());
		}
		// 要撈出勤紀錄，防止主管在員工有來的情況下亂記曠職
		List<WorkSystem> staffInfo = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		for (var item : staffInfo) {
			LocalDate localDate = item.getWorkTime().toLocalDate();
			if (localDate.equals(absenteeismDate)) {
				res.setMessage("這位員工這天有上班或以記曠職過");
				return new WorkSystemRes(res.getMessage());
			}
		}
		// 上面都沒擋掉代表真ㄉ曠職了
		LocalDateTime absenteeismDateTime = absenteeismDate.atStartOfDay();
		WorkSystem workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), absenteeismDateTime,
				absenteeismDateTime, "曠職", 0);
		res.setMessage("新增曠職成功");
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, res.getMessage());

	}

	// =====刪除曠職資料
	@Override
	public WorkSystemRes deleteAbsenteeismForManager(WorkSystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getUuid())) {
			res.setMessage("參數值不能空");
			return new WorkSystemRes(res.getMessage());
		}
		// 請求亂碼uuid ， 因為上班時打過卡了，固會產生(前端顯示時，藉由"按鈕"取得亂碼)
		Optional<WorkSystem> workSystemOp = workSystemDao.findById(uuid);
		// 其實這裡的防呆沒啥意義，因為前端會顯示的資料 ，資料庫一定有該uuid
		if (!workSystemOp.isPresent()) {
			return new WorkSystemRes("參數值錯誤");
		}
		WorkSystem workSystem = workSystemOp.get();
		// 確認是主管不小心記<曠職>才能刪除，以防刪除到員工有來上班的紀錄
		if (workSystem.getAttendanceStatus().equals("曠職")) {
			workSystemDao.deleteById(uuid);
			res.setMessage("刪除曠職成功");
			return new WorkSystemRes(res.getMessage());
		}
		return new WorkSystemRes("這位員工這天沒有曠職");
	}

	// =====印出該員工打卡資料(前端給打卡下班用)
	@Override
	public WorkSystemRes getWorkInfoListToday(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new WorkSystemRes("參數值不能為空");
		}
		LocalDate nowDate = LocalDate.now();
		LocalDateTime nowDateTime = nowDate.atStartOfDay();
		List<WorkSystem> workInfoList = workSystemDao
				.findByEmployeeCodeAndWorkTimeGreaterThanEqual(req.getEmployeeCode(), nowDateTime);
		if (workInfoList.isEmpty()) {
			return new WorkSystemRes("你是不是上班沒打卡!?");
		}
		res.setWorkInfoList(workInfoList);
		return res;
	}

	// ===刪除曠職前，列出資訊，藉由按鈕取到uuid 
	@Override
	public WorkSystemRes getWorkInfoListAbsenteeism(WorkSystemReq req) {
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		if (!StringUtils.hasText(req.getAbsenteeismDate()) || !StringUtils.hasText(req.getEmployeeCode())
				|| !StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("參數值或日期不能為空");
		}
		LocalDate absenteeismDate = LocalDate.parse(req.getAbsenteeismDate(), formatDate);
		Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getManagerEmployeeCode());// 5
		Optional<EmployeeInfo> employeeManagerOp = employeeInfoDao.findById(req.getEmployeeCode());// 5
		if (!employeeStaffOp.isPresent() || !employeeManagerOp.isPresent()) {
			return new WorkSystemRes("請輸入正確編號");
		}
		EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
		EmployeeInfo employeeManagerInfo = employeeManagerOp.get();// 6
		if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
			return new WorkSystemRes("您與該員工不同部門");
		}
		LocalDateTime absenteeismDateTime = absenteeismDate.atStartOfDay();
		List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeAndWorkTime(req.getEmployeeCode(),
				absenteeismDateTime);
		if (workInfoList.isEmpty()) {
			return new WorkSystemRes("查無資料");
		}
		return new WorkSystemRes(workInfoList, " 部門 : " + employeeManagerInfo.getSection());
	}

}
