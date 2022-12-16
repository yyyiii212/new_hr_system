package com.example.new_hr_system.service.impl;

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
		SalarySystemRes res = new SalarySystemRes();
		if (!StringUtils.hasText(req.getEmployeeCode()) || !StringUtils.hasText(req.getSalaryDate())) {
			res.setMessage("參數不能空");
			SalarySystem salarySystem = null;
			return new SalarySystemRes(salarySystem, res.getMessage());
		}
		String checkYearAndMonth = "^[1-9]\\d{3}年(0[1-9]|1[0-2]|[1-9])月([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])日";
		boolean checkDate = req.getSalaryDate().matches(checkYearAndMonth);
		if (!checkDate) {
			res.setMessage("格式為yyyy年mm月dd日");
			return new SalarySystemRes(res.getMessage());
		}
		return null;

	}

//===============================================================================

	// =====新增薪水資料
	@Override
	public SalarySystemRes creatSalarySystem(SalarySystemReq req) {
		SalarySystemRes res = check(req);
		// 防呆 (日期正規表達)、員工編號空值
		if (res != null) {
			return res;
		}
		res = new SalarySystemRes();

		// 要藉由這張表的資料取得需要的資訊 計算 1.年資 2.姓名 3.主管等級
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeInfoOp.isPresent()) {
			res.setMessage("找不到該員工");
			return new SalarySystemRes(res.getMessage());
		}

		// 避免新增到同一位員工 在同年同月有兩筆相同資料
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());
		// 轉日期的正規表達
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy年M月d日");

		// 將接近來的日期<字串>轉為日期
		LocalDate salaryDate = LocalDate.parse(req.getSalaryDate(), format);

		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				res.setMessage("以新增過這位員工該年、該月的薪水資料");
				return new SalarySystemRes(res.getMessage());
			}
		}

		// 需要計算工作時數
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		if (workSystemList.isEmpty()) {
			res.setMessage("找不到該員工的打卡資料");
			return new SalarySystemRes(res.getMessage());
		}

		// 先new出來
		SalarySystem salarySystem = new SalarySystem();

		// 因為這裡要取得預設的底薪
		int salary = salarySystem.getSalary();

		// 將EmployeeInfo這張表的資訊拿出來
		EmployeeInfo employeeInfo = employeeInfoOp.get();

		// 拿出年資
		int seniority = employeeInfo.getSeniority();

		// 滿一年加一千
		for (int i = 1; i <= seniority; i++) {
			salary += 1000;
		}

		// 接出總共工作時數
		int workHours = 0;

		// 逞罰扣薪
		int salaryDeduct = 0;

		for (var item : workSystemList) {
			if (item.getWorkTime().getYear() == salaryDate.getYear()
					&& item.getWorkTime().getMonthValue() == salaryDate.getMonthValue()) {
				workHours += item.getAttendanceHours();
				if (item.getAttendanceStatus().contains("遲到")) {
					salaryDeduct = salaryDeduct - 500;
				}
				if (item.getAttendanceStatus().contains("曠職")) {
					salaryDeduct = salaryDeduct - 1000;
				}
			}
		}

		if (workHours == 0) {
			res.setMessage("找不到該員工的上班時間");
			return new SalarySystemRes(res.getMessage());
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
		res.setMessage("新增成功");
		return new SalarySystemRes(finalSalarySystem, res.getMessage());
	}

	// =====修改薪水資料 (基本上只能修改底薪)，無視年資
	@Override
	public SalarySystemRes updateSalarySystem(SalarySystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		SalarySystemRes res = new SalarySystemRes();

		// 沒有輸入日期為true，沒有輸入代表要修改底薪
		boolean checkDateIsNull = !StringUtils.hasText(req.getSalaryDate());

		// 沒有輸入底薪為true
		boolean checkSalaryIsNull = req.getSalary() == null;

		if (!checkSalaryIsNull && req.getSalary() <= 0) {
			res.setMessage("底薪不可小於 0 ");
			return new SalarySystemRes(res.getMessage());
		}

		if (!StringUtils.hasText(req.getUuid()) || (checkDateIsNull && checkSalaryIsNull)) {
			res.setMessage("參數不能空");
			return new SalarySystemRes(res.getMessage());
		}

		// 透過uuid取得該員工資訊
		Optional<SalarySystem> salarySystemOp = salarySystemDao.findById(uuid);
		if (!salarySystemOp.isPresent()) {
			res.setMessage("錯誤資訊 (這個防呆沒什麼意義)");
			return new SalarySystemRes(res.getMessage());
		}
		// 取值
		SalarySystem salarySystem = salarySystemOp.get();

		// 因為底薪被修改，所以要重新計算總薪水
		int total = 0;

		// 沒有輸入日期為true
		if (checkDateIsNull) {
			total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
					+ (salarySystem.getSalaryDeduct());
			salarySystem.setSalary(req.getSalary());
			salarySystem.setTotalSalary(total);
			salarySystemDao.save(salarySystem);
			res.setMessage("修改底薪成功");
			return new SalarySystemRes(salarySystem, res.getMessage());
		}

		// 上面沒有擋掉代表一訂有輸入日期，故要規定日期的正規表達
		String checkDateString = "^[1-9]\\d{3}年(0[1-9]|1[0-2]|[1-9])月([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])日";

		// 判斷日期是否符合格式
		boolean checkDate = req.getSalaryDate().matches(checkDateString);
		if (!checkDate) {
			res.setMessage("格式為yyyy年mm月dd日");
			return new SalarySystemRes(res.getMessage());
		}

		// 日期的正規表達
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy年M月d日");

		/// 將接近來的日期字串轉成日期
		LocalDate salaryDate = LocalDate.parse(req.getSalaryDate(), format);

		// 為了以防修改的日期有重複的情況，撈出來防呆
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		// 防呆
		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				res.setMessage("以新增過這位員工該年分、該月份的薪水資料");
				salarySystem = null;
				return new SalarySystemRes(salarySystem, res.getMessage());
			}
		}

		// 要撈出輸入的這個月有沒有上班，用時數判斷
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		// 計算總工作時數
		int workHours = 0;

		for (var item : workSystemList) {
			if (item.getWorkTime().getYear() == salaryDate.getYear()
					&& item.getWorkTime().getMonthValue() == salaryDate.getMonthValue()) {
				workHours += item.getAttendanceHours();
			}
		}

		if (workHours <= 0) {
			res.setMessage("找不到該員工的上班時間");
			return new SalarySystemRes(res.getMessage());
		}

		// 沒有輸入底薪為true，代表只要修改日期
		if (checkSalaryIsNull) {
			salarySystem.setSalaryDate(salaryDate);
			salarySystemDao.save(salarySystem);
			res.setMessage("修改日期成功");
			return new SalarySystemRes(salarySystem, res.getMessage());
		}

		// 上面都沒擋掉，代表兩個皆要修改
		total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
				+ (salarySystem.getSalaryDeduct());
		salarySystem.setSalaryDate(salaryDate);
		salarySystem.setSalary(req.getSalary());
		salarySystemDao.save(salarySystem);
		res.setMessage("修改日期、薪資成功");
		return new SalarySystemRes(salarySystem, res.getMessage());
	}

	// =====搜尋資料 (給員工的) ps.員工只能搜尋到自己的資料
	@Override
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req) {
		SalarySystemRes res = new SalarySystemRes();
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());

		String checkDateString = "^[1-9]\\d{3}年(0[1-9]|1[0-2]|[1-9])月([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])日";

		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy年M月d日");

		if (!checkEmployeeCode || (!checkEmployeeCode && checkSearchStartDate && checkSearchEndDate)) {
			res.setMessage("參數不能空");
			return new SalarySystemRes(res.getMessage());
		}

		if (checkSearchEndDate && !checkSearchStartDate) {
			res.setMessage("請輸入開始時間");
			return new SalarySystemRes(res.getMessage());
		}

		if (checkSearchStartDate && checkSearchEndDate) {

			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			boolean checkSearchMonthEndString = req.getSearchEndDate().matches(checkDateString);
			if (!checkSearchYearStaetString && !checkSearchMonthEndString) {
				res.setMessage("格式為yyyy年mm月dd日");
				return new SalarySystemRes(res.getMessage());
			}
		}

		if (checkSearchStartDate) {

			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			if (!checkSearchYearStaetString) {
				res.setMessage("格式為yyyy年mm月dd日");
				return new SalarySystemRes(res.getMessage());
			}
		}

		if (!checkSearchStartDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeOrderBySalaryDateDesc(req.getEmployeeCode());
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("查無資料");
				return new SalarySystemRes(res.getMessage());
			}

			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		if (checkSearchStartDate && checkSearchEndDate) {

			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				res.setMessage("結束時間不可小於開始時間");
				return new SalarySystemRes(res.getMessage());
			}

			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							endDate);
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("查無資料");
				return new SalarySystemRes(res.getMessage());
			}

			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		if (LocalDate.now().isBefore(startDate)) {
			res.setMessage("今天時間不可小於開始時間");
			return new SalarySystemRes(res.getMessage());
		}

		List<SalarySystem> salarySystemListInfo = salarySystemDao
				.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
						LocalDate.now());
		if (salarySystemListInfo.isEmpty()) {
			res.setMessage("查無資料");
			return new SalarySystemRes(res.getMessage());
		}
		res.setSalarySystemList(salarySystemListInfo);
		return res;
	}

	@Override
	public SalarySystemRes searchSalarySystemForManager(SalarySystemReq req) {
		SalarySystemRes res = new SalarySystemRes();
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());

		String checkDateString = "^[1-9]\\d{3}年(0[1-9]|1[0-2]|[1-9])月([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])日";

		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy年M月d日");

		if (!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate) {
			res.setMessage("參數不能空");
			return new SalarySystemRes(res.getMessage());
		}

		if (checkSearchEndDate && !checkSearchStartDate) {
			res.setMessage("請輸入開始時間");
			return new SalarySystemRes(res.getMessage());
		}

		if (checkSearchStartDate && checkSearchEndDate) {

			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			boolean checkSearchMonthEndString = req.getSearchEndDate().matches(checkDateString);
			if (!checkSearchYearStaetString && !checkSearchMonthEndString) {
				res.setMessage("格式為yyyy年mm月dd日");
				return new SalarySystemRes(res.getMessage());
			}
		}

		if (checkSearchStartDate) {

			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			if (!checkSearchYearStaetString) {
				res.setMessage("格式為yyyy年mm月dd日");
				return new SalarySystemRes(res.getMessage());
			}
		}

		if (checkEmployeeCode && !checkSearchStartDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("查無資料");
				return new SalarySystemRes(res.getMessage());
			}
			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		if (checkEmployeeCode && checkSearchStartDate && checkSearchEndDate) {
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				res.setMessage("結束時間不可小於開始時間");
				return new SalarySystemRes(res.getMessage());
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							endDate);
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("查無資料");
				return new SalarySystemRes(res.getMessage());
			}
			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		if (checkEmployeeCode && checkSearchStartDate) {
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			if (LocalDate.now().isBefore(startDate)) {
				res.setMessage("今天時間不可小於開始時間");
				return new SalarySystemRes(res.getMessage());
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							LocalDate.now());
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("查無資料");
				return new SalarySystemRes(res.getMessage());
			}
			res.setSalarySystemList(salarySystemListInfo);
			return res;

		}

		if (checkSearchStartDate && checkSearchEndDate) {
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				res.setMessage("結束時間不可小於開始時間");
				return new SalarySystemRes(res.getMessage());
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findBySalaryDateBetweenOrderBySalaryDateDesc(startDate, endDate);
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("查無資料");
				return new SalarySystemRes(res.getMessage());
			}
			res.setSalarySystemList(salarySystemListInfo);
			return res;

		}

		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		if (LocalDate.now().isBefore(startDate)) {
			res.setMessage("今天時間不可小於開始時間");
			return new SalarySystemRes(res.getMessage());
		}
		List<SalarySystem> salarySystemListInfo = salarySystemDao
				.findBySalaryDateBetweenOrderBySalaryDateDesc(startDate, LocalDate.now());
		if (salarySystemListInfo.isEmpty()) {
			res.setMessage("查無資料");
			return new SalarySystemRes(res.getMessage());
		}
		res.setSalarySystemList(salarySystemListInfo);
		return res;
	}

}
