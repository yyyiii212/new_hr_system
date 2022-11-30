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

		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());// ��DB���L���ƪ����
		if (employeeInfoOp.isPresent()) { // ��Ʀ����ƴN�^��null
			return null;
		}
		
		EmployeeInfo employeeInfo = new EmployeeInfo(req.getEmployeeCode(), req.getName(),
				req.getId(), req.getEmployeeEmail(), req.getSection(), req.getLevel(), req.getSeniority(),
				req.getSituation());// �s�W���
		employeeInfo.setJoinTime(new Date());
		
		employeeInfoDao.save(employeeInfo);// �x�s

		return employeeInfo;
	}

	@Override
	public EmployeeInfo readEmployeeInfo(EmployeeInfoReq req) {

		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());// ��DB���L���ƪ����
		if (!employeeInfoOp.isPresent()) { // ��ƨS�����ƴN�^��null
			return null;
		}

		EmployeeInfo employeeInfo = employeeInfoOp.get();// ���o�d�ߪ����

		return employeeInfo;
	}

	@Override
	public EmployeeInfo updateEmployeeInfo(EmployeeInfoReq req) {

		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());// ��DB���L���ƪ����
		if (!employeeInfoOp.isPresent()) { // ��ƨS�����ƴN�^��null
			return null;
		}

		EmployeeInfo employeeInfo = employeeInfoOp.get();// ���o�Q�ק諸���

		employeeInfo.setName(req.getName());// �ק���
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

		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());// ��DB���L���ƪ����
		if (!employeeInfoOp.isPresent()) { // ��ƨS�����ƴN�^��null
			return null;
		}

		EmployeeInfo employeeInfo = employeeInfoOp.get();// ���o�n�R�������
		employeeInfoDao.delete(employeeInfo);

		EmployeeInfoRes employeeInfoRes = new EmployeeInfoRes();
		employeeInfoRes.setMessage("Successful !!"); // �^�Ǧ��\���T��

		return employeeInfo;
	}

}
