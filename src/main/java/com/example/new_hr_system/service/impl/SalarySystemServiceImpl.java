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

	// =====新增薪水資料(給主管)
	@Override
	public SalarySystemRes creatSalarySystem(SalarySystemReq req) {
		SalarySystemRes res = check(req);
		// 防呆 (日期正規表達)、員工編號空值
		if (res != null) {
			return res;
		}
		res = new SalarySystemRes();
		Optional<EmployeeInfo> salaryEmployeeInfoOp = employeeInfoDao.findById(req.getSalaryEmployeeCode());
		if (!salaryEmployeeInfoOp.isPresent()) {
			return new SalarySystemRes("請檢察您的編號");
		}
		EmployeeInfo managerEmployeeInfo = salaryEmployeeInfoOp.get();
		// 要藉由這張表的資料取得需要的資訊 計算 1.年資 2.姓名 3.主管等級
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeInfoOp.isPresent()) {
			return new SalarySystemRes("找不到該員工");
		}
		EmployeeInfo employeeInfo = employeeInfoOp.get();
		if (!managerEmployeeInfo.getSection().equals(employeeInfo.getSection())) {
			return new SalarySystemRes("你們是不同的部門");
		}

		// 避免新增到同一位員工 在同年同月有兩筆相同資料
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());

		// 將接近來的日期<字串>轉為日期
		LocalDate salaryDate = req.getSalaryDate();

		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				return new SalarySystemRes("以新增過這位員工該年、該月的薪水資料");
			}
		}

		// 需要計算工作時數
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		if (workSystemList.isEmpty()) {
			return new SalarySystemRes("找不到該員工的打卡資料");
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

		// 接出總共工作時數
		int workHours = 0;

		// 逞罰扣薪
		int salaryDeduct = 0;

		for (var item : workSystemList) {
			if ((item.getWorkTime().getYear() == salaryDate.getYear()
					&& item.getWorkTime().getMonthValue() + 1 == salaryDate.getMonthValue())
					|| (item.getWorkTime().getYear() + 1 == salaryDate.getYear()
							&& item.getWorkTime().getMonthValue() == 12 && salaryDate.getMonthValue() == 1)) {
				workHours += item.getAttendanceHours();
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
		return new SalarySystemRes(finalSalarySystem, "新增成功");
	}

	// =====修改薪水資料 (基本上只能修改底薪)，無視年資 (給主管)
	@Override
	public SalarySystemRes updateSalarySystem(SalarySystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		SalarySystemRes res = new SalarySystemRes();
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

		if (!checkSalaryIsNull && req.getSalary() <= 0) {
			return new SalarySystemRes("底薪不可小於 0 ");
		}

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

		// 沒有輸入日期為true
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

		/// 將接近來的日期字串轉成日期 (req)
		LocalDate salaryDate = req.getSalaryDate();

		// 為了以防修改的日期有重複的情況，撈出來防呆
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		// 防呆
		for (var item : salarySystemList) {
			if (item == salarySystem) {
				continue;
			}
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				return new SalarySystemRes("以新增過這位員工該年分、該月份的薪水資料");
			}
		}

		// 要撈出輸入的這個月有沒有上班，用時數判斷
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		// 計算總工作時數
		int workHours = 0;

		for (var item : workSystemList) {
			if ((item.getWorkTime().getYear() == salaryDate.getYear()
					&& item.getWorkTime().getMonthValue() + 1 == salaryDate.getMonthValue())
					|| (item.getWorkTime().getYear() + 1 == salaryDate.getYear()
							&& item.getWorkTime().getMonthValue() == 12 && salaryDate.getMonthValue() == 1)) {
				workHours += item.getAttendanceHours();
			}
		}

		if (workHours <= 0) {
			return new SalarySystemRes("找不到該員工的上班時間");
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

	// =====搜尋資料 (給員工的) ps.員工只能搜尋到自己的資料
	@Override
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req) {
		SalarySystemRes res = new SalarySystemRes();
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		boolean checkSearchStartDate = req.getSearchStartDate() != null;
		boolean checkSearchEndDate = req.getSearchEndDate() != null;
		if (!checkEmployeeCode || (!checkEmployeeCode && checkSearchStartDate && checkSearchEndDate)) {
			res.setMessage("參數不能空");
			return new SalarySystemRes(res.getMessage());
		}

		if (checkSearchEndDate && !checkSearchStartDate) {
			res.setMessage("請輸入開始時間");
			return new SalarySystemRes(res.getMessage());
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
				return new SalarySystemRes("結束時間不可小於開始時間");
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
			return new SalarySystemRes("今天時間不可小於開始時間");
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

//-----------------------------------主管可搜尋到所有人的薪水資料---------------------------------------------------
	@Override
	public SalarySystemRes searchSalarySystemForManager(SalarySystemReq req) {
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		boolean checkSearchStartDate = req.getSearchStartDate() != null;
		boolean checkSearchEndDate = req.getSearchEndDate() != null;
		List<SalarySystem> salaryList = new ArrayList<>();
		if (!StringUtils.hasText(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("請輸入主管編號");
		}
		Optional<EmployeeInfo> managerEmployeeInfoOp = employeeInfoDao.findById(req.getSalaryEmployeeCode());
		if (!managerEmployeeInfoOp.isPresent()) {
			return new SalarySystemRes("找不到該主管");
		}
		EmployeeInfo managerEmployeeInfo = managerEmployeeInfoOp.get();
		// 想搜尋員工編號都、開始日期、結束日期都沒輸入 (需過慮)
		if (!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao.findByOrderBySalaryDateDesc();
			for (var item : salarySystemListInfo) {
				Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(item.getEmployeeCode());
				EmployeeInfo employeeInfo = employeeInfoOp.get();
				if (employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
					salaryList.add(item);
				}
			}
			return new SalarySystemRes(salaryList, "部門 : " + managerEmployeeInfo.getSection());
		}

		if (checkSearchEndDate && !checkSearchStartDate) {
			return new SalarySystemRes("請輸入開始時間");
		}
		// 有輸入員工編號、沒有開始日期
		if (checkEmployeeCode && !checkSearchStartDate) {
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeInfoOp.isPresent()) {
				return new SalarySystemRes("請輸入正確員工編號");
			}
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
				return new SalarySystemRes("請輸入正確員工編號");
			}
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (!employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				return new SalarySystemRes("您與這位員工不同部門");
			}
			LocalDate startDate = req.getSearchStartDate();
			LocalDate endDate = req.getSearchEndDate();
			if (endDate.isBefore(startDate)) {
				return new SalarySystemRes("結束時間不可小於開始時間");
			}
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
				return new SalarySystemRes("請輸入正確員工編號");
			}
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (!employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				return new SalarySystemRes("您與這位員工不同部門");
			}
			LocalDate startDate = req.getSearchStartDate();
			if (LocalDate.now().isBefore(startDate)) {
				return new SalarySystemRes("今天時間不可小於開始時間");
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
			LocalDate startDate = req.getSearchStartDate();
			LocalDate endDate = req.getSearchEndDate();
			if (endDate.isBefore(startDate)) {
				return new SalarySystemRes("結束時間不可小於開始時間");
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findBySalaryDateBetweenOrderBySalaryDateDesc(startDate, endDate);
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("查無資料");
			}
			for (var item : salarySystemListInfo) {
				Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(item.getEmployeeCode());
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
			return new SalarySystemRes("今天時間不可小於開始時間");
		}
		List<SalarySystem> salarySystemListInfo = salarySystemDao
				.findBySalaryDateBetweenOrderBySalaryDateDesc(startDate, LocalDate.now());
		if (salarySystemListInfo.isEmpty()) {
			return new SalarySystemRes("查無資料");
		}
		for (var item : salarySystemListInfo) {
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(item.getEmployeeCode());
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				salaryList.add(item);
			}
		}
		return new SalarySystemRes(salaryList, "部門 : " + managerEmployeeInfo.getSection());
	}

}
