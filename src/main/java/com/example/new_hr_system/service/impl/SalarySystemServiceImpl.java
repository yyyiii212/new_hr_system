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


	@Override
	public SalarySystemRes creatSalarySystem(SalarySystemReq req) {
		SalarySystemRes res = check(req);
		if (res != null) {
			return res;
		}

		res = new SalarySystemRes();

		
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeInfoOp.isPresent()) {
			res.setMessage("找不到該員工");
			return new SalarySystemRes(res.getMessage());
		}

	
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());

		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy年M月d日");

		
		LocalDate salaryDate = LocalDate.parse(req.getSalaryDate(), format);

		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				res.setMessage("以新增過這位員工該年、該月的薪水資料");
				return new SalarySystemRes(res.getMessage());
			}
		}

		
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		if (workSystemList.isEmpty()) {
			res.setMessage("找不到該員工的打卡資料");
			return new SalarySystemRes(res.getMessage());
		}
	
		SalarySystem salarySystem = new SalarySystem();

	
		int salary = salarySystem.getSalary();

	
		EmployeeInfo employeeInfo = employeeInfoOp.get();

		
		int seniority = employeeInfo.getSeniority();

		
		for (int i = 1; i <= seniority; i++) {
			salary += 1000;
		}


	int workHours = 0;

	
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
		
		int raisePay = (workHours - 174) * 100;

	
		if (raisePay <= 0) {
			raisePay = raisePay * 0;
		}

		
		int managerRaisePay = employeeInfo.getLevel() * 5000;

	
		int totalSalary = salary + raisePay + managerRaisePay + (salaryDeduct);

		SalarySystem finalSalarySystem = new SalarySystem(UUID.randomUUID(), req.getEmployeeCode(),
				employeeInfo.getName(), salaryDate, salary, raisePay, managerRaisePay, salaryDeduct, totalSalary);
		salarySystemDao.save(finalSalarySystem);
		res.setMessage("新增成功");
		return new SalarySystemRes(finalSalarySystem, res.getMessage());
	}

	// =====
	@Override
	public SalarySystemRes updateSalarySystem(SalarySystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		SalarySystemRes res = new SalarySystemRes();

	
		boolean checkDateIsNull = !StringUtils.hasText(req.getSalaryDate());

	
		boolean checkSalaryIsNull = req.getSalary() == null;

		if (!checkSalaryIsNull && req.getSalary() <= 0) {
			res.setMessage("底薪不可小於 0 ");
			return new SalarySystemRes(res.getMessage());
		}
		
		if (!StringUtils.hasText(req.getUuid()) || (checkDateIsNull && checkSalaryIsNull)) {
			res.setMessage("參數不能空");
			return new SalarySystemRes(res.getMessage());
		}

		Optional<SalarySystem> salarySystemOp = salarySystemDao.findById(uuid);
		if (!salarySystemOp.isPresent()) {
			res.setMessage("錯誤資訊 (這個防呆沒什麼意義)");
			return new SalarySystemRes(res.getMessage());
		}

		
		SalarySystem salarySystem = salarySystemOp.get();

		
		int total = 0;

	
		if (checkDateIsNull) {
			total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
					+ (salarySystem.getSalaryDeduct());
			salarySystem.setSalary(req.getSalary());
			salarySystem.setTotalSalary(total);
			salarySystemDao.save(salarySystem);
			res.setMessage("修改底薪成功");
			return new SalarySystemRes(salarySystem, res.getMessage());
		}

		
		String checkDateString = "^[1-9]\\d{3}年(0[1-9]|1[0-2]|[1-9])月([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])日";
		boolean checkDate = req.getSalaryDate().matches(checkDateString);
		if (!checkDate) {
			res.setMessage("格式為yyyy年mm月dd日");
			return new SalarySystemRes(res.getMessage());
		}

		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy年M月d日");

	
		LocalDate salaryDate = LocalDate.parse(req.getSalaryDate(), format);

		
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				res.setMessage("以新增過這位員工該年分、該月份的薪水資料");
				salarySystem = null;
				return new SalarySystemRes(salarySystem, res.getMessage());
			}
		}

		
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

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

		
		if (checkSalaryIsNull) {
			salarySystem.setSalaryDate(salaryDate);
			salarySystemDao.save(salarySystem);
			res.setMessage("修改日期成功");
			return new SalarySystemRes(salarySystem, res.getMessage());
		}

		
		total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
				+ (salarySystem.getSalaryDeduct());
		salarySystem.setSalaryDate(salaryDate);
		salarySystem.setSalary(req.getSalary());
		salarySystemDao.save(salarySystem);
		res.setMessage("修改日期、薪資成功");
		return new SalarySystemRes(salarySystem, res.getMessage());
	}

	// =====
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
