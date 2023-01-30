package com.example.new_hr_system.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.entity.SalarySystem;
import com.example.new_hr_system.entity.WorkSystem;
import com.example.new_hr_system.respository.AbsenceSystemDao;
import com.example.new_hr_system.respository.EmployeeInfoDao;
import com.example.new_hr_system.respository.SalarySystemDao;
import com.example.new_hr_system.respository.WorkSystemDao;
import com.example.new_hr_system.service.ifs.SalarySystemService;
import com.example.new_hr_system.vo.SalarySystemReq;
import com.example.new_hr_system.vo.SalarySystemRes;
import com.example.new_hr_system.vo.WorkSystemRes;

@Service
public class SalarySystemServiceImpl implements SalarySystemService {
	@Autowired
	private AbsenceSystemDao absenceSystemDao;

	@Autowired
	private EmployeeInfoDao employeeInfoDao;

	@Autowired
	private SalarySystemDao salarySystemDao;

	@Autowired
	private WorkSystemDao workSystemDao;

	// --------------------------------------------------------
	private SalarySystemRes check(SalarySystemReq req) {
		if (!StringUtils.hasText(req.getEmployeeCode()) || req.getSalaryDate() == null
				|| !StringUtils.hasText(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("參數不能空");
		}
		return null;

	}

//===============================================================================

	/*------------------------------------------------(主管)新增薪水資料*/
	@Override
	public SalarySystemRes creatSalarySystem(SalarySystemReq req) {
		SalarySystemRes res = check(req);

		// 防呆 (日期正規表達)、員工編號空值
		if (res != null) {
			return res;
		}
		res = new SalarySystemRes();

		// 要判斷該主管是否跟員工同一個部門
		Optional<EmployeeInfo> salaryEmployeeInfoOp = employeeInfoDao.findById(req.getSalaryEmployeeCode());
		if (!salaryEmployeeInfoOp.isPresent()) {
			return new SalarySystemRes("請檢察您的編號");
		}

		EmployeeInfo managerEmployeeInfo = salaryEmployeeInfoOp.get();

		// 要判斷該主管是否跟員工同一個部門
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeInfoOp.isPresent()) {
			return new SalarySystemRes("找不到該員工");
		}

		EmployeeInfo employeeInfo = employeeInfoOp.get();

		if (!managerEmployeeInfo.getSection().equals(employeeInfo.getSection())) {
			return new SalarySystemRes("你們是不同的部門");
		}

		// 判斷新增者與被新增者的等級
		if (managerEmployeeInfo.getLevel() < employeeInfo.getLevel()) {
			return new SalarySystemRes("你的權限不夠");
		}

		// 避免新增到同一位員工 在同年同月有兩筆相同資料
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());
		LocalDate salaryDate = req.getSalaryDate();

		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				return new SalarySystemRes("以新增過這位員工該年、該月的薪水資料");
			}
		}
		String message = null;
		int month = 0;
		switch (salaryDate.getMonthValue()) {
		case 1: {
			month = 12;
			message = "請確認該員工是否在去年" + month + "月 有出勤";
			break;
		}
		default: {
			message = "請確認該員工是否在" + (salaryDate.getMonthValue() - 1) + "月 有出勤";
			break;
		}
		}
		// 需要計算工作時數
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		if (workSystemList.isEmpty()) {
			return new SalarySystemRes(message);
		}

		// 先new出來
		SalarySystem salarySystem = new SalarySystem();

		// 因為這裡要取得預設的底薪
		int salary = salarySystem.getSalary();

		// 拿出年資
		int seniority = employeeInfo.getSeniority();

		// 滿一年加一千
		for (int i = 1; i <= seniority; i++) {
			salary += 1000;
		}

		// 預備接出總共工作時數
		int workHours = 0;

		// 逞罰扣薪
		int salaryDeduct = 0;

		// 判斷是否是"過去一個月"的工作時數等資訊 同時判斷年是否符合 或是 當遇到跨年時 工作年+1 & 工作月==12 & 薪資月==1
		for (var item : workSystemList) {
			if ((item.getWorkTime().getYear() == salaryDate.getYear()
					&& item.getWorkTime().getMonthValue() + 1 == salaryDate.getMonthValue())
					|| (item.getWorkTime().getYear() + 1 == salaryDate.getYear()
							&& item.getWorkTime().getMonthValue() == 12 && salaryDate.getMonthValue() == 1)) {

				// 計算工作總時數
				workHours += item.getAttendanceHours();

				// 三種逞罰
				if (item.getAttendanceStatus() == null || item.getAttendanceStatus().length() == 0) {
					salaryDeduct = salaryDeduct - 100;// 忘了打卡
					continue;
				}
				if (item.getAttendanceStatus().contains("遲到")) {
					salaryDeduct = salaryDeduct - 500;
				}
				if (item.getAttendanceStatus().contains("曠職")) {
					salaryDeduct = salaryDeduct - 1000;
				}
			}
		}

		// 有個情況會進來 9:00打卡不到10:00就閃人
		if (workHours == 0) {
			return new SalarySystemRes(message);
		}

		// 一般加給 (總時數-時數上限) 加班一小時給一百
		int raisePay = (workHours - 174) * 100;

		// 當沒有加班有可能是負數，故要歸零
		if (raisePay <= 0) {
			raisePay = raisePay * 0;
		}

		// 判斷主管階層，1層加五千
		int managerRaisePay = employeeInfo.getLevel() * 5000;

		// 計算總薪水
		int totalSalary = salary + raisePay + managerRaisePay + (salaryDeduct);

		SalarySystem finalSalarySystem = new SalarySystem(UUID.randomUUID(), req.getEmployeeCode(),
				employeeInfo.getName(), salaryDate, salary, raisePay, managerRaisePay, salaryDeduct, totalSalary);

		salarySystemDao.save(finalSalarySystem);
		return new SalarySystemRes(finalSalarySystem, "新增成功");
	}

	/*------------------------------------------------(主管)修改薪水資料*/
	@Override
	public SalarySystemRes updateSalarySystem(SalarySystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		if (!StringUtils.hasText(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("請輸入主管編號");
		}

		// 沒有輸入日期為true，沒有輸入代表要修改底薪
		boolean checkDateIsNull = false;
		if (req.getSalaryDate() == null) {
			checkDateIsNull = true;
		}

		// 沒有輸入底薪為true
		boolean checkSalaryIsNull = req.getSalary() == null;

		// 若是把布林值拿掉他沒有找到薪資的情況下會錯誤
		if (!checkSalaryIsNull && req.getSalary() <= 0) {
			return new SalarySystemRes("底薪不可小於 0 ");
		}

		// 全部都沒輸入
		if (!StringUtils.hasText(req.getUuid()) || (checkDateIsNull && checkSalaryIsNull)) {
			return new SalarySystemRes("參數不能空");
		}

		// 透過uuid取得該員工資訊
		Optional<SalarySystem> salarySystemOp = salarySystemDao.findById(uuid);
		if (!salarySystemOp.isPresent()) {
			return new SalarySystemRes("錯誤資訊 (這個防呆沒什麼意義)");
		}

		// 取值
		SalarySystem salarySystem = salarySystemOp.get();

		// 因為底薪被修改，所以要重新計算總薪水
		int total = 0;

		// 沒有輸入日期為true (代表修改底薪)
		if (checkDateIsNull) {
			total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
					+ (salarySystem.getSalaryDeduct());
			salarySystem.setSalary(req.getSalary());
			salarySystem.setTotalSalary(total);
			salarySystemDao.save(salarySystem);
			return new SalarySystemRes(salarySystem, "修改底薪成功");
		}

		// 上面沒有擋掉代表一訂有輸入日期，故要規定日期的正規表達
		// 判斷日期是否符合格式
		LocalDate salaryDate = req.getSalaryDate();// 黃色比較好看 ^^

		// 判斷原本的月份有沒有跟他輸入的 年分、月份 不一樣
		if (salarySystem.getSalaryDate().getMonthValue() != salaryDate.getMonthValue()
				|| salarySystem.getSalaryDate().getYear() != salaryDate.getYear()) {
			return new SalarySystemRes("請先去新增(只能修改到<日>)");
		}
		// 沒有輸入底薪為true，代表只要修改日期
		if (checkSalaryIsNull) {
			salarySystem.setSalaryDate(salaryDate);
			salarySystemDao.save(salarySystem);
			return new SalarySystemRes(salarySystem, "修改日期成功");
		}
		// 上面都沒擋掉，代表兩個皆要修改
		total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
				+ (salarySystem.getSalaryDeduct());
		salarySystem.setSalaryDate(salaryDate);
		salarySystem.setSalary(req.getSalary());
		salarySystem.setTotalSalary(total);
		salarySystemDao.save(salarySystem);
		return new SalarySystemRes(salarySystem, "修改日期、薪資成功");
	}

	/*------------------------------------------------(員工)查詢薪水資料*/
	@Override
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req) {
		SalarySystemRes res = new SalarySystemRes();
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		boolean checkSearchStartDate = req.getSearchStartDate() != null;
		boolean checkSearchEndDate = req.getSearchEndDate() != null;
		if (!checkEmployeeCode) {
			return new SalarySystemRes("請輸入員工編號");
		}

		if (checkSearchEndDate && !checkSearchStartDate) {
			return new SalarySystemRes("請輸入開始時間");
		}

		if (!checkSearchStartDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeOrderBySalaryDateDesc(req.getEmployeeCode());
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("查無資料");
			}

			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		if (checkSearchStartDate && checkSearchEndDate) {
			LocalDate startDate = req.getSearchStartDate();
			LocalDate endDate = req.getSearchEndDate();
			if (endDate.isBefore(startDate)) {
				return new SalarySystemRes("開始時間不可小於結束時間");
			}

			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							endDate);
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("查無資料");
			}

			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		LocalDate startDate = req.getSearchStartDate();
		if (LocalDate.now().isBefore(startDate)) {
			return new SalarySystemRes("開始時間不可大於今天時間");
		}

		List<SalarySystem> salarySystemListInfo = salarySystemDao
				.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
						LocalDate.now());
		if (salarySystemListInfo.isEmpty()) {
			return new SalarySystemRes("查無資料");
		}
		res.setSalarySystemList(salarySystemListInfo);
		return res;
	}

	/*------------------------------------------------(主管)查詢薪水資料*/
	@Override
	public SalarySystemRes searchSalarySystemForManager(SalarySystemReq req) {
		// 有輸入員工編號 true
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		// 有輸入開始日期true
		boolean checkSearchStartDate = req.getSearchStartDate() != null;
		// 有輸入結束日期true
		boolean checkSearchEndDate = req.getSearchEndDate() != null;
		// 準備接過濾後的值
		List<SalarySystem> salaryList = new ArrayList<>();

		if (!StringUtils.hasText(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("請輸入主管編號");
		}

		// 檢查主管編號 後面要判斷部門
		Optional<EmployeeInfo> managerEmployeeInfoOp = employeeInfoDao.findById(req.getSalaryEmployeeCode());
		if (!managerEmployeeInfoOp.isPresent()) {
			return new SalarySystemRes("找不到該主管");
		}
		EmployeeInfo managerEmployeeInfo = managerEmployeeInfoOp.get();

		// 搜尋員工編號都、開始日期、結束日期都沒輸入 (需過慮)
		if (!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao.findByOrderBySalaryDateDesc();
			for (var item : salarySystemListInfo) {
				Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(item.getEmployeeCode());

				// 因為逸祥那裏會刪除自己的資料，我這裡找不到東西會抱錯，固要在他找不到時從頭迴圈
				if (!employeeInfoOp.isPresent()) {
					continue;
				}

				// 沒有從頭就取值
				EmployeeInfo employeeInfo = employeeInfoOp.get();

				// 過濾部門
				if (employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
					salaryList.add(item);
				}
			}
			return new SalarySystemRes(salaryList, "部門 : " + managerEmployeeInfo.getSection());
		}

		// 判斷只有結束日期、沒有開始日期
		if (checkSearchEndDate && !checkSearchStartDate) {
			return new SalarySystemRes("請輸入開始時間");
		}

		// 有輸入員工編號、沒有開始日期 (因為這裡掉進去代表會有員工編號)
		if (checkEmployeeCode && !checkSearchStartDate) {
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeInfoOp.isPresent()) {
				return new SalarySystemRes("請確認員工編號");
			}
			// 取值後判斷是否同部門即可
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (!employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				return new SalarySystemRes("您與員工不同部門");
			}

			List<SalarySystem> salarySystemListInfo = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("查無資料");
			}
			return new SalarySystemRes(salarySystemListInfo, "部門 : " + managerEmployeeInfo.getSection());
		}

		// 有員工編號、開始日、結束日期
		if (checkEmployeeCode && checkSearchStartDate && checkSearchEndDate) {
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeInfoOp.isPresent()) {
				return new SalarySystemRes("請確認員工編號");
			}
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (!employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				return new SalarySystemRes("您與這位員工不同部門");
			}

			// 黃色比較好看
			LocalDate startDate = req.getSearchStartDate();
			LocalDate endDate = req.getSearchEndDate();

			if (endDate.isBefore(startDate)) {
				return new SalarySystemRes("結束時間不可小於開始時間");
			}

			// 回傳時間區間資料
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							endDate);
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("查無資料");
			}
			return new SalarySystemRes(salarySystemListInfo, "部門 : " + managerEmployeeInfo.getSection());
		}

		// 有員工編號、開始日期
		if (checkEmployeeCode && checkSearchStartDate) {
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeInfoOp.isPresent()) {
				return new SalarySystemRes("請確認員工編號");
			}
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (!employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				return new SalarySystemRes("您與這位員工不同部門");
			}
			LocalDate startDate = req.getSearchStartDate();
			if (LocalDate.now().isBefore(startDate)) {
				return new SalarySystemRes("開始時間不可大於今天時間");
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							LocalDate.now());
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("查無資料");
			}
			return new SalarySystemRes(salarySystemListInfo, "部門 : " + managerEmployeeInfo.getSection());

		}

		// 搜尋開始日期、結束日期 <上面都沒擋掉，說明一定沒有指定員工編號> (要過濾)
		if (checkSearchStartDate && checkSearchEndDate) {
			// 黃色比較好看
			LocalDate startDate = req.getSearchStartDate();
			LocalDate endDate = req.getSearchEndDate();

			if (endDate.isBefore(startDate)) {
				return new SalarySystemRes("開始時間不可大於結束時間");
			}
			// 先找出時間區間資料
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findBySalaryDateBetweenOrderBySalaryDateDesc(startDate, endDate);
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("查無資料");
			}
			// 過濾同部門
			for (var item : salarySystemListInfo) {
				Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(item.getEmployeeCode());
				if (!employeeInfoOp.isPresent()) {
					continue;
				}
				EmployeeInfo employeeInfo = employeeInfoOp.get();
				if (employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
					salaryList.add(item);
				}
			}
			return new SalarySystemRes(salaryList, "部門 : " + managerEmployeeInfo.getSection());
		}

		// 上面都沒擋掉，說明只有輸入開始日期 (需過慮)
		LocalDate startDate = req.getSearchStartDate();
		if (LocalDate.now().isBefore(startDate)) {
			return new SalarySystemRes("開始時間不可大於今天時間");
		}
		List<SalarySystem> salarySystemListInfo = salarySystemDao
				.findBySalaryDateBetweenOrderBySalaryDateDesc(startDate, LocalDate.now());
		if (salarySystemListInfo.isEmpty()) {
			return new SalarySystemRes("查無資料");
		}
		for (var item : salarySystemListInfo) {
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(item.getEmployeeCode());
			if (!employeeInfoOp.isPresent()) {
				continue;
			}
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				salaryList.add(item);
			}
		}
		return new SalarySystemRes(salaryList, "部門 : " + managerEmployeeInfo.getSection());
	}

}
