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

	// =====新增薪水資料
	@Override
	public SalarySystemRes creatSalarySystem(SalarySystemReq req) {
		SalarySystemRes res = check(req);
		if (res != null) {
			return res;
		}

		res = new SalarySystemRes();

		// 撈到EmployeeInfo的資料, 防呆、存姓名、計算主管加給、底薪 會用到
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeInfoOp.isPresent()) {
			res.setMessage("找不到該員工");
			return new SalarySystemRes(res.getMessage());
		}

		// 因為不能同個員工在同年同月有兩筆薪水資料,所以要先撈起來防呆
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());

		// 轉型前,確認格式
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy年M月d日");

		// 字串轉日期 ps.幾號發薪就<x>d
		LocalDate salaryDate = LocalDate.parse(req.getSalaryDate(), format);

		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				res.setMessage("以新增過這位員工該年份、該月份的薪水資料");
				return new SalarySystemRes(res.getMessage());
			}
		}

		// 撈WorkSystem的資料,存 一般加給、逞罰 會用到
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		if (workSystemList.isEmpty()) {
			res.setMessage("找不到該員工的打卡資料");
			return new SalarySystemRes(res.getMessage());
		}
		// 先new出SalarySystem才能拿到本身的預設值<底薪>
		SalarySystem salarySystem = new SalarySystem();

		// 拿到底薪20000
		int salary = salarySystem.getSalary();

		// 取EmployeeInfo的資料, 防呆、存姓名、計算主管加給、底薪 會用到
		EmployeeInfo employeeInfo = employeeInfoOp.get();

		// 拿到年資
		int seniority = employeeInfo.getSeniority();

		// 每滿一年( 底薪 )+1000(有可能會改) ps.因為最小年資是 0 , 所以要用 int = 1 去跑
		for (int i = 1; i <= seniority; i++) {
			salary += 1000;
		}

		// 計算工作時數
		int workHours = 0;

		// 逞罰扣錢
		int salaryDeduct = 0;

		// 這裡要掃他過去這個月的工作時數、及罰款項目 所以應該要-1 <測試階段懶得改>
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
		// 一般加給 174 = 法定加班時數
		int raisePay = (workHours - 174) * 100;

		// 變負數ㄉ話要歸零
		if (raisePay <= 0) {
			raisePay = raisePay * 0;
		}

		// 主管加給 (最小是 0)
		int managerRaisePay = employeeInfo.getLevel() * 5000;

		// 薪資總額
		int totalSalary = salary + raisePay + managerRaisePay + (salaryDeduct);

		SalarySystem finalSalarySystem = new SalarySystem(UUID.randomUUID(), req.getEmployeeCode(),
				employeeInfo.getName(), salaryDate, salary, raisePay, managerRaisePay, salaryDeduct, totalSalary);
		salarySystemDao.save(finalSalarySystem);
		res.setMessage("新增成功");
		return new SalarySystemRes(finalSalarySystem, res.getMessage());
	}

	// =====更新薪水資料(基本上只能更改,底薪、薪水時間) ps.底薪無視年資 看人資想給多少
	@Override
	public SalarySystemRes updateSalarySystem(SalarySystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		SalarySystemRes res = new SalarySystemRes();

		// true代表要修改底薪 (沒有輸入date為true)
		boolean checkDateIsNull = !StringUtils.hasText(req.getSalaryDate());

		// true代表要修改日期 (沒有輸入薪資為true)
		boolean checkSalaryIsNull = req.getSalary() == null;

		if (!checkSalaryIsNull && req.getSalary() <= 0) {
			res.setMessage("底薪不可小於零");
			return new SalarySystemRes(res.getMessage());
		}
		// uuid、date、薪資都沒輸入要防呆
		if (!StringUtils.hasText(req.getUuid()) || (checkDateIsNull && checkSalaryIsNull)) {
			res.setMessage("參數不能空");
			return new SalarySystemRes(res.getMessage());
		}

		Optional<SalarySystem> salarySystemOp = salarySystemDao.findById(uuid);
		if (!salarySystemOp.isPresent()) {
			res.setMessage("錯誤資訊(其實這個防呆沒什麼意義)");
			return new SalarySystemRes(res.getMessage());
		}

		// 防呆通過進行取値
		SalarySystem salarySystem = salarySystemOp.get();

		// 準備要接所有薪資
		int total = 0;

		// 這裡進去代表用戶只要修改底薪
		if (checkDateIsNull) {
			total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
					+ (salarySystem.getSalaryDeduct());
			salarySystem.setSalary(req.getSalary());
			salarySystem.setTotalSalary(total);
			salarySystemDao.save(salarySystem);
			res.setMessage("修改底薪成功");
			return new SalarySystemRes(salarySystem, res.getMessage());
		}

		// 上面沒擋掉,代表日期會進行修改,那就必須把字串轉日期了
		String checkDateString = "^[1-9]\\d{3}年(0[1-9]|1[0-2]|[1-9])月([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])日";
		boolean checkDate = req.getSalaryDate().matches(checkDateString);
		if (!checkDate) {
			res.setMessage("格式為yyyy年mm月dd日");
			return new SalarySystemRes(res.getMessage());
		}

		// 轉型前,確認格式
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy年M月d日");

		// 字串轉日期 (輸入的日期)
		LocalDate salaryDate = LocalDate.parse(req.getSalaryDate(), format);

		// 因為不能同個員工在同年同月有兩筆薪水資料,所以要先撈起來防呆
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				res.setMessage("以新增過這位員工該年份、該月份的薪水資料");
				salarySystem = null;
				return new SalarySystemRes(salarySystem, res.getMessage());
			}
		}

		// 撈WorkSystem的資料,因為有可能他修改的月份是來自沒有工作的月份所以要撈
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		int workHours = 0;

		// 這裡要掃他過去這個月的工作時數、及罰款項目 所以應該要-1 <測試階段懶得改>
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

		// 這裡進去代表用戶只要修改日期
		if (checkSalaryIsNull) {
			salarySystem.setSalaryDate(salaryDate);
			salarySystemDao.save(salarySystem);
			res.setMessage("修改日期成功");
			return new SalarySystemRes(salarySystem, res.getMessage());
		}

		// 上面都沒擋掉，代表日期、底薪 皆要修改
		total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
				+ (salarySystem.getSalaryDeduct());
		salarySystem.setSalaryDate(salaryDate);
		salarySystem.setSalary(req.getSalary());
		salarySystemDao.save(salarySystem);
		res.setMessage("修改日期、薪資成功");
		return new SalarySystemRes(salarySystem, res.getMessage());
	}

	// =====搜尋薪水資訊(給員工的)
	@Override
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req) {
		SalarySystemRes res = new SalarySystemRes();
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());// 有員工編號true
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());// 有開始時間true
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());// 有結束時間true
		// 日期(字串)的正規表達
		String checkDateString = "^[1-9]\\d{3}年(0[1-9]|1[0-2]|[1-9])月([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])日";
		// 日期的正規表達
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy年M月d日");
		// 員工只能搜尋自己的資料，故要判斷
		if (!checkEmployeeCode || (!checkEmployeeCode && checkSearchStartDate && checkSearchEndDate)) {
			res.setMessage("參數值不能空");
			return new SalarySystemRes(res.getMessage());
		}

		// 只有結束時間，沒有開始時間就要防呆
		if (checkSearchEndDate && !checkSearchStartDate) {
			res.setMessage("請輸入開始時間");
			return new SalarySystemRes(res.getMessage());
		}

		// 有開始、結束時間ㄉ防呆
		if (checkSearchStartDate && checkSearchEndDate) {
			// 判斷是否符合正規表達
			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			boolean checkSearchMonthEndString = req.getSearchEndDate().matches(checkDateString);
			if (!checkSearchYearStaetString && !checkSearchMonthEndString) {
				res.setMessage("時間格式錯誤 EX: yyyy年mm月dd日");
				return new SalarySystemRes(res.getMessage());
			}
		}

		// 只有開始時間的防呆
		if (checkSearchStartDate) {
			// 判斷是否符合正規表達
			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			if (!checkSearchYearStaetString) {
				res.setMessage("時間格式錯誤 EX: yyyy年mm月dd日");
				return new SalarySystemRes(res.getMessage());
			}
		}

		// 上面都沒擋代表資料輸入沒問題 ，可以進入資料庫
		// 沒有輸入開始日期(說明一定有員工編號，沒有結束日期)
		if (!checkSearchStartDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeOrderBySalaryDateDesc(req.getEmployeeCode());
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("查無資料");
				return new SalarySystemRes(res.getMessage());
			}
			// 所以直接回傳所有資料
			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}
		// 有輸入開始、結束日期
		if (checkSearchStartDate && checkSearchEndDate) {
			// 將字串轉date
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				res.setMessage("結束時間不可小於開始時間");
				return new SalarySystemRes(res.getMessage());
			}
			// 因為是員工的搜尋，所以一定要用員工編號 +(日期區間)
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							endDate);
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("查無資料");
				return new SalarySystemRes(res.getMessage());
			}
			// 回傳所有資料
			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		// 上面沒擋掉代表沒有輸入結束日期，有開始日期、員工編號
		// 轉型開始日期即可
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		if (LocalDate.now().isBefore(startDate)) {
			res.setMessage("今天時間不可小於開始時間");
			return new SalarySystemRes(res.getMessage());
		}
		// 給到開始>今天的資料 (包含開始時間)
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

	// =====搜尋薪水資訊(給主管的)
	@Override
	public SalarySystemRes searchSalarySystemForManager(SalarySystemReq req) {
		SalarySystemRes res = new SalarySystemRes();
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());// 有員工編號true
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());// 有開始時間true
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());// 有結束時間true
		// 開始、結束時間的正規表達
		String checkDateString = "^[1-9]\\d{3}年(0[1-9]|1[0-2]|[1-9])月([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])日";
		// 轉成localdate的的格式
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy年M月d日");
		// 三者皆沒輸入的就要防呆
		if (!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate) {
			res.setMessage("參數值不能空");
			return new SalarySystemRes(res.getMessage());
		}
		// 只有結束時間，沒有開始時間就要防呆
		if (checkSearchEndDate && !checkSearchStartDate) {
			res.setMessage("請輸入開始時間");
			return new SalarySystemRes(res.getMessage());
		}
		// 有開始、結束時間的防呆(判斷是否符合正規表達)
		if (checkSearchStartDate && checkSearchEndDate) {
			// 判斷是否符合正規表達
			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			boolean checkSearchMonthEndString = req.getSearchEndDate().matches(checkDateString);
			if (!checkSearchYearStaetString && !checkSearchMonthEndString) {
				res.setMessage("時間格式錯誤 EX: yyyy年mm月dd日");
				return new SalarySystemRes(res.getMessage());
			}
		}
		// 只有開始時間的防呆(判斷是否符合正規表達)
		if (checkSearchStartDate) {
			// 判斷是否符合正規表達
			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			if (!checkSearchYearStaetString) {
				res.setMessage("時間格式錯誤 EX: yyyy年mm月dd日");
				return new SalarySystemRes(res.getMessage());
			}
		}

		// 判斷有輸入員工編號、沒有輸入開始日期 (說明直接回傳全部資料)
		if (checkEmployeeCode && !checkSearchStartDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("查無資料");
				return new SalarySystemRes(res.getMessage());
			}
			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		// 判斷有員工編號、開始、結束日期
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

		// 有員工編號、開始時間，但沒有結束時間
		if (checkEmployeeCode && checkSearchStartDate) {
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			if (LocalDate.now().isBefore(startDate)) {
				res.setMessage("今天不可小於開始時間");
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

		// 上面都沒擋掉，說明一定沒有員工編號
		if (checkSearchStartDate && checkSearchEndDate) {
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				res.setMessage("結束時間不小於開始時間");
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

		// 上面都沒有擋掉代表沒有輸入員工編號、結束日期
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
