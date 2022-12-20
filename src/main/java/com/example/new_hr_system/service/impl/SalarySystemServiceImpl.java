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
			return new SalarySystemRes("�ѼƤ����");
		}
		return null;

	}

//===============================================================================

	// =====�s�W�~�����(���D��)
	@Override
	public SalarySystemRes creatSalarySystem(SalarySystemReq req) {
		SalarySystemRes res = check(req);
		// ���b (������W��F)�B���u�s���ŭ�
		if (res != null) {
			return res;
		}
		res = new SalarySystemRes();
		Optional<EmployeeInfo> salaryEmployeeInfoOp = employeeInfoDao.findById(req.getSalaryEmployeeCode());
		if (!salaryEmployeeInfoOp.isPresent()) {
			return new SalarySystemRes("���˹�z���s��");
		}
		EmployeeInfo managerEmployeeInfo = salaryEmployeeInfoOp.get();
		// �n�ǥѳo�i����ƨ��o�ݭn����T �p�� 1.�~�� 2.�m�W 3.�D�޵���
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeInfoOp.isPresent()) {
			return new SalarySystemRes("�䤣��ӭ��u");
		}
		EmployeeInfo employeeInfo = employeeInfoOp.get();
		if (!managerEmployeeInfo.getSection().equals(employeeInfo.getSection())) {
			return new SalarySystemRes("�A�̬O���P������");
		}

		// �קK�s�W��P�@����u �b�P�~�P�릳�ⵧ�ۦP���
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());

		// �N����Ӫ����<�r��>�ର���
		LocalDate salaryDate = req.getSalaryDate();

		for (var item : salarySystemList) {
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				return new SalarySystemRes("�H�s�W�L�o����u�Ӧ~�B�Ӥ몺�~�����");
			}
		}

		// �ݭn�p��u�@�ɼ�
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		if (workSystemList.isEmpty()) {
			return new SalarySystemRes("�䤣��ӭ��u�����d���");
		}

		// ��new�X��
		SalarySystem salarySystem = new SalarySystem();

		// �]���o�̭n���o�w�]�����~
		int salary = salarySystem.getSalary();

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
			if ((item.getWorkTime().getYear() == salaryDate.getYear()
					&& item.getWorkTime().getMonthValue() + 1 == salaryDate.getMonthValue())
					|| (item.getWorkTime().getYear() + 1 == salaryDate.getYear()
							&& item.getWorkTime().getMonthValue() == 12 && salaryDate.getMonthValue() == 1)) {
				workHours += item.getAttendanceHours();
				if (item.getAttendanceStatus() == null || item.getAttendanceStatus().length() == 0) {
					salaryDeduct = salaryDeduct - 100;// �ѤF���d
					continue;
				}
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
		return new SalarySystemRes(finalSalarySystem, "�s�W���\");
	}

	// =====�ק��~����� (�򥻤W�u��ק侀�~)�A�L���~�� (���D��)
	@Override
	public SalarySystemRes updateSalarySystem(SalarySystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		SalarySystemRes res = new SalarySystemRes();
		if (!StringUtils.hasText(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("�п�J�D�޽s��");
		}
		// �S����J�����true�A�S����J�N��n�ק侀�~
		boolean checkDateIsNull = false;
		if (req.getSalaryDate() == null) {
			checkDateIsNull = true;
		}

		// �S����J���~��true
		boolean checkSalaryIsNull = req.getSalary() == null;

		if (!checkSalaryIsNull && req.getSalary() <= 0) {
			return new SalarySystemRes("���~���i�p�� 0 ");
		}

		if (!StringUtils.hasText(req.getUuid()) || (checkDateIsNull && checkSalaryIsNull)) {
			return new SalarySystemRes("�ѼƤ����");
		}

		// �z�Luuid���o�ӭ��u��T
		Optional<SalarySystem> salarySystemOp = salarySystemDao.findById(uuid);
		if (!salarySystemOp.isPresent()) {
			return new SalarySystemRes("���~��T (�o�Ө��b�S����N�q)");
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
			return new SalarySystemRes(salarySystem, "�ק侀�~���\");
		}
		// �W���S���ױ��N��@�q����J����A�G�n�W�w��������W��F
		// �P�_����O�_�ŦX�榡

		/// �N����Ӫ�����r���ন��� (req)
		LocalDate salaryDate = req.getSalaryDate();

		// ���F�H���ק諸��������ƪ����p�A���X�Ө��b
		List<SalarySystem> salarySystemList = salarySystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		// ���b
		for (var item : salarySystemList) {
			if (item == salarySystem) {
				continue;
			}
			if (item.getSalaryDate().getYear() == salaryDate.getYear()
					&& item.getSalaryDate().getMonthValue() == salaryDate.getMonthValue()) {
				return new SalarySystemRes("�H�s�W�L�o����u�Ӧ~���B�Ӥ�����~�����");
			}
		}

		// �n���X��J���o�Ӥ릳�S���W�Z�A�ήɼƧP�_
		List<WorkSystem> workSystemList = workSystemDao.findByEmployeeCode(salarySystem.getEmployeeCode());

		// �p���`�u�@�ɼ�
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
			return new SalarySystemRes("�䤣��ӭ��u���W�Z�ɶ�");
		}

		// �S����J���~��true�A�N��u�n�ק���
		if (checkSalaryIsNull) {
			salarySystem.setSalaryDate(salaryDate);
			salarySystemDao.save(salarySystem);
			return new SalarySystemRes(salarySystem, "�ק������\");
		}
		// �W�����S�ױ��A�N���Ӭҭn�ק�
		total = req.getSalary() + salarySystem.getRaisePay() + salarySystem.getManagerRaisePay()
				+ (salarySystem.getSalaryDeduct());
		salarySystem.setSalaryDate(salaryDate);
		salarySystem.setSalary(req.getSalary());
		salarySystem.setTotalSalary(total);
		salarySystemDao.save(salarySystem);
		return new SalarySystemRes(salarySystem, "�ק����B�~�ꦨ�\");
	}

	// =====�j�M��� (�����u��) ps.���u�u��j�M��ۤv�����
	@Override
	public SalarySystemRes searchSalarySystemForStaff(SalarySystemReq req) {
		SalarySystemRes res = new SalarySystemRes();
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		boolean checkSearchStartDate = req.getSearchStartDate() != null;
		boolean checkSearchEndDate = req.getSearchEndDate() != null;
		if (!checkEmployeeCode || (!checkEmployeeCode && checkSearchStartDate && checkSearchEndDate)) {
			res.setMessage("�ѼƤ����");
			return new SalarySystemRes(res.getMessage());
		}

		if (checkSearchEndDate && !checkSearchStartDate) {
			res.setMessage("�п�J�}�l�ɶ�");
			return new SalarySystemRes(res.getMessage());
		}

		if (!checkSearchStartDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeOrderBySalaryDateDesc(req.getEmployeeCode());
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("�d�L���");
			}

			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		if (checkSearchStartDate && checkSearchEndDate) {
			LocalDate startDate = req.getSearchStartDate();
			LocalDate endDate = req.getSearchEndDate();
			if (endDate.isBefore(startDate)) {
				return new SalarySystemRes("�����ɶ����i�p��}�l�ɶ�");
			}

			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							endDate);
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("�d�L���");
			}

			res.setSalarySystemList(salarySystemListInfo);
			return res;
		}

		LocalDate startDate = req.getSearchStartDate();
		if (LocalDate.now().isBefore(startDate)) {
			return new SalarySystemRes("���Ѯɶ����i�p��}�l�ɶ�");
		}

		List<SalarySystem> salarySystemListInfo = salarySystemDao
				.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
						LocalDate.now());
		if (salarySystemListInfo.isEmpty()) {
			return new SalarySystemRes("�d�L���");
		}
		res.setSalarySystemList(salarySystemListInfo);
		return res;
	}

//-----------------------------------�D�ޥi�j�M��Ҧ��H���~�����---------------------------------------------------
	@Override
	public SalarySystemRes searchSalarySystemForManager(SalarySystemReq req) {
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		boolean checkSearchStartDate = req.getSearchStartDate() != null;
		boolean checkSearchEndDate = req.getSearchEndDate() != null;
		List<SalarySystem> salaryList = new ArrayList<>();
		if (!StringUtils.hasText(req.getSalaryEmployeeCode())) {
			return new SalarySystemRes("�п�J�D�޽s��");
		}
		Optional<EmployeeInfo> managerEmployeeInfoOp = employeeInfoDao.findById(req.getSalaryEmployeeCode());
		if (!managerEmployeeInfoOp.isPresent()) {
			return new SalarySystemRes("�䤣��ӥD��");
		}
		EmployeeInfo managerEmployeeInfo = managerEmployeeInfoOp.get();
		// �Q�j�M���u�s�����B�}�l����B����������S��J (�ݹL�{)
		if (!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate) {
			List<SalarySystem> salarySystemListInfo = salarySystemDao.findByOrderBySalaryDateDesc();
			for (var item : salarySystemListInfo) {
				Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(item.getEmployeeCode());
				EmployeeInfo employeeInfo = employeeInfoOp.get();
				if (employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
					salaryList.add(item);
				}
			}
			return new SalarySystemRes(salaryList, "���� : " + managerEmployeeInfo.getSection());
		}

		if (checkSearchEndDate && !checkSearchStartDate) {
			return new SalarySystemRes("�п�J�}�l�ɶ�");
		}
		// ����J���u�s���B�S���}�l���
		if (checkEmployeeCode && !checkSearchStartDate) {
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeInfoOp.isPresent()) {
				return new SalarySystemRes("�п�J���T���u�s��");
			}
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (!employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				return new SalarySystemRes("�z�P���u���P����");
			}

			List<SalarySystem> salarySystemListInfo = salarySystemDao.findByEmployeeCode(req.getEmployeeCode());
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("�d�L���");
			}
			return new SalarySystemRes(salarySystemListInfo, "���� : " + managerEmployeeInfo.getSection());
		}
		// �����u�s���B�}�l��B�������
		if (checkEmployeeCode && checkSearchStartDate && checkSearchEndDate) {
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeInfoOp.isPresent()) {
				return new SalarySystemRes("�п�J���T���u�s��");
			}
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (!employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				return new SalarySystemRes("�z�P�o����u���P����");
			}
			LocalDate startDate = req.getSearchStartDate();
			LocalDate endDate = req.getSearchEndDate();
			if (endDate.isBefore(startDate)) {
				return new SalarySystemRes("�����ɶ����i�p��}�l�ɶ�");
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							endDate);
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("�d�L���");
			}
			return new SalarySystemRes(salarySystemListInfo, "���� : " + managerEmployeeInfo.getSection());
		}
		// �����u�s���B�}�l���
		if (checkEmployeeCode && checkSearchStartDate) {
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeInfoOp.isPresent()) {
				return new SalarySystemRes("�п�J���T���u�s��");
			}
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (!employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				return new SalarySystemRes("�z�P�o����u���P����");
			}
			LocalDate startDate = req.getSearchStartDate();
			if (LocalDate.now().isBefore(startDate)) {
				return new SalarySystemRes("���Ѯɶ����i�p��}�l�ɶ�");
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(req.getEmployeeCode(), startDate,
							LocalDate.now());
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("�d�L���");
			}
			return new SalarySystemRes(salarySystemListInfo, "���� : " + managerEmployeeInfo.getSection());

		}
		// �j�M�}�l����B������� <�W�����S�ױ��A�����@�w�S�����w���u�s��> (�n�L�o)
		if (checkSearchStartDate && checkSearchEndDate) {
			LocalDate startDate = req.getSearchStartDate();
			LocalDate endDate = req.getSearchEndDate();
			if (endDate.isBefore(startDate)) {
				return new SalarySystemRes("�����ɶ����i�p��}�l�ɶ�");
			}
			List<SalarySystem> salarySystemListInfo = salarySystemDao
					.findBySalaryDateBetweenOrderBySalaryDateDesc(startDate, endDate);
			if (salarySystemListInfo.isEmpty()) {
				return new SalarySystemRes("�d�L���");
			}
			for (var item : salarySystemListInfo) {
				Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(item.getEmployeeCode());
				EmployeeInfo employeeInfo = employeeInfoOp.get();
				if (employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
					salaryList.add(item);
				}
			}
			return new SalarySystemRes(salaryList, "���� : " + managerEmployeeInfo.getSection());
		}
		// �W�����S�ױ��A�����u����J�}�l��� (�ݹL�{)
		LocalDate startDate = req.getSearchStartDate();
		if (LocalDate.now().isBefore(startDate)) {
			return new SalarySystemRes("���Ѯɶ����i�p��}�l�ɶ�");
		}
		List<SalarySystem> salarySystemListInfo = salarySystemDao
				.findBySalaryDateBetweenOrderBySalaryDateDesc(startDate, LocalDate.now());
		if (salarySystemListInfo.isEmpty()) {
			return new SalarySystemRes("�d�L���");
		}
		for (var item : salarySystemListInfo) {
			Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(item.getEmployeeCode());
			EmployeeInfo employeeInfo = employeeInfoOp.get();
			if (employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
				salaryList.add(item);
			}
		}
		return new SalarySystemRes(salaryList, "���� : " + managerEmployeeInfo.getSection());
	}

}
