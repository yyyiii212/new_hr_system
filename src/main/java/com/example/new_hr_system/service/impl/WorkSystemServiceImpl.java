package com.example.new_hr_system.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.entity.WorkSystem;
import com.example.new_hr_system.respository.AbsenceSystemDao;
import com.example.new_hr_system.respository.EmployeeInfoDao;
import com.example.new_hr_system.respository.SalarySystemDao;
import com.example.new_hr_system.respository.WorkSystemDao;
import com.example.new_hr_system.service.ifs.WorkSystemService;
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
	@Autowired
	private HttpSession httpSession;

	// -------------------------------------------------------------------------------

	// =====1.�W�Z���d
	@Override
	public WorkSystemRes punchToWork(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			res.setMessage("�ѼƭȤ��ର��");
			return new WorkSystemRes(res.getMessage());
		}
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeInfoOp.isPresent()) {
			res.setMessage("�䤣��ӭ��u");
			return new WorkSystemRes(res.getMessage());
		}
		// �ǥѭ��u�s��(���O�Dkey)�����
		List<WorkSystem> staffInfo = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		// �~���B����B�� ���@�ˮ� �N���L�d�F
		for (WorkSystem item : staffInfo) {
			LocalDate localDate = item.getWorkTime().toLocalDate();
			if (localDate.equals(LocalDate.now())) {
				res.setMessage("�ŭ��ƥ��d");
				return new WorkSystemRes(res.getMessage());
			}
		}
		res.setMessage("�W�Z���d���\");
		WorkSystem workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), LocalDateTime.now(), null,
				null, 0);
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, res.getMessage());

	}

	// =====2.�U�Z���d
	@Override
	public WorkSystemRes punchToOffWork(WorkSystemReq req) {

		UUID uuid = UUID.fromString(req.getUuid());
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getUuid())) {
			res.setMessage("�ѼƭȤ��ର��");
			return new WorkSystemRes(res.getMessage());
		}
		// �ШD�ýXuuid �A �]���W�Z�ɥ��L�d�F�A�T�|����(�e����ܮɡA�ǥ�"���s"���o�ýX)
		Optional<WorkSystem> workSystemOp = workSystemDao.findById(uuid);

		// �]���@�w���A�ҥH���ӵ����
		WorkSystem workSystem = workSystemOp.get();

		// �v�˨��䪺"�а�"�|�b�ڳo��s�W (ps.�o�̩|���T�w)
		if ((workSystem.getAttendanceStatus() != null) || workSystem.getOffWorkTime() != null) {
			res.setMessage("�Фŧ�若�d���e");
			return new WorkSystemRes(res.getMessage());
		}

		// ���o�̪����b�Sԣ�N�q�A�]���e�ݷ|��ܪ���� �A��Ʈw�@�w����uuid
		if (!workSystemOp.isPresent()) {
			res.setMessage("�Ѽƭȿ��~");
			return new WorkSystemRes(res.getMessage());
		}

		// �T�{�~���B����B�Ѽ� ���ۦP�N���O��ѥ��d(ps.�ѤF���d)
		LocalDate workDate = workSystem.getWorkTime().toLocalDate();
		if (!workDate.equals(LocalDate.now())) {
			res.setMessage("����ɥ��d");
			return new WorkSystemRes(res.getMessage());
		}

		// ���`�W�U�Z���ɼ�(�{�b�ɶ�-�W�Z�ɶ�)
		int countOffWorkHours = LocalDateTime.now().getHour() - 9;

		// ���Φ��h�ɪ��p��ɼ� (�U�Z-�L�Ӫ��ɶ�)
		int countWorkLateOrLeaveTime = LocalDateTime.now().getHour() - workSystem.getWorkTime().getHour();

		// �r���s���p
		String attendanceStatusStr;

		// �Ĥ@��:���+���h = �W�Z�ɶ� >= 9:00 & �W�Z���� >= 1 & �U�Z�ɼ� < 8 (ps.�p�J�ɼƬ� : ��쪺�ɼ�)
		// �ĤG��:��� =�W�Z�p�� >= 9:00 & �W�Z���� >= 1 (���F��9:00) �άO �W�Z�p�� > 9:00 (ps.�p�J�ɼƬ� : ��쪺�ɼ�)
		// �ĤT��:���h = (���`�W�U�Z���ɼ�) < 8 (ps.�p�J�ɼƬ� : �U�Z�ɶ�-9�I ���ɼ�) # ���h�S�����
		// �ĥ|��:���` = �W�z�����X (ps.�p�J�ɼƬ� :���`���ɼ�)

		if ((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				&& countOffWorkHours < 8) {
			attendanceStatusStr = "���+���h";
			workSystem.setOffWorkTime(LocalDateTime.now());
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);

		} else if ((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				|| workSystem.getWorkTime().getHour() > 9) {
			attendanceStatusStr = "���";
			workSystem.setOffWorkTime(LocalDateTime.now());
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);
		} else if (countOffWorkHours < 8) {
			attendanceStatusStr = "���h";
			if (countOffWorkHours <= 0) {
				countOffWorkHours = 0;
			}
			workSystem.setOffWorkTime(LocalDateTime.now());
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		} else {
			attendanceStatusStr = "���`";
			workSystem.setOffWorkTime(LocalDateTime.now());
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		}
		workSystemDao.save(workSystem);
		res.setMessage("�U�Z���d���\");
		return new WorkSystemRes(workSystem, res.getMessage());

	}

	// =====�j�M���d���(�����u��)
	@Override
	public WorkSystemRes searchWorkInfoForStaff(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());// �����u�s��true
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());// ���}�l���true
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());// ���������true
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		// ���u�u��j����ۤv����ơA�G�o��n�P�_
		if (!checkEmployeeCode || (!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate)) {
			return new WorkSystemRes("�ѼƩέ��u�s�����ର��");
		}
		// �������ɶ����S���}�l�ɶ��n���b
		if (!checkSearchStartDate && checkSearchEndDate) {
			return new WorkSystemRes("��J�}�l�ɶ�");
		}

		// �S����J�}�l�ɶ��@�w�|���L��
		if (!checkSearchStartDate) {
			List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeOrderByWorkTimeDesc(req.getEmployeeCode());
			if (workInfoList.isEmpty()) {
				return new WorkSystemRes("�d�L���");
			}
			res.setWorkInfoList(workInfoList);
			return res;
		}

		// �}�l�B�����ɶ�������J
		if (checkSearchStartDate && checkSearchEndDate) {
			// �T�{�O�_�ŦX���W��F
			if (!req.getSearchStartDate().matches(checkDateString)
					|| !req.getSearchEndDate().matches(checkDateString)) {
				res.setMessage("����榡���~ �п�J(yyyy�~mm��dd��)");
				return new WorkSystemRes(res.getMessage());
			}
			// ���U���N��i�H�����F
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				return new WorkSystemRes("�����ɶ����i�p��}�l�ɶ�");
			}

			// �]��worktime���榡�OLocalDateTime �ҥH�n�NLocalDate�૬��LocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atStartOfDay();
			// ���X�ŦX��檺���
			List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(
					req.getEmployeeCode(), startDateTime, endDateTime);
			if (workInfoList.isEmpty()) {
				return new WorkSystemRes("�d�L���");
			}
			res.setWorkInfoList(workInfoList);
			return res;
		}
		// �W�����S�ױ��N�����u�s���B�}�l����A�S���������
		if (!req.getSearchStartDate().matches(checkDateString)) {
			res.setMessage("����榡���~ �п�J(yyyy�~mm��dd��)");
			return new WorkSystemRes(res.getMessage());
		}
		// �u�n�N�}�l������Y�iLocalDate
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		// �����ɶ��Τ���
		LocalDate endDate = LocalDate.now();
		if (endDate.isBefore(startDate)) {
			res.setMessage("���Ѯɶ����i�p��}�l�ɶ�");
			return new WorkSystemRes(res.getMessage());
		}
		// �]��worktime���榡�OLocalDateTime �ҥH�n�NLocalDate�૬��LocalDateTime
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atStartOfDay();
		List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(
				req.getEmployeeCode(), startDateTime, endDateTime);
		if (workInfoList.isEmpty()) {
			res.setMessage("�d�L���");
			return new WorkSystemRes(res.getMessage());
		}
		res.setWorkInfoList(workInfoList);
		return res;
	}

	// =====�j�M���d���(���D�ު�)
	@Override
	public WorkSystemRes searchWorkInfoForManager(WorkSystemReq req, HttpSession httpSession) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�п�J�z���D�޽s��");
		}
		Optional<EmployeeInfo> employeeInfoManagerOp = employeeInfoDao.findById(req.getManagerEmployeeCode());// 1
		if (!employeeInfoManagerOp.isPresent()) {
			return new WorkSystemRes("�п�J�z�����T�s��");
		}
		EmployeeInfo employeeManagerInfo = employeeInfoManagerOp.get();// 2
		List<WorkSystem> workInfoAllList = new ArrayList<>();// 3

		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());// �����u�s��true
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());// ���}�l���true
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());// ���������true
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		// �T�̳��S��J �^�ǩҦ���T���D�ެ�
		if ((!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate)) {
			List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();
			for (var item : workInfoList) {// 4
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
					workInfoAllList.add(item);// 8

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("�ӳ����S����ơA�нT�{�ӳ����O�_���ӭ��u");
			}
			return new WorkSystemRes(workInfoAllList, " ���� : " + employeeManagerInfo.getSection());
		}

		// ����������A���S���}�l��������b
		if (checkSearchEndDate && !checkSearchStartDate) {
			return new WorkSystemRes("��J�}�l���");
		}

		// ���}�l�B����������P�_�A���W��F���b
		if (checkSearchStartDate && checkSearchEndDate) {
			if (!req.getSearchStartDate().matches(checkDateString)
					|| !req.getSearchEndDate().matches(checkDateString)) {
				res.setMessage("����榡���~ �п�J(yyyy�~mm��dd��)");
				return new WorkSystemRes(res.getMessage());
			}
		}
		// ���}�l������P�_�A���W��F���b
		if (checkSearchStartDate) {
			if (!req.getSearchStartDate().matches(checkDateString)) {
				res.setMessage("����榡���~ �п�J(yyyy�~mm��dd��)");
				return new WorkSystemRes(res.getMessage());
			}
		}

		// �P�_����J���u�s���A���S����J�}�l���
		if (checkEmployeeCode && !checkSearchStartDate) {
			List<WorkSystem> workInfoListByEmployeeCode = workSystemDao
					.findByEmployeeCodeOrderByWorkTimeDesc(req.getEmployeeCode());
			if (workInfoListByEmployeeCode.isEmpty()) {
				return new WorkSystemRes("�d�L���");
			}
			for (var item : workInfoListByEmployeeCode) {// 4
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
					workInfoAllList.add(item);// 8

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("�ӳ����S����ơA�нT�{�ӳ����O�_���ӭ��u");
			}
			return new WorkSystemRes(workInfoAllList, " ���� : " + employeeManagerInfo.getSection());
		}

		// �T�̳�����J ���u�s���B�}�l�B�������
		if (checkEmployeeCode && checkSearchStartDate && checkSearchEndDate) {
			// �૬
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			if (endDate.isBefore(startDate)) {
				res.setMessage("�����ɶ����p��}�l�ɶ�");
				return new WorkSystemRes(res.getMessage());
			}
			// ���F��iJPA��k�A�নLocalDateTime�A�]���u�@�ɶ��OLocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atStartOfDay();
			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(req.getEmployeeCode(), startDateTime,
							endDateTime);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				return new WorkSystemRes("�d�L���");
			}
			for (var item : workInfoListByEmployeeCodeAndDate) {// 4
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
					workInfoAllList.add(item);// 8

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("�ӳ����S����ơA�нT�{�ӳ����O�_���ӭ��u");
			}

			return new WorkSystemRes(workInfoAllList, " ���� : " + employeeManagerInfo.getSection());

		}
		// �P�_�u�����u�s���B�}�l���
		if (checkEmployeeCode && checkSearchStartDate) {
			// �u�n��}�l����Y�i
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			if (LocalDate.now().isBefore(startDate)) {
				res.setMessage("���Ѥ��i�p��}�l�ɶ�");
				return new WorkSystemRes(res.getMessage());
			}
			// ���F��iJPA��k�A�নLocalDateTime�A�]���u�@�ɶ��OLocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDate endDateNow = LocalDate.now();
			LocalDateTime endDateTimeNow = endDateNow.atStartOfDay();
			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(req.getEmployeeCode(), startDateTime,
							endDateTimeNow);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				res.setMessage("�d�L���");
				return new WorkSystemRes(res.getMessage());
			}
			for (var item : workInfoListByEmployeeCodeAndDate) {// 4
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
					workInfoAllList.add(item);// 8

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("�ӳ����S����ơA�нT�{�ӳ����O�_���ӭ��u");
			}
			return new WorkSystemRes(workInfoAllList, " ���� : " + employeeManagerInfo.getSection());
		}
		// �W�����S�ױ��A�N��@�w�S�����u�s�� (�j�M�ɶ��϶����Ҧ����u)
		if (checkSearchStartDate && checkSearchEndDate) {
			// �N�}�l�B��������૬
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
			// ���F��iJPA��k�A�নLocalDateTime�A�]���u�@�ɶ��OLocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atStartOfDay();
			if (endDate.isBefore(startDate)) {
				res.setMessage("�����ɶ����p��}�l�ɶ�");
				return new WorkSystemRes(res.getMessage());
			}
			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByWorkTimeBetweenOrderByWorkTimeDesc(startDateTime, endDateTime);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				return new WorkSystemRes("�d�L���");
			}
			for (var item : workInfoListByEmployeeCodeAndDate) {// 4
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
					workInfoAllList.add(item);// 8

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("�ӳ����S����ơA�нT�{�ӳ����O�_���ӭ��u");
			}
			return new WorkSystemRes(workInfoAllList, " ���� : " + employeeManagerInfo.getSection());
		}
		// �W�����S�ױ��N��S�����u�s���B�������
		// �u�n��}�l����Y�i
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		if (LocalDate.now().isBefore(startDate)) {
			res.setMessage("���Ѯɶ����i�p��}�l�ɶ�");
			return new WorkSystemRes(res.getMessage());
		}
		// ���F��iJPA��k�A�নLocalDateTime�A�]���u�@�ɶ��OLocalDateTime
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDate endDateNow = LocalDate.now();
		LocalDateTime endDateTimeNow = endDateNow.atStartOfDay();
		List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
				.findByWorkTimeBetweenOrderByWorkTimeDesc(startDateTime, endDateTimeNow);
		if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
			return new WorkSystemRes("�d�L���");
		}
		for (var item : workInfoListByEmployeeCodeAndDate) {// 4
			Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());// 5
			EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
			if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {// 7
				workInfoAllList.add(item);// 8

			}
		}
		if (workInfoAllList.isEmpty()) {
			return new WorkSystemRes("�ӳ����S����ơA�нT�{�ӳ����O�_���ӭ��u");
		}
		return new WorkSystemRes(workInfoAllList, " ���� : " + employeeManagerInfo.getSection());

	}

	// =====�R�����d���(���D�ު�)=>�R���ɶ��϶������
	@Override
	public WorkSystemRes deleteWorkInfoByDateBetween(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getSearchStartDate()) || !StringUtils.hasText(req.getSearchEndDate())) {
			List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();
			return new WorkSystemRes(workInfoList,"�R���e���ݬݧa");
		}
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		if (!req.getSearchStartDate().matches(checkDateString) || !req.getSearchEndDate().matches(checkDateString)) {
			res.setMessage("����榡���~ �п�J(yyyy�~mm��dd��)");

			return new WorkSystemRes(res.getMessage());
		}
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
		if (endDate.isBefore(startDate)) {
			res.setMessage("�}�l�ɶ����i�j�󵲧��ɶ�");
			return new WorkSystemRes( res.getMessage());
		}
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atStartOfDay();
		workSystemDao.deleteByWorkTimeBetween(startDateTime, endDateTime);
		List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();
		return new WorkSystemRes(workInfoList, "�R�����\");
	}

	// =====�s�W�m¾���(���D�ު�)
	@Override
	public WorkSystemRes creatAbsenteeismForManager(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getEmployeeCode()) || !StringUtils.hasText(req.getAbsenteeismDate())
				|| !StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�ѼƭȤ��ର��");
		}
		Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getEmployeeCode());// 5
		Optional<EmployeeInfo> employeeManagerOp = employeeInfoDao.findById(req.getManagerEmployeeCode());// 5
		if (!employeeStaffOp.isPresent() || !employeeManagerOp.isPresent()) {
			return new WorkSystemRes("�п�J���T�s��");
		}
		EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
		EmployeeInfo employeeManagerInfo = employeeManagerOp.get();// 6

		if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
			return new WorkSystemRes("�z�P�ӭ��u���P����");
		}
		// ����x���W��F
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		if (!req.getAbsenteeismDate().matches(checkDateString)) {
			res.setMessage("����榡���~ �п�J(yyyy�~mm��dd��)");
			return new WorkSystemRes(res.getMessage());
		}
		LocalDate absenteeismDate = LocalDate.parse(req.getAbsenteeismDate(), formatDate);
		// �]���������D���H�N�O�m¾�A�n�T�{���u���ѨS�Ӥ~��O
		if (absenteeismDate.isAfter(LocalDate.now())) {
			res.setMessage(req.getAbsenteeismDate() + "�L��~��n���m¾");
			return new WorkSystemRes(res.getMessage());
		}
		// �n���X�Ԭ����A����D�ަb���u���Ӫ����p�U�ðO�m¾
		List<WorkSystem> staffInfo = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		for (var item : staffInfo) {
			LocalDate localDate = item.getWorkTime().toLocalDate();
			if (localDate.equals(absenteeismDate)) {
				res.setMessage("�o����u�o�Ѧ��W�Z�ΥH�O�m¾�L");
				return new WorkSystemRes(res.getMessage());
			}
		}
		// �W�����S�ױ��N��u�x�m¾�F
		LocalDateTime absenteeismDateTime = absenteeismDate.atStartOfDay();
		WorkSystem workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), absenteeismDateTime,
				absenteeismDateTime, "�m¾", 0);
		res.setMessage("�s�W�m¾���\");
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, res.getMessage());

	}

	// =====�R���m¾���
	@Override
	public WorkSystemRes deleteAbsenteeismForManager(WorkSystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getUuid())) {
			res.setMessage("�ѼƭȤ����");
			return new WorkSystemRes(res.getMessage());
		}
		// �ШD�ýXuuid �A �]���W�Z�ɥ��L�d�F�A�T�|����(�e����ܮɡA�ǥ�"���s"���o�ýX)
		Optional<WorkSystem> workSystemOp = workSystemDao.findById(uuid);
		// ���o�̪����b�Sԣ�N�q�A�]���e�ݷ|��ܪ���� �A��Ʈw�@�w����uuid
		if (!workSystemOp.isPresent()) {
			return new WorkSystemRes("�Ѽƭȿ��~");
		}
		WorkSystem workSystem = workSystemOp.get();
		// �T�{�O�D�ޤ��p�߰O<�m¾>�~��R���A�H���R������u���ӤW�Z������
		if (workSystem.getAttendanceStatus().equals("�m¾")) {
			workSystemDao.deleteById(uuid);
			res.setMessage("�R���m¾���\");
			return new WorkSystemRes(res.getMessage());
		}
		return new WorkSystemRes("�o����u�o�ѨS���m¾");
	}

	// =====�L�X�ӭ��u���d���(�e�ݵ����d�U�Z��)
	@Override
	public WorkSystemRes getWorkInfoListToday(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new WorkSystemRes("�ѼƭȤ��ର��");
		}
		LocalDate nowDate = LocalDate.now();
		LocalDateTime nowDateTime = nowDate.atStartOfDay();
		List<WorkSystem> workInfoList = workSystemDao
				.findByEmployeeCodeAndWorkTimeGreaterThanEqual(req.getEmployeeCode(), nowDateTime);
		if (workInfoList.isEmpty()) {
			return new WorkSystemRes("�A�O���O�W�Z�S���d!?");
		}
		res.setWorkInfoList(workInfoList);
		return res;
	}

	// ===�R���m¾�e�A�C�X��T�A�ǥѫ��s����uuid 
	@Override
	public WorkSystemRes getWorkInfoListAbsenteeism(WorkSystemReq req) {
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		if (!StringUtils.hasText(req.getAbsenteeismDate()) || !StringUtils.hasText(req.getEmployeeCode())
				|| !StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�ѼƭȩΤ�����ର��");
		}
		LocalDate absenteeismDate = LocalDate.parse(req.getAbsenteeismDate(), formatDate);
		Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getManagerEmployeeCode());// 5
		Optional<EmployeeInfo> employeeManagerOp = employeeInfoDao.findById(req.getEmployeeCode());// 5
		if (!employeeStaffOp.isPresent() || !employeeManagerOp.isPresent()) {
			return new WorkSystemRes("�п�J���T�s��");
		}
		EmployeeInfo employeeStaffInfo = employeeStaffOp.get();// 6
		EmployeeInfo employeeManagerInfo = employeeManagerOp.get();// 6
		if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
			return new WorkSystemRes("�z�P�ӭ��u���P����");
		}
		LocalDateTime absenteeismDateTime = absenteeismDate.atStartOfDay();
		List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeAndWorkTime(req.getEmployeeCode(),
				absenteeismDateTime);
		if (workInfoList.isEmpty()) {
			return new WorkSystemRes("�d�L���");
		}
		return new WorkSystemRes(workInfoList, " ���� : " + employeeManagerInfo.getSection());
	}

}
