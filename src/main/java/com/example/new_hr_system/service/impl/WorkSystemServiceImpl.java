package com.example.new_hr_system.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.new_hr_system.constants.HrSystemRtnCode;
import com.example.new_hr_system.entity.WorkSystem;
import com.example.new_hr_system.respository.AbsenceSystemDao;
import com.example.new_hr_system.respository.EmployeeInfoDao;
import com.example.new_hr_system.respository.SalarySystemDao;
import com.example.new_hr_system.respository.WorkSystemDao;
import com.example.new_hr_system.service.ifs.HrSystemService;
import com.example.new_hr_system.service.ifs.WorkSystemService;
import com.example.new_hr_system.vo.EmployeeInfoRes;
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

	// ---------------------------------------1.痁ゴ
	@Override
	public WorkSystemRes punchToWork(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		// 耞絪腹块タ絋籔
//		String parttern1 = "[A-Z]\\d{3}";
//		if (!req.getEmployeeCode().matches(parttern1)) {
//			res.setMessage("ぃ琌赣そ");
//			WorkSystem workSystem = null;
//			return new WorkSystemRes(workSystem, res.getMessage());
//		}
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			res.setMessage("把计ぃ");
			WorkSystem workSystem = null;
			return new WorkSystemRes(workSystem, res.getMessage());
		}
		res.setMessage("痁ゴΘ");
		WorkSystem workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), LocalDateTime.now(), null,
				null, 0);
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, res.getMessage());
	}

	// ---------------------------------------2.痁ゴ
	@Override
	public WorkSystemRes punchToOffWork(WorkSystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		WorkSystemRes res = new WorkSystemRes();
		Optional<WorkSystem> workSystemOp = workSystemDao.findById(uuid);
		WorkSystem workSystem = workSystemOp.get();
		if (!workSystemOp.isPresent()) {
			res.setMessage("把计ぃ");
			workSystem = null;
			return new WorkSystemRes(workSystem, res.getMessage());
		}
		if (!(workSystem.getWorkTime().getYear() == LocalDateTime.now().getYear())) {
			if (!workSystem.getWorkTime().getMonth().equals(LocalDateTime.now().getMonth())) {
				if (!(workSystem.getWorkTime().getDayOfMonth() == (LocalDateTime.now().getDayOfMonth()))) {
					res.setMessage("ぃ干ゴ");
					workSystem = null;
					return new WorkSystemRes(workSystem, res.getMessage());
				}
			}
		}

		int hours = LocalDateTime.now().getHour() - 9;
		int lateTime = LocalDateTime.now().getHour() - workSystem.getWorkTime().getHour();
		String attendanceStatusStr;
		if ((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() > 1)) {
			if (hours < 8) {
				attendanceStatusStr = "筐+Ν癶";
				workSystem = new WorkSystem(workSystem.getUuid(), workSystem.getEmployeeCode(),
						workSystem.getWorkTime(), LocalDateTime.now(), attendanceStatusStr, lateTime);
			}

		} else if (workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() > 1) {
			attendanceStatusStr = "筐";
			workSystem = new WorkSystem(workSystem.getUuid(), workSystem.getEmployeeCode(), workSystem.getWorkTime(),
					LocalDateTime.now(), attendanceStatusStr, lateTime);
		} else if (hours < 8) {
			attendanceStatusStr = "Ν癶";
			workSystem = new WorkSystem(workSystem.getUuid(), workSystem.getEmployeeCode(), workSystem.getWorkTime(),
					LocalDateTime.now(), attendanceStatusStr, lateTime);
		} else {
			attendanceStatusStr = "タ盽";
			workSystem = new WorkSystem(workSystem.getUuid(), workSystem.getEmployeeCode(), workSystem.getWorkTime(),
					LocalDateTime.now(), attendanceStatusStr, hours);
		}
		workSystemDao.save(workSystem);
		res.setMessage("痁ゴΘ");
		return new WorkSystemRes(workSystem, res.getMessage());

	}
	// ---------------------------------------3.琩高对猵

	@Override
	public WorkSystemRes searchWorkInfo(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			res.setMessage("把计ぃ眔");
			WorkSystem workSystem = null;
			return new WorkSystemRes(workSystem, res.getMessage());
		}

		if (req.getSearchMonth() != null && req.getSearchYear() == null) {
			res.setMessage("ぃ眔");
			WorkSystem workSystem = null;
			return new WorkSystemRes(workSystem, res.getMessage());
		}

		List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeOrderByWorkTimeDesc(req.getEmployeeCode());
		if (workInfoList.isEmpty()) {
			res.setMessage("琩礚戈workInfoList");
			WorkSystem workSystem = null;
			return new WorkSystemRes(workSystem, res.getMessage());
		}

		if (req.getSearchMonth() == null && req.getSearchYear() == null) {
			res.setWorkInfoList(workInfoList);
			return res;
		}
		List<WorkSystem> workInfoListByYearOrMonth = new ArrayList<>();

		for (var item : workInfoList) {
			if (req.getSearchMonth() == null && item.getWorkTime().getYear() == req.getSearchYear()) {
				workInfoListByYearOrMonth.add(item);// 眔┮Τ戈
			} else if (item.getWorkTime().getMonthValue() == (req.getSearchMonth())
					&& item.getWorkTime().getYear() == req.getSearchYear()) {
				workInfoListByYearOrMonth.add(item);// 眔だxる戈
			}
		}
		if (workInfoListByYearOrMonth.isEmpty()) {
			res.setMessage("琩礚戈workInfoListByYearOrMonth");
			WorkSystem workSystem = null;
			return new WorkSystemRes(workSystem, res.getMessage());
		}
		res.setWorkInfoList(workInfoListByYearOrMonth);
		return res;
	}
}
