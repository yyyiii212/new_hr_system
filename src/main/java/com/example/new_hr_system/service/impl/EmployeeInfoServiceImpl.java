package com.example.new_hr_system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.entity.SalarySystem;
import com.example.new_hr_system.respository.AbsenceSystemDao;
import com.example.new_hr_system.respository.EmployeeInfoDao;
import com.example.new_hr_system.respository.SalarySystemDao;
import com.example.new_hr_system.respository.WorkSystemDao;
import com.example.new_hr_system.service.ifs.EmployeeInfoService;
import com.example.new_hr_system.vo.EmployeeInfoReq;
import com.example.new_hr_system.vo.EmployeeInfoRes;

@Service
public class EmployeeInfoServiceImpl implements EmployeeInfoService {
	@Autowired
	private AbsenceSystemDao absenceSystemDao;

	@Autowired
	private EmployeeInfoDao employeeInfoDao;

	@Autowired
	private SalarySystemDao salarySystemDao;

	@Autowired
	private WorkSystemDao workSystemDao;
	
	@Override
	public EmployeeInfo loginJudgment(EmployeeInfoReq req) {
		
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		
		if(employeeInfoOp.isPresent()) {
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if(req.getId() == employeeInfo.getId()) {
				return employeeInfo;
			}
		}
		return null;
	}

	@Override
	public EmployeeInfo createEmployeeInfo(EmployeeInfoReq req) {

		// 找DB有無重複的資料
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		// 資料有重複就回傳null
		if (employeeInfoOp.isPresent()) {
			return null;
		}
		// 新增資料
		EmployeeInfo employeeInfo = new EmployeeInfo(req.getName(), req.getId(), req.getEmployeeEmail(),
				req.getSection(), req.getLevel(), req.getSeniority(), req.getSituation());
		employeeInfo.setJoinTime(new Date());
		// 判斷部門
		if (req.getSection().equals("人資")) {
			employeeInfo.setEmployeeCode("A" + req.getEmployeeCode());
		}
		if (req.getSection().equals("會計")) {
			employeeInfo.setEmployeeCode("B" + req.getEmployeeCode());
		}
		if (req.getSection().equals("敲詐")) {
			employeeInfo.setEmployeeCode("C" + req.getEmployeeCode());
		}

		return employeeInfoDao.save(employeeInfo);
	}

	@Override
	public List<EmployeeInfo> readEmployeeInfo(EmployeeInfoReq req) {

		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return employeeInfoDao.findAll();
		}

		// 找DB有無重複的資料
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		// 資料沒有重複就回傳null
		if (!employeeInfoOp.isPresent()) {
			return null;
		}
		// 取得查詢的資料
		EmployeeInfo employeeInfo = employeeInfoOp.get();
		List<EmployeeInfo> employeeInfoList = new ArrayList<>();
		employeeInfoList.add(employeeInfo);

		return employeeInfoList;
	}

	@Override
	public EmployeeInfo updateEmployeeInfo(EmployeeInfoReq req) {

		// 找DB有無重複的資料
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		// 資料沒有重複就回傳null
		if (!employeeInfoOp.isPresent()) {
			return null;
		}
		// 取得想修改的資料
		EmployeeInfo employeeInfo = employeeInfoOp.get();
		// 修改資料
		employeeInfo.setName(req.getName());
		employeeInfo.setId(req.getId());
		employeeInfo.setEmployeeEmail(req.getEmployeeEmail());
		employeeInfo.setSection(req.getSection());
		employeeInfo.setLevel(req.getLevel());
		employeeInfo.setSeniority(req.getSeniority());
		employeeInfo.setSituation(req.getSituation());
		employeeInfoDao.save(employeeInfo);

		// 修改薪資DB內的名字
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());
		for (SalarySystem item : salarySystemList) {
			item.setName(req.getName());
			salarySystemDao.save(item);
		}

		return employeeInfo;
	}

	@Override
	public EmployeeInfo deleteEmployeeInfo(EmployeeInfoReq req) {

		// 找DB有無重複的資料
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		// 資料沒有重複就回傳null
		if (!employeeInfoOp.isPresent()) {
			return null;
		}
		// 取得要刪除的資料
		EmployeeInfo employeeInfo = employeeInfoOp.get();
		employeeInfoDao.delete(employeeInfo);

		EmployeeInfoRes employeeInfoRes = new EmployeeInfoRes();
		// 回傳成功的訊息
		employeeInfoRes.setMessage("Successful !!");

		return employeeInfo;
	}
}
