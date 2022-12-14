package com.example.new_hr_system.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.respository.AbsenceSystemDao;
import com.example.new_hr_system.respository.EmployeeInfoDao;
import com.example.new_hr_system.respository.SalarySystemDao;
import com.example.new_hr_system.respository.WorkSystemDao;
import com.example.new_hr_system.service.ifs.HrSystemService;
import com.example.new_hr_system.vo.EmployeeInfoReq;
import com.example.new_hr_system.vo.EmployeeInfoRes;

@Service
public class HrSystemServiceImpl implements HrSystemService {

	@Autowired
	private AbsenceSystemDao absenceSystemDao;

	@Autowired
	private EmployeeInfoDao employeeInfoDao;

	@Autowired
	private SalarySystemDao salarySystemDao;

	@Autowired
	private WorkSystemDao workSystemDao;

	@Override
	public EmployeeInfo createEmployeeInfo(EmployeeInfoReq req) {

		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());// 找DB有無重複的資料
		if (employeeInfoOp.isPresent()) { // 資料有重複就回傳null
			return null;
		}

		EmployeeInfo employeeInfo = new EmployeeInfo(req.getName(), req.getId(),
				req.getEmployeeEmail(), req.getSection(), req.getSituation());// 新增資料
		employeeInfo.setJoinTime(new Date());

		employeeInfoDao.save(employeeInfo);// 儲存

		return employeeInfo;
	}

	@Override
	public EmployeeInfo readEmployeeInfo(EmployeeInfoReq req) {

		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());// 找DB有無重複的資料
		if (!employeeInfoOp.isPresent()) { // 資料沒有重複就回傳null
			return null;
		}

		EmployeeInfo employeeInfo = employeeInfoOp.get();// 取得查詢的資料

		return employeeInfo;
	}

	@Override
	public EmployeeInfo updateEmployeeInfo(EmployeeInfoReq req) {

		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());// 找DB有無重複的資料
		if (!employeeInfoOp.isPresent()) { // 資料沒有重複就回傳null
			return null;
		}

		EmployeeInfo employeeInfo = employeeInfoOp.get();// 取得想修改的資料

		employeeInfo.setName(req.getName());// 修改資料
		employeeInfo.setId(req.getId());
		employeeInfo.setEmployeeEmail(req.getEmployeeEmail());
		employeeInfo.setSection(req.getSection());
		employeeInfo.setLevel(req.getLevel());
		employeeInfo.setSeniority(req.getSeniority());
		employeeInfo.setSituation(req.getSituation());
		employeeInfoDao.save(employeeInfo);

		return employeeInfo;
	}

	@Override
	public EmployeeInfo deleteEmployeeInfo(EmployeeInfoReq req) {

		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());// 找DB有無重複的資料
		if (!employeeInfoOp.isPresent()) { // 資料沒有重複就回傳null
			return null;
		}

		EmployeeInfo employeeInfo = employeeInfoOp.get();// 取得要刪除的資料
		employeeInfoDao.delete(employeeInfo);

		EmployeeInfoRes employeeInfoRes = new EmployeeInfoRes();
		employeeInfoRes.setMessage("Successful !!"); // 回傳成功的訊息

		return employeeInfo;
	}

}
