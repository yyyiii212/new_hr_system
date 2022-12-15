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
			res.setMessage("�ѼƤ����");
			SalarySystem salarySystem = null;
			return new SalarySystemRes(salarySystem, res.getMessage());
		}
		String checkYearAndMonth = "^[1-9]\\d{3}�~(0[1-9]|1[0-2]|[1-9])��([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])��";
		boolean checkDate = req.getSalaryDate().matches(checkYearAndMonth);
		if (!checkDate) {
			res.setMessage("�榡��yyyy�~mm��dd��");
			return new SalarySystemRes(res.getMessage());
		}
		return null;

	}

//===============================================================================

	// =====�s�W�~�����
	@Override
	public SalarySystemRes creatSalarySystem(SalarySystemReq req) {
		SalarySystemRes res = check(req);
		// ���b (������W��F)�B���u�s���ŭ�
		if (res != null) {
			return res;
		}
		res = new SalarySystemRes();

		// �n�ǥѳo�i����ƨ��o�ݭn����T �p�� 1.�~�� 2.�m�W 3.�D�޵���
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeInfoOp.isPresent()) {
			res.setMessage("�䤣��ӭ��u");
			return new SalarySystemRes(res.getMessage());
		}

		// �קK�s�W��P�@����u �b�P�~�P�릳�ⵧ�ۦP���
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());
		// ���������W��F
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy�~M��d��");

		// �N����Ӫ����<�r��>�ର���
		LocalDate salaryDate = LocalDate.parse(req.getSalaryDate(), format);

		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				res.setMessage("�H�s�W�L�o����u�Ӧ~�B�Ӥ몺�~�����");
				return new SalarySystemRes(res.getMessage());
			}
		}

		// �ݭn�p��u�@�ɼ�
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		if (workSystemList.isEmpty()) {
			res.setMessage("�䤣��ӭ��u�����d���");
			return new SalarySystemRes(res.getMessage());
		}

		// ��new�X��
		SalarySystem salarySystem = new SalarySystem();

		// �]���o�̭n���o�w�]�����~
		int salary = salarySystem.getSalary();

		// �NEmployeeInfo�o�i����T���X��
		EmployeeInfo employeeInfo = employeeInfoOp.get();

		// ���X�~��
		int seniority = employeeInfo.getSeniority();

		// ���@�~�[�@�d
		for (int i = 1; i <= seniority; i++) {
			salary += 1000;
		}

		// ���X�`�@�u�@�ɼ�
		int workHours = 0;

		// �x�@���~
		int salaryDeduct = 0;

		for (var item : workSystemList) {
			if (item.getWorkTime().getYear() == salaryDate.getYear()
					&& item.getWorkTime().getMonthValue() == salaryDate.getMonthValue()) {
				workHours += item.getAttendanceHours();
				if (item.getAttendanceStatus().contains("���")) {
					salaryDeduct = salaryDeduct - 500;
				}
				if (item.getAttendanceStatus().contains("�m¾")) {
					salaryDeduct = salaryDeduct - 1000;
				}
			}
		}

		if (workHours == 0) {
			res.setMessage("�䤣��ӭ��u���W�Z�ɶ�");
			return new SalarySystemRes(res.getMessage());
		}

		// �@��[�� (�`�ɼ�-�ɼƤW��) �[�Z�@�p�ɵ��@��
		int raisePay = (workHours - 174) * 100;

		// ��S���[�Z���i��O�t�ơA�G�n�k�s
		if (raisePay <= 0) {
			raisePay = raisePay * 0;
		}

		// �P�_�D�޶��h�A1�h�[���d
		int managerRaisePay = employeeInfo.getLevel() * 5000;

		// �p���`�~��
		int totalSalary = salary + raisePay + managerRaisePay + (salaryDeduct);

		SalarySystem finalSalarySystem = new SalarySystem(UUID.randomUUID(), req.getEmployeeCode(),
				employeeInfo.getName(), salaryDate, salary, raisePay, managerRaisePay, salaryDeduct, totalSalary);
		salarySystemDao.save(finalSalarySystem);
		res.setMessage("�s�W���\");
		return new SalarySystemRes(finalSalarySystem, res.getMessage());
	}

	// =====�ק��~����� (�򥻤W�u��ק侀�~)�A�L���~��
	@Override
	public SalarySystemRes updateSalarySystem(SalarySystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		SalarySystemRes res = new SalarySystemRes();

		// �S����J�����true�A�S����J�N��n�ק侀�~
		boolean checkDateIsNull = !StringUtils.hasText(req.getSalaryDate());

		// �S����J���~��true
		boolean checkSalaryIsNull = req.getSalary() == null;

		if (!checkSalaryIsNull && req.getSalary() <= 0) {
			res.setMessage("���~���i�p�� 0 ");
			return new SalarySystemRes(res.getMessage());
		}

		if (!StringUtils.hasText(req.getUuid()) || (checkDateIsNull && checkSalaryIsNull)) {
			res.setMessage("�ѼƤ����");
			return new SalarySystemRes(res.getMessage());
		}

		// �z�Luuid���o�ӭ��u��T
		Optional<SalarySystem> salarySystemOp = salarySystemDao.findById(uuid);
		if (!salarySystemOp.isPresent()) {
			res.setMessage("���~��T (�o�Ө��b�S����N�q)");
			return new SalarySystemRes(res.getMessage());
		}
		// ����
		SalarySystem salarySystem = salarySystemOp.get();

		// �]�����~�Q�ק�A�ҥH�n���s�p���`�~��
		int total = 0;

		// �S����J�����true
		if (checkDateIsNull) {
			total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
					+ (salarySystem.getSalaryDeduct());
			salarySystem.setSalary(req.getSalary());
			salarySystem.setTotalSalary(total);
			salarySystemDao.save(salarySystem);
			res.setMessage("�ק侀�~���\");
			return new SalarySystemRes(salarySystem, res.getMessage());
		}

		// �W���S���ױ��N��@�q����J����A�G�n�W�w��������W��F
		String checkDateString = "^[1-9]\\d{3}�~(0[1-9]|1[0-2]|[1-9])��([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])��";

		// �P�_����O�_�ŦX�榡
		boolean checkDate = req.getSalaryDate().matches(checkDateString);
		if (!checkDate) {
			res.setMessage("�榡��yyyy�~mm��dd��");
			return new SalarySystemRes(res.getMessage());
		}

		// ��������W��F
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy�~M��d��");

		/// �N����Ӫ�����r���ন���
		LocalDate salaryDate = LocalDate.parse(req.getSalaryDate(), format);

		// ���F�H���ק諸��������ƪ����p�A���X�Ө��b
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		// ���b
		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				res.setMessage("�H�s�W�L�o����u�Ӧ~���B�Ӥ�����~�����");
				salarySystem = null;
				return new SalarySystemRes(salarySystem, res.getMessage());
			}
		}

		// �n���X��J���o�Ӥ릳�S���W�Z�A�ήɼƧP�_
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		// �p���`�u�@�ɼ�
		int workHours = 0;

		for (var item : workSystemList) {
			if (item.getWorkTime().getYear() == salaryDate.getYear()
					&& item.getWorkTime().getMonthValue() == salaryDate.getMonthValue()) {
				workHours += item.getAttendanceHours();
			}
		}

		if (workHours <= 0) {
			res.setMessage("�䤣��ӭ��u���W�Z�ɶ�");
			return new SalarySystemRes(res.getMessage());
		}

		// �S����J���~��true�A�N��u�n�ק���
		if (checkSalaryIsNull) {
			salarySystem.setSalaryDate(salaryDate);
			salarySystemDao.save(salarySystem);
			res.setMessage("�ק������\");
			return new SalarySystemRes(salarySystem, res.getMessage());
		}

		// �W�����S�ױ��A�N���Ӭҭn�ק�
		total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
				+ (salarySystem.getSalaryDeduct());
		salarySystem.setSalaryDate(salaryDate);
		salarySystem.setSalary(req.getSalary());
		salarySystemDao.save(salarySystem);
		res.setMessage("�ק����B�~�ꦨ�\");
		return new SalarySystemRes(salarySystem, res.getMessage());
	}

	// =====�j�M��� (�����u��) ps.���u�u��j�M��ۤv�����
	@Override
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req) {
		SalarySystemRes res = new SalarySystemRes();
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());

		String checkDateString = "^[1-9]\\d{3}�~(0[1-9]|1[0-2]|[1-9])��([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])��";

		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy�~M��d��");

		if (!checkEmployeeCode || (!checkEmployeeCode && checkSearchStartDate && checkSearchEndDate)) {
			res.setMessage("�ѼƤ����");
			return new SalarySystemRes(res.getMessage());
		}

		if (checkSearchEndDate && !checkSearchStartDate) {
			res.setMessage("�п�J�}�l�ɶ�");
			return new SalarySystemRes(res.getMessage());
		}

		if (checkSearchStartDate && checkSearchEndDate) {

			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			boolean checkSearchMonthEndString = req.getSearchEndDate().matches(checkDateString);
			if (!checkSearchYearStaetString && !checkSearchMonthEndString) {
				res.setMessage("�榡��yyyy�~mm��dd��");
				return new SalarySystemRes(res.getMessage());
			}
		}

		if (checkSearchStartDate) {

			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			if (!checkSearchYearStaetString) {
				res.setMessage("�榡��yyyy�~mm��dd��");
				return new SalarySystemRes(res.getMessage());
			}
		}

		if (!checkSearchStartDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeOrderBySalaryDateDesc(req.getEmployeeCode());
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("�d�L���");
				return new SalarySystemRes(res.getMessage());
			}

			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		if (checkSearchStartDate && checkSearchEndDate) {

			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				res.setMessage("�����ɶ����i�p��}�l�ɶ�");
				return new SalarySystemRes(res.getMessage());
			}

			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							endDate);
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("�d�L���");
				return new SalarySystemRes(res.getMessage());
			}

			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		if (LocalDate.now().isBefore(startDate)) {
			res.setMessage("���Ѯɶ����i�p��}�l�ɶ�");
			return new SalarySystemRes(res.getMessage());
		}

		List<SalarySystem> salarySystemListInfo = salarySystemDao
				.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
						LocalDate.now());
		if (salarySystemListInfo.isEmpty()) {
			res.setMessage("�d�L���");
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

		String checkDateString = "^[1-9]\\d{3}�~(0[1-9]|1[0-2]|[1-9])��([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])��";

		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy�~M��d��");

		if (!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate) {
			res.setMessage("�ѼƤ����");
			return new SalarySystemRes(res.getMessage());
		}

		if (checkSearchEndDate && !checkSearchStartDate) {
			res.setMessage("�п�J�}�l�ɶ�");
			return new SalarySystemRes(res.getMessage());
		}

		if (checkSearchStartDate && checkSearchEndDate) {

			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			boolean checkSearchMonthEndString = req.getSearchEndDate().matches(checkDateString);
			if (!checkSearchYearStaetString && !checkSearchMonthEndString) {
				res.setMessage("�榡��yyyy�~mm��dd��");
				return new SalarySystemRes(res.getMessage());
			}
		}

		if (checkSearchStartDate) {

			boolean checkSearchYearStaetString = req.getSearchStartDate().matches(checkDateString);
			if (!checkSearchYearStaetString) {
				res.setMessage("�榡��yyyy�~mm��dd��");
				return new SalarySystemRes(res.getMessage());
			}
		}

		if (checkEmployeeCode && !checkSearchStartDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("�d�L���");
				return new SalarySystemRes(res.getMessage());
			}
			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		if (checkEmployeeCode && checkSearchStartDate && checkSearchEndDate) {
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				res.setMessage("�����ɶ����i�p��}�l�ɶ�");
				return new SalarySystemRes(res.getMessage());
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							endDate);
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("�d�L���");
				return new SalarySystemRes(res.getMessage());
			}
			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		if (checkEmployeeCode && checkSearchStartDate) {
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			if (LocalDate.now().isBefore(startDate)) {
				res.setMessage("���Ѯɶ����i�p��}�l�ɶ�");
				return new SalarySystemRes(res.getMessage());
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							LocalDate.now());
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("�d�L���");
				return new SalarySystemRes(res.getMessage());
			}
			res.setSalarySystemList(salarySystemListInfo);
			return res;

		}

		if (checkSearchStartDate && checkSearchEndDate) {
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				res.setMessage("�����ɶ����i�p��}�l�ɶ�");
				return new SalarySystemRes(res.getMessage());
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findBySalaryDateBetweenOrderBySalaryDateDesc(startDate, endDate);
			if (salarySystemListInfo.isEmpty()) {
				res.setMessage("�d�L���");
				return new SalarySystemRes(res.getMessage());
			}
			res.setSalarySystemList(salarySystemListInfo);
			return res;

		}

		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		if (LocalDate.now().isBefore(startDate)) {
			res.setMessage("���Ѯɶ����i�p��}�l�ɶ�");
			return new SalarySystemRes(res.getMessage());
		}
		List<SalarySystem> salarySystemListInfo = salarySystemDao
				.findBySalaryDateBetweenOrderBySalaryDateDesc(startDate, LocalDate.now());
		if (salarySystemListInfo.isEmpty()) {
			res.setMessage("�d�L���");
			return new SalarySystemRes(res.getMessage());
		}
		res.setSalarySystemList(salarySystemListInfo);
		return res;
	}

}
