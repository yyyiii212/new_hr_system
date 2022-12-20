package com.example.new_hr_system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.new_hr_system.constants.EmployeeInfoRtnCode;
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

		if (employeeInfoOp.isPresent()) {
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (employeeInfo.getId().equals(req.getId())) {
				return employeeInfo;
			}
		}
		return null;
	}

	@Override
	public EmployeeInfo createEmployeeInfo(EmployeeInfoReq req) {
		// �P�_����
		if (req.getSection().equals("�H��")) {
			EmployeeInfo employeeInfo = new EmployeeInfo();
			employeeInfo.setEmployeeCode("A" + req.getEmployeeCode());
			// ��DB���L���ƪ����
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(employeeInfo.getEmployeeCode());
			// ��Ʀ����ƴN�^��null
			if (employeeInfoOp.isPresent()) {
				return null;
			}
		}
		if (req.getSection().equals("�|�p")) {
			EmployeeInfo employeeInfo = new EmployeeInfo();
			employeeInfo.setEmployeeCode("B" + req.getEmployeeCode());
			// ��DB���L���ƪ����
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(employeeInfo.getEmployeeCode());
			// ��Ʀ����ƴN�^��null
			if (employeeInfoOp.isPresent()) {
				return null;
			}
		}
		if (req.getSection().equals("�V�B")) {
			EmployeeInfo employeeInfo = new EmployeeInfo();
			employeeInfo.setEmployeeCode("C" + req.getEmployeeCode());
			// ��DB���L���ƪ����
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(employeeInfo.getEmployeeCode());
			// ��Ʀ����ƴN�^��null
			if (employeeInfoOp.isPresent()) {
				return null;
			}
		}

		// �s�W���
		EmployeeInfo employeeInfo = new EmployeeInfo(req.getName(), req.getId(), req.getEmployeeEmail(),
				req.getSection(), req.getSituation());
		employeeInfo.setJoinTime(new Date());

		List<EmployeeInfo> employeeIdList = employeeInfoDao.findAllById(req.getId());

		if (!employeeIdList.isEmpty()) {
			return null;
		}

		if (req.getTitle().equals("���u")) {
			employeeInfo.setLevel(0);
		}
		if (req.getTitle().equals("�D��")) {
			employeeInfo.setLevel(1);
		}
		if (req.getTitle().equals("�g�z")) {
			employeeInfo.setLevel(2);
		}
		employeeInfo.setSeniority(0);

		return employeeInfoDao.save(employeeInfo);
	}

	@Override
	public List<EmployeeInfo> readEmployeeInfo(EmployeeInfoReq req) {

		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return employeeInfoDao.findAll();
		}

		// ��DB���L���ƪ����
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		// ��ƨS�����ƴN�^��null
		if (!employeeInfoOp.isPresent()) {
			return null;
		}
		// ���o�d�ߪ����
		EmployeeInfo employeeInfo = employeeInfoOp.get();
		List<EmployeeInfo> employeeInfoList = new ArrayList<>();
		employeeInfoList.add(employeeInfo);

		return employeeInfoList;
	}

	@Override
	public EmployeeInfo readOneEmployeeInfo(EmployeeInfoReq req) {

		// ��DB���L���ƪ����
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());

		// ���o�d�ߪ����
		EmployeeInfo employeeInfo = employeeInfoOp.get();

		return employeeInfo;
	}

	@Override
	public EmployeeInfo updateEmployeeInfo(EmployeeInfoReq req) {

		// ��DB���L���ƪ����
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		// ��ƨS�����ƴN�^��null
		if (!employeeInfoOp.isPresent()) {
			return null;
		}
		// ���o�Q�ק諸���
		EmployeeInfo employeeInfo = employeeInfoOp.get();
		// �ק���
		if (req.getTitle().equals("���u")) {
			employeeInfo.setLevel(0);
		}
		if (req.getTitle().equals("�D��")) {
			employeeInfo.setLevel(1);
		}
		if (req.getTitle().equals("�g�z")) {
			employeeInfo.setLevel(2);
		}
		employeeInfo.setName(req.getName());
		employeeInfo.setId(req.getId());
		employeeInfo.setEmployeeEmail(req.getEmployeeEmail());
		employeeInfo.setSection(req.getSection());
//		employeeInfo.setSeniority(req.getSeniority());
		employeeInfo.setSituation(req.getSituation());
		employeeInfoDao.save(employeeInfo);

		// �ק��~��DB�����W�r
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());
		for (SalarySystem item : salarySystemList) {
			item.setName(req.getName());
			salarySystemDao.save(item);
		}

		return employeeInfo;
	}

	@Override
	public EmployeeInfo deleteEmployeeInfo(EmployeeInfoReq req) {

		// ��DB���L���ƪ����
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		// ��ƨS�����ƴN�^��null
		if (!employeeInfoOp.isPresent()) {
			return null;
		}
		// ���o�n�R�������
		EmployeeInfo employeeInfo = employeeInfoOp.get();
		employeeInfoDao.delete(employeeInfo);

		EmployeeInfoRes employeeInfoRes = new EmployeeInfoRes();
		// �^�Ǧ��\���T��
		employeeInfoRes.setMessage("Successful !!");

		return employeeInfo;
	}
}
