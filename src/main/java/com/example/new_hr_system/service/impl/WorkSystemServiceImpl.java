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

	// -------------------------------------------------------------------------------

	/*------------------------------------------------(員工)打卡上班*/
	@Override
	public WorkSystemRes punchToWork(WorkSystemReq req) {

		// 上班只能打自己的卡，故要防呆 看是否為空

		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new WorkSystemRes("參數值不能為空");
		}

		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());

		if (!employeeInfoOp.isPresent()) {
			return new WorkSystemRes("找不到該員工");
		}

		// 藉由員工編號撈資料(判斷有無重複打卡)
		List<WorkSystem> staffInfo = workSystemDao.findByEmployeeCode(req.getEmployeeCode());

		// 年分、月份、日 都一樣時 代表打過卡了
		for (WorkSystem item : staffInfo) {
			LocalDate localDate = item.getWorkTime().toLocalDate();
			if (localDate.equals(LocalDate.now())) {
				return new WorkSystemRes("勿重複打卡");
			}

		}
		DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm");
		String nowDateTimeString = LocalDateTime.now().format(formatDateTime);
		LocalDateTime finalDateTime = LocalDateTime.parse(nowDateTimeString, formatDateTime);
		WorkSystem workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), finalDateTime, null, null, 0);
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, "上班打卡成功");

	}

	/*------------------------------------------------(員工)打卡下班*/
	@Override
	public WorkSystemRes punchToOffWork(WorkSystemReq req) {
		// 這個防呆沒什麼用
		if (!StringUtils.hasText(req.getUuid())) {
			return new WorkSystemRes("參數值不能為空");
		}

		UUID uuid = UUID.fromString(req.getUuid());

		// 請求亂碼uuid ， 因為上班時打過卡了，固會產生(前端顯示時，藉由"按鈕"取得亂碼)
		Optional<WorkSystem> workSystemOp = workSystemDao.findById(uuid);

		// 其實這裡的防呆沒啥意義，因為前端會顯示的資料 ，資料庫一定有該uuid
		if (!workSystemOp.isPresent()) {
			return new WorkSystemRes("參數值錯誤");
		}

		// 藉由uuid取到該資料
		WorkSystem workSystem = workSystemOp.get();

		// 當有下班時間或是有當天上班狀況時就會擋掉
		if ((workSystem.getAttendanceStatus() != null) || workSystem.getOffWorkTime() != null) {
			return new WorkSystemRes("請勿更改打卡內容");
		}

		// 確認年分、月份、天數 不相同代表不是當天打卡(ps.忘了打卡) <其實這裡應該沒用ㄌ>
		LocalDate workDate = workSystem.getWorkTime().toLocalDate();
		if (!workDate.equals(LocalDate.now())) {
			return new WorkSystemRes("請去找主管幫你補打卡");
		}

		// 正常上下班的時數(現在時間-上班時間) ps.(9 = 上班規定幾點要到)
		int countOffWorkHours = LocalDateTime.now().getHour() - 9;

		// 遲到或早退時的計算時數 (下班時間-他來的時間)
		int countWorkLateOrLeaveTime = LocalDateTime.now().getHour() - workSystem.getWorkTime().getHour();

		// 字串更新狀況
		String attendanceStatusStr;
		DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm");
		String nowDateTimeString = LocalDateTime.now().format(formatDateTime);
		LocalDateTime finalDateTime = LocalDateTime.parse(nowDateTimeString, formatDateTime);

		// 第一種:遲到+早退 = (上班時間 >= 9:00 & 上班分鐘 >= 1 & 下班小時 < 18) ||(上班時間 > 9:00 & 下班時間 <
		// 18)
		// 1 = > (ps.計入時數為 : 遲到的時數 ps.18=幾點下班)
		// 第二種:遲到 =上班小時 >= 9:00 & 上班分鐘 >= 1 (為了防9:00) 或是 上班小時 > 9:00 (ps.計入時數為 : 遲到的時數)
		// 2 = >防(9:00)或大於(10:00)的情況
		// 第三種:早退 = 下班小時小於18 # 早退沒有遲到
		// 3 => ps.計入時數為正常時數
		// 第四種:正常 = 上述都不合 (ps.計入時數為 :正常的時數)

		if (((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				&& LocalDateTime.now().getHour() < 18)
				|| (workSystem.getWorkTime().getHour() > 9 && LocalDateTime.now().getHour() < 18)) {
			attendanceStatusStr = "遲到+早退";
			// 防止 EX: 7 : 55 打卡上班 8:00 打卡下班 ，得出的遲到時數卻是 1 所以 超過 x : 30 直接 x+1
			if (workSystem.getWorkTime().getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}
			// 上述情況發生被-1時可能會低於0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem.setOffWorkTime(finalDateTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);

		} else if ((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				|| workSystem.getWorkTime().getHour() > 9) {
			attendanceStatusStr = "遲到";
			// 防止 EX: 7 : 55 打卡上班 8:00 打卡下班 ，得出的遲到時數卻是 1 所以 超過 x : 30 直接 x+1
			if (workSystem.getWorkTime().getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}
			// 上述情況發生被-1時可能會低於0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem.setOffWorkTime(finalDateTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);
		} else if (LocalDateTime.now().getHour() < 18) {
			attendanceStatusStr = "早退";
			// 以防出現 8:00打卡 9:00打卡下班
			if (countOffWorkHours <= 0) {
				countOffWorkHours = 0;
			}
			workSystem.setOffWorkTime(finalDateTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		} else {
			attendanceStatusStr = "正常";
			workSystem.setOffWorkTime(finalDateTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		}
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, "下班打卡成功");

	}

	/*------------------------------------------------(員工)搜尋打卡資料*/
	@Override
	public WorkSystemRes searchWorkInfoForStaff(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();

		// 有員工編號true
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());

		// 有開始日期true
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());

		// 有結束日期true
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());

		// 接近來是字串 ， 所以要正規表達 (其實可以不用了)
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";

		// 員工只能搜索到自己的資料，故這邊要判斷
		if (!checkEmployeeCode) {
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

		// 開始、結束時間都有輸入 (判斷是否符合字串正規表達)
		if (checkSearchStartDate && checkSearchEndDate) {
			if (!req.getSearchStartDate().matches(checkDateString)
					|| !req.getSearchEndDate().matches(checkDateString)) {
				return new WorkSystemRes("日期格式錯誤 請輸入(yyyy-mm-dd)");
			}

			// 往下走代表可以轉日期了
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);

			// 防87
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

		// 上面都沒擋掉代表有員工編號、開始日期，沒有結束日期 (固只要判斷開始日期是否符合正規表達)
		if (!req.getSearchStartDate().matches(checkDateString)) {
			return new WorkSystemRes("日期格式錯誤 請輸入(yyyy-mm-dd日)");
		}

		// 只要將開始日期轉行即可LocalDate
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);

		// 結束時間用今天
		LocalDate endDate = LocalDate.now();
		if (endDate.isBefore(startDate)) {
			return new WorkSystemRes("開始時間不可大於今天時間");
		}

		// 因為worktime的格式是LocalDateTime 所以要將LocalDate轉型成LocalDateTime (讓尾數時間變成 00:00:00)
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atStartOfDay();

		List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(
				req.getEmployeeCode(), startDateTime, endDateTime);
		if (workInfoList.isEmpty()) {
			return new WorkSystemRes("查無資料");
		}
		res.setWorkInfoList(workInfoList);
		return res;
	}

	/*------------------------------------------------(主管)搜尋打卡資料*/
	@Override
	public WorkSystemRes searchWorkInfoForManager(WorkSystemReq req) {
		// 這個功能是給主管用的，主管只能看到相同部門底下員工的資料，所以我需要主管編號
		if (!StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("請輸入您的主管編號");
		}

		Optional<EmployeeInfo> employeeInfoManagerOp = employeeInfoDao.findById(req.getManagerEmployeeCode());
		if (!employeeInfoManagerOp.isPresent()) {
			return new WorkSystemRes("請檢查您的編號");
		}

		// 取到主管編號的值
		EmployeeInfo employeeManagerInfo = employeeInfoManagerOp.get();

		// 接過濾後的東西
		List<WorkSystem> workInfoAllList = new ArrayList<>();

		// 有員工編號true
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		// 有開始日期true
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());
		// 有結束日期true
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());

		// 接近來字串，所以要正規表達
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";

		// 三者都沒輸入 回傳所有資訊給主管看 (要過濾)
		if ((!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate)) {
			List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();

			for (var item : workInfoList) {
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());

				// 因為逸祥那邊有刪除他自己的資料，所以我這邊要判斷如果在他那邊翻不到該名員工要從頭迴圈
				if (!employeeStaffOp.isPresent()) {
					continue;
				}
				// 上面沒從頭就取值
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

				// 判斷是不是一樣的部門
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
					workInfoAllList.add(item);

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
				return new WorkSystemRes("日期格式錯誤 請輸入(yyyy-mm-dd)");
			}
		}

		// 有開始日期的判斷，正規表達防呆
		if (checkSearchStartDate) {
			if (!req.getSearchStartDate().matches(checkDateString)) {
				return new WorkSystemRes("日期格式錯誤 請輸入(yyyy-mm-dd)");
			}
		}

		// 來到這裡說明通過所有防呆
		// 判斷有輸入員工編號，但沒有輸入開始日期
		if (checkEmployeeCode && !checkSearchStartDate) {
			Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeStaffOp.isPresent()) {
				return new WorkSystemRes("找不到該員工");
			}

			// 找到取值
			EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

			// 因為有輸入員工編號，所以在取得list前做防呆即可
			if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
				return new WorkSystemRes("你與該名員工不同部門");

			}

			List<WorkSystem> workInfoListByEmployeeCode = workSystemDao
					.findByEmployeeCodeOrderByWorkTimeDesc(req.getEmployeeCode());
			if (workInfoListByEmployeeCode.isEmpty()) {
				return new WorkSystemRes("查無資料");
			}

			return new WorkSystemRes(workInfoListByEmployeeCode, " 部門 : " + employeeManagerInfo.getSection());
		}

		// 三者都有輸入 員工編號、開始、結束日期
		if (checkEmployeeCode && checkSearchStartDate && checkSearchEndDate) {
			Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeStaffOp.isPresent()) {
				return new WorkSystemRes("找不到該員工");
			}

			// 找到取值
			EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

			// 因為有輸入員工編號，所以在取得list前做防呆即可
			if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
				return new WorkSystemRes("你與該名員工不同部門");

			}

			// 來到這代表日期也都符合正規表達
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);

			// 防87
			if (endDate.isBefore(startDate)) {
				return new WorkSystemRes("結束時間不小於開始時間");
			}

			// 因為entity是LocalDateTime 所以我先轉成LocalDate 再轉LocalDateTime 讓尾數變成00:00:00
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atStartOfDay();
			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(req.getEmployeeCode(), startDateTime,
							endDateTime);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				return new WorkSystemRes("查無資料");
			}
			return new WorkSystemRes(workInfoListByEmployeeCodeAndDate, " 部門 : " + employeeManagerInfo.getSection());

		}
		// 判斷只有員工編號、開始日期
		if (checkEmployeeCode && checkSearchStartDate) {
			Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeStaffOp.isPresent()) {
				return new WorkSystemRes("找不到該員工");
			}

			EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

			// 因為有輸入員工編號，所以在取得list前做防呆即可
			if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
				return new WorkSystemRes("你與該名員工不同部門");

			}

			// 只要轉開始日期即可
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			if (LocalDate.now().isBefore(startDate)) {
				return new WorkSystemRes("開始時間不可大於今天");
			}

			// 因為entity是LocalDateTime 所以我先轉成LocalDate 再轉LocalDateTime 讓尾數變成00:00:00
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDate endDateNow = LocalDate.now();
			LocalDateTime endDateTimeNow = endDateNow.atStartOfDay();

			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(req.getEmployeeCode(), startDateTime,
							endDateTimeNow);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				return new WorkSystemRes("查無資料");
			}
			return new WorkSystemRes(workInfoListByEmployeeCodeAndDate, " 部門 : " + employeeManagerInfo.getSection());
		}

		// 上面都沒擋掉，代表一定沒有員工編號 (搜尋時間區間的所有員工) (要過濾)
		if (checkSearchStartDate && checkSearchEndDate) {

			// 將開始、結束日期轉型
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);

			// 因為entity是LocalDateTime 所以我先轉成LocalDate 再轉LocalDateTime 讓尾數變成00:00:00
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atStartOfDay();

			// 防87
			if (endDate.isBefore(startDate)) {
				return new WorkSystemRes("結束時間不小於開始時間");
			}
			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByWorkTimeBetweenOrderByWorkTimeDesc(startDateTime, endDateTime);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				return new WorkSystemRes("查無資料");
			}
			for (var item : workInfoListByEmployeeCodeAndDate) {
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());
				// 逸祥那裏會刪除員工資訊，所以要判斷找不到員工時，從頭迴圈
				if (!employeeStaffOp.isPresent()) {
					continue;
				}

				// 沒從頭就取值
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

				// 判斷是否同一個部門
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
					workInfoAllList.add(item);

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("該部門沒有資料，請確認該部門是否有該員工");
			}
			return new WorkSystemRes(workInfoAllList, " 部門 : " + employeeManagerInfo.getSection());
		}

		// 上面都沒擋掉代表沒有員工編號、結束日期 ps.只有開始日期
		// 只要轉開始日期即可 (要過濾)
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		if (LocalDate.now().isBefore(startDate)) {
			return new WorkSystemRes("今天時間不可小於開始時間");
		}
		// 因為entity是LocalDateTime 所以我先轉成LocalDate 再轉LocalDateTime 讓尾數變成00:00:00
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDate endDateNow = LocalDate.now();
		LocalDateTime endDateTimeNow = endDateNow.atStartOfDay();
		List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
				.findByWorkTimeBetweenOrderByWorkTimeDesc(startDateTime, endDateTimeNow);
		if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
			return new WorkSystemRes("查無資料");
		}
		for (var item : workInfoListByEmployeeCodeAndDate) {
			Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());

			// 逸祥那裏會刪除員工資訊，所以要判斷找不到員工時，從頭迴圈
			if (!employeeStaffOp.isPresent()) {
				continue;
			}
			// 沒從頭就取值
			EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

			// 判斷是否同一個部門
			if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
				workInfoAllList.add(item);

			}
		}
		if (workInfoAllList.isEmpty()) {
			return new WorkSystemRes("該部門沒有資料，請確認該部門是否有該員工");
		}
		return new WorkSystemRes(workInfoAllList, " 部門 : " + employeeManagerInfo.getSection());

	}

	/*------------------------------------------------(大老闆)刪除時間區間的打卡資料*/
	@Override
	public WorkSystemRes deleteWorkInfoByDateBetween(WorkSystemReq req) {
		// 沒有輸入日期時，先給他看所有資料
		if (!StringUtils.hasText(req.getSearchStartDate()) || !StringUtils.hasText(req.getSearchEndDate())) {
			List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();
			return new WorkSystemRes(workInfoList, "刪除前先看看吧");
		}

		// 上面沒擋掉代表一定有日期
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		// 判斷是否符合正規表達
		if (!req.getSearchStartDate().matches(checkDateString) || !req.getSearchEndDate().matches(checkDateString)) {
			return new WorkSystemRes("日期格式錯誤 請輸入(yyyy-mm-dd)");
		}
		// 先轉日期
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
		if (endDate.isBefore(startDate)) {
			return new WorkSystemRes("開始時間不可大於結束時間");
		}
		// 因為entity是LocalDateTime 所以我先轉成LocalDate 再轉LocalDateTime 讓尾數變成00:00:00
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atStartOfDay();
		workSystemDao.deleteByWorkTimeBetween(startDateTime, endDateTime);
		List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();
		return new WorkSystemRes(workInfoList, "刪除成功");
	}

	/*------------------------------------------------(主管)新增曠職行為*/
	@Override
	public WorkSystemRes creatAbsenteeismForManager(WorkSystemReq req) {
		if (!StringUtils.hasText(req.getEmployeeCode()) || !StringUtils.hasText(req.getAbsenteeismDate())
				|| !StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("參數值不能為空");
		}

		// 因為要判斷員工與主管是否同一部門
		Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getEmployeeCode());
		Optional<EmployeeInfo> employeeManagerOp = employeeInfoDao.findById(req.getManagerEmployeeCode());
		if (!employeeStaffOp.isPresent() || !employeeManagerOp.isPresent()) {
			return new WorkSystemRes("請輸入正確編號可能不存在該名員工");
		}

		// 兩個都取值
		EmployeeInfo employeeStaffInfo = employeeStaffOp.get();
		EmployeeInfo employeeManagerInfo = employeeManagerOp.get();

		// 判斷是否同個部門
		if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
			return new WorkSystemRes("您與該員工不同部門");
		}

		// 判斷要要記曠職者與被記曠職者的階級
		if (employeeManagerInfo.getLevel() < employeeStaffInfo.getLevel()) {
			return new WorkSystemRes("您的權限不夠");
		}

		// 日期ㄉ正規表達
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		if (!req.getAbsenteeismDate().matches(checkDateString)) {
			return new WorkSystemRes("日期格式錯誤 請輸入(yyyy-mm-dd)");
		}

		// 通過正規表達 轉日期
		LocalDate absenteeismDate = LocalDate.parse(req.getAbsenteeismDate(), formatDate);

		// 因為不能讓主管隨意記曠職，要確認員工今天沒來才能記
		if (absenteeismDate.isAfter(LocalDate.now())) {
			return new WorkSystemRes(req.getAbsenteeismDate() + "過後才能登錄曠職");
		}

		// 要撈出勤紀錄，防止主管在員工有來的情況下亂記曠職
		List<WorkSystem> staffInfo = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		for (var item : staffInfo) {

			// 將上班時間轉成LocalDate 才可比對是否一樣
			LocalDate localDate = item.getWorkTime().toLocalDate();
			if (localDate.equals(absenteeismDate)) {
				return new WorkSystemRes("這位員工這天有上班或以記曠職過");
			}
		}

		// 上面都沒擋掉代表真ㄉ曠職了
		LocalDateTime absenteeismDateTime = absenteeismDate.atStartOfDay();
		WorkSystem workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), absenteeismDateTime,
				absenteeismDateTime, "曠職", 0);
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, "新增曠職成功");

	}

	/*------------------------------------------------(主管)刪除曠職行為*/
	@Override
	public WorkSystemRes deleteAbsenteeismForManager(WorkSystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		if (!StringUtils.hasText(req.getUuid())) {
			return new WorkSystemRes("參數值不能空");
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
			return new WorkSystemRes("刪除曠職成功");
		}
		return new WorkSystemRes("這位員工這天沒有曠職");
	}

	/*------------------------------------------------(員工)印出打卡資訊給下班打卡用*/
	@Override
	public WorkSystemRes getWorkInfoListToday(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new WorkSystemRes("參數值不能為空");
		}

		// 讓尾數變成00:00:00
		LocalDateTime nowDateTime = LocalDate.now().atStartOfDay();

		// 因為搜尋的是大於等於"今天"所以永遠只有一筆資料 不出意外的話 2ㄏ2ㄏ^^
		List<WorkSystem> workInfoList = workSystemDao
				.findByEmployeeCodeAndWorkTimeGreaterThanEqual(req.getEmployeeCode(), nowDateTime);
		if (workInfoList.isEmpty()) {
			return new WorkSystemRes("你是不是上班沒打卡!?");
		}
		res.setWorkInfoList(workInfoList);
		return res;
	}

	/*------------------------------------------------(主管)印出曠職資訊給刪除曠職行為用*/
	@Override
	public WorkSystemRes getWorkInfoListAbsenteeism(WorkSystemReq req) {
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		if (!StringUtils.hasText(req.getAbsenteeismDate()) || !StringUtils.hasText(req.getEmployeeCode())
				|| !StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("參數值或日期不能為空");
		}
		// 接近來是字串 先轉成Date
		LocalDate absenteeismDate = LocalDate.parse(req.getAbsenteeismDate(), formatDate);
		// 先取得主管與員工的編號
		Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getManagerEmployeeCode());
		Optional<EmployeeInfo> employeeManagerOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeStaffOp.isPresent() || !employeeManagerOp.isPresent()) {
			return new WorkSystemRes("請輸入正確編號可能沒有該員工");
		}

		// 兩位取值
		EmployeeInfo employeeStaffInfo = employeeStaffOp.get();
		EmployeeInfo employeeManagerInfo = employeeManagerOp.get();
		if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
			return new WorkSystemRes("您與該員工不同部門");
		}

		// 判斷要要記曠職者與被記曠職者的階級
		if (employeeManagerInfo.getLevel() < employeeStaffInfo.getLevel()) {
			return new WorkSystemRes("您的權限不夠");
		}
		
		// 尾數是00:00:00
		LocalDateTime absenteeismDateTime = absenteeismDate.atStartOfDay();
		// 因為新增曠職時 尾數是00:00:00 所以只要日期正確一定可以找到
		List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeAndWorkTime(req.getEmployeeCode(),
				absenteeismDateTime);
		if (workInfoList.isEmpty()) {
			return new WorkSystemRes("該員工這天可能沒有曠職");
		}
		return new WorkSystemRes(workInfoList, " 部門 : " + employeeManagerInfo.getSection());
	}

	/*------------------------------------------------(主管)員工沒打卡下班找主管補打下班卡*/
	@Override
	public WorkSystemRes updeateWorkOffTimeForManager(WorkSystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());

		// 前端接進來格式 YYYY-MM-DDTHH:MM
		DateTimeFormatter foemat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		if (!StringUtils.hasText(req.getUuid()) || !StringUtils.hasText(req.getOffWorkTime())) {
			return new WorkSystemRes("參數值不能空");
		}

		// 將T替換成空格 以便符合格式
		String reqDate = req.getOffWorkTime().replace('T', ' ');
		// 轉成LocalDateTime
		LocalDateTime offWorkTime = LocalDateTime.parse(reqDate, foemat);
		if (offWorkTime.getYear() != LocalDateTime.now().getYear()
				|| offWorkTime.getMonthValue() != LocalDateTime.now().getMonthValue()) {
			return new WorkSystemRes("超過" + offWorkTime.getMonthValue() + "月，不能補打卡");
		}

		// 請求亂碼uuid ， 因為上班時打過卡了，固會產生(前端顯示時，藉由"按鈕"取得亂碼)
		Optional<WorkSystem> workSystemOp = workSystemDao.findById(uuid);
		// 其實這裡的防呆沒啥意義，因為前端會顯示的資料 ，資料庫一定有該uuid
		if (!workSystemOp.isPresent()) {
			return new WorkSystemRes("參數值錯誤");
		}
		WorkSystem workSystem = workSystemOp.get();

		// 要判斷打卡上班時間是不是在下班之前 還有 打卡上班是不是跟他補打卡的時間是不是"同一天"
		if ((offWorkTime.isBefore(workSystem.getWorkTime()))
				|| offWorkTime.getYear() != workSystem.getWorkTime().getYear()
				|| offWorkTime.getMonthValue() != workSystem.getWorkTime().getMonthValue()
				|| offWorkTime.getDayOfMonth() != workSystem.getWorkTime().getDayOfMonth()) {
			return new WorkSystemRes("請輸入正確日期時間");
		}

		if (workSystem.getOffWorkTime() != null) {
			return new WorkSystemRes("這位員工有打卡");
		}
		// 正常上下班的時數(現在時間-上班時間)
		int countOffWorkHours = offWorkTime.getHour() - 9;

		// 遲到或早退時的計算時數 (下班-他來的時間)
		int countWorkLateOrLeaveTime = offWorkTime.getHour() - workSystem.getWorkTime().getHour();

		// 字串更新狀況
		String attendanceStatusStr;

		// 第一種:遲到+早退 = (上班時間 >= 9:00 & 上班分鐘 >= 1 & 下班小時 < 18) ||(上班時間 > 9:00 & 下班時間 <
		// 18)
		// 1 = > (ps.計入時數為 : 遲到的時數 ps.18=幾點下班)
		// 第二種:遲到 =上班小時 >= 9:00 & 上班分鐘 >= 1 (為了防9:00) 或是 上班小時 > 9:00 (ps.計入時數為 : 遲到的時數)
		// 2 = >防(9:00)或大於(10:00)的情況
		// 第三種:早退 = 下班小時小於18 # 早退沒有遲到
		// 3 => ps.計入時數為正常時數
		// 第四種:正常 = 上述都不合 (ps.計入時數為 :正常的時數)

		if (((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				&& offWorkTime.getHour() < 18)
				|| (workSystem.getWorkTime().getHour() > 9 && offWorkTime.getHour() < 18)) {
			attendanceStatusStr = "遲到+早退";

			// 防止 EX: 7 : 55 打卡上班 8:00 打卡下班 ，得出的遲到時數卻是 1 所以 超過 x : 30 直接 x+1
			if (workSystem.getWorkTime().getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}

			// 上述情況發生被-1時可能會低於0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem.setOffWorkTime(offWorkTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);

		} else if ((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				|| workSystem.getWorkTime().getHour() > 9) {
			attendanceStatusStr = "遲到";

			// 防止 EX: 7 : 55 打卡上班 8:00 打卡下班 ，得出的遲到時數卻是 1 所以 超過 x : 30 直接 x+1
			if (workSystem.getWorkTime().getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}

			// 上述情況發生被-1時可能會低於0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem.setOffWorkTime(offWorkTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);
		} else if (countOffWorkHours < 8) {
			attendanceStatusStr = "早退";

			// 以防出現 8:00打卡 9:00打卡下班
			if (countOffWorkHours <= 0) {
				countOffWorkHours = 0;
			}

			workSystem.setOffWorkTime(offWorkTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		} else {
			attendanceStatusStr = "正常";
			workSystem.setOffWorkTime(offWorkTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		}
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, "下次請記得打卡");
	}

	/*------------------------------------------------(主管)員工沒打卡下班找主管補打卡*/
	@Override
	public WorkSystemRes forgotToPunchCard(WorkSystemReq req) {
		// 前端接進來格式 YYYY-MM-DDTHH:MM
		DateTimeFormatter foematDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		if (!StringUtils.hasText(req.getEmployeeCode()) || !StringUtils.hasText(req.getManagerEmployeeCode())
				|| !StringUtils.hasText(req.getPunchWorkTime()) || !StringUtils.hasText(req.getOffWorkTime())) {
			return new WorkSystemRes("參數值不能為空");
		}

		String workDateTimeString = req.getPunchWorkTime().replace('T', ' ');
		String offWorkDateTimeString = req.getOffWorkTime().replace('T', ' ');

		// 轉成LocalDateTime
		LocalDateTime workDateTime = LocalDateTime.parse(workDateTimeString, foematDateTime);
		LocalDateTime offWorkDateTime = LocalDateTime.parse(offWorkDateTimeString, foematDateTime);
		if (workDateTime.getYear() != LocalDateTime.now().getYear()
				|| workDateTime.getMonthValue() != LocalDateTime.now().getMonthValue()) {
			return new WorkSystemRes("超過" + workDateTime.getMonthValue() + "月，不能補打卡");
		}

		Optional<EmployeeInfo> managerEmployeeInfoOp = employeeInfoDao.findById(req.getManagerEmployeeCode());
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());

		if (!employeeInfoOp.isPresent() || !managerEmployeeInfoOp.isPresent()) {
			return new WorkSystemRes("請檢察員工編號");
		}

		EmployeeInfo employeeInfo = employeeInfoOp.get();
		EmployeeInfo managerEmployeeInfo = managerEmployeeInfoOp.get();

		if (!employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
			return new WorkSystemRes("你們不同部門");
		}

		// 接近來是字串 先轉成Date
		LocalDate workDate = workDateTime.toLocalDate();

		if ((offWorkDateTime.isBefore(workDateTime)) || offWorkDateTime.getYear() != workDateTime.getYear()
				|| offWorkDateTime.getMonthValue() != workDateTime.getMonthValue()
				|| offWorkDateTime.getDayOfMonth() != workDateTime.getDayOfMonth()) {
			return new WorkSystemRes("請輸入正確日期時間");
		}

		// 藉由員工編號撈資料(判斷有無重複打卡)
		List<WorkSystem> staffInfo = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		// 年分、月份、日 都一樣時 代表打過卡了
		for (WorkSystem item : staffInfo) {
			LocalDate localDate = item.getWorkTime().toLocalDate();
			if (localDate.equals(workDate)) {
				return new WorkSystemRes("這位員工在" + workDate + "有打卡，不需要補");
			}
		}

		// 正常上下班的時數(現在時間-上班時間)
		int countOffWorkHours = offWorkDateTime.getHour() - 9;

		// 遲到或早退時的計算時數 (下班-他來的時間)
		int countWorkLateOrLeaveTime = offWorkDateTime.getHour() - workDateTime.getHour();

		// 字串更新狀況
		String attendanceStatusStr;
		WorkSystem workSystem = new WorkSystem();

		// 第一種:遲到+早退 = (上班時間 >= 9:00 & 上班分鐘 >= 1 & 下班小時 < 18) ||(上班時間 > 9:00 & 下班時間 <
		// 18)
		// 1 = > (ps.計入時數為 : 遲到的時數 ps.18=幾點下班)
		// 第二種:遲到 =上班小時 >= 9:00 & 上班分鐘 >= 1 (為了防9:00) 或是 上班小時 > 9:00 (ps.計入時數為 : 遲到的時數)
		// 2 = >防(9:00)或大於(10:00)的情況
		// 第三種:早退 = 下班小時小於18 # 早退沒有遲到
		// 3 => ps.計入時數為正常時數
		// 第四種:正常 = 上述都不合 (ps.計入時數為 :正常的時數)

		if (((workDateTime.getHour() >= 9 && workDateTime.getMinute() >= 1) && offWorkDateTime.getHour() < 18)
				|| (workDateTime.getHour() > 9 && offWorkDateTime.getHour() < 18)) {
			attendanceStatusStr = "遲到+早退";

			// 防止 EX: 7 : 55 打卡上班 8:00 打卡下班 ，得出的遲到時數卻是 1 所以 超過 x : 30 直接 x+1
			if (workDateTime.getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}

			// 上述情況發生被-1時可能會低於0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), workDateTime, offWorkDateTime,
					attendanceStatusStr, countWorkLateOrLeaveTime);

		} else if ((workDateTime.getHour() >= 9 && workDateTime.getMinute() >= 1) || workDateTime.getHour() > 9) {
			attendanceStatusStr = "遲到";

			// 防止 EX: 7 : 55 打卡上班 8:00 打卡下班 ，得出的遲到時數卻是 1 所以 超過 x : 30 直接 x+1
			if (workDateTime.getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}

			// 上述情況發生被-1時可能會低於0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), workDateTime, offWorkDateTime,
					attendanceStatusStr, countWorkLateOrLeaveTime);
		} else if (countOffWorkHours < 8) {
			attendanceStatusStr = "早退";

			// 以防出現 8:00打卡 9:00打卡下班
			if (countOffWorkHours <= 0) {
				countOffWorkHours = 0;
			}
			workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), workDateTime, offWorkDateTime,
					attendanceStatusStr, countOffWorkHours);
		} else {
			attendanceStatusStr = "正常";
			workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), workDateTime, offWorkDateTime,
					attendanceStatusStr, countOffWorkHours);
		}
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, "感謝主管，下次請記得打卡");

	}

}
