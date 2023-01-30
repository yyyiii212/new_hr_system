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

	// -------------------------------------------------------------------------------

	/*------------------------------------------------(���u)���d�W�Z*/
	@Override
	public WorkSystemRes punchToWork(WorkSystemReq req) {

		// �W�Z�u�ॴ�ۤv���d�A�G�n���b �ݬO�_����

		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new WorkSystemRes("�ѼƭȤ��ର��");
		}

		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());

		if (!employeeInfoOp.isPresent()) {
			return new WorkSystemRes("�䤣��ӭ��u");
		}

		// �ǥѭ��u�s�������(�P�_���L���ƥ��d)
		List<WorkSystem> staffInfo = workSystemDao.findByEmployeeCode(req.getEmployeeCode());

		// �~���B����B�� ���@�ˮ� �N���L�d�F
		for (WorkSystem item : staffInfo) {
			LocalDate localDate = item.getWorkTime().toLocalDate();
			if (localDate.equals(LocalDate.now())) {
				return new WorkSystemRes("�ŭ��ƥ��d");
			}

		}
		DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm");
		String nowDateTimeString = LocalDateTime.now().format(formatDateTime);
		LocalDateTime finalDateTime = LocalDateTime.parse(nowDateTimeString, formatDateTime);
		WorkSystem workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), finalDateTime, null, null, 0);
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, "�W�Z���d���\");

	}

	/*------------------------------------------------(���u)���d�U�Z*/
	@Override
	public WorkSystemRes punchToOffWork(WorkSystemReq req) {
		// �o�Ө��b�S�����
		if (!StringUtils.hasText(req.getUuid())) {
			return new WorkSystemRes("�ѼƭȤ��ର��");
		}

		UUID uuid = UUID.fromString(req.getUuid());

		// �ШD�ýXuuid �A �]���W�Z�ɥ��L�d�F�A�T�|����(�e����ܮɡA�ǥ�"���s"���o�ýX)
		Optional<WorkSystem> workSystemOp = workSystemDao.findById(uuid);

		// ���o�̪����b�Sԣ�N�q�A�]���e�ݷ|��ܪ���� �A��Ʈw�@�w����uuid
		if (!workSystemOp.isPresent()) {
			return new WorkSystemRes("�Ѽƭȿ��~");
		}

		// �ǥ�uuid����Ӹ��
		WorkSystem workSystem = workSystemOp.get();

		// ���U�Z�ɶ��άO����ѤW�Z���p�ɴN�|�ױ�
		if ((workSystem.getAttendanceStatus() != null) || workSystem.getOffWorkTime() != null) {
			return new WorkSystemRes("�Фŧ�若�d���e");
		}

		// �T�{�~���B����B�Ѽ� ���ۦP�N���O��ѥ��d(ps.�ѤF���d) <���o�����ӨS�Σ{>
		LocalDate workDate = workSystem.getWorkTime().toLocalDate();
		if (!workDate.equals(LocalDate.now())) {
			return new WorkSystemRes("�Хh��D�����A�ɥ��d");
		}

		// ���`�W�U�Z���ɼ�(�{�b�ɶ�-�W�Z�ɶ�) ps.(9 = �W�Z�W�w�X�I�n��)
		int countOffWorkHours = LocalDateTime.now().getHour() - 9;

		// ���Φ��h�ɪ��p��ɼ� (�U�Z�ɶ�-�L�Ӫ��ɶ�)
		int countWorkLateOrLeaveTime = LocalDateTime.now().getHour() - workSystem.getWorkTime().getHour();

		// �r���s���p
		String attendanceStatusStr;
		DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm");
		String nowDateTimeString = LocalDateTime.now().format(formatDateTime);
		LocalDateTime finalDateTime = LocalDateTime.parse(nowDateTimeString, formatDateTime);

		// �Ĥ@��:���+���h = (�W�Z�ɶ� >= 9:00 & �W�Z���� >= 1 & �U�Z�p�� < 18) ||(�W�Z�ɶ� > 9:00 & �U�Z�ɶ� <
		// 18)
		// 1 = > (ps.�p�J�ɼƬ� : ��쪺�ɼ� ps.18=�X�I�U�Z)
		// �ĤG��:��� =�W�Z�p�� >= 9:00 & �W�Z���� >= 1 (���F��9:00) �άO �W�Z�p�� > 9:00 (ps.�p�J�ɼƬ� : ��쪺�ɼ�)
		// 2 = >��(9:00)�Τj��(10:00)�����p
		// �ĤT��:���h = �U�Z�p�ɤp��18 # ���h�S�����
		// 3 => ps.�p�J�ɼƬ����`�ɼ�
		// �ĥ|��:���` = �W�z�����X (ps.�p�J�ɼƬ� :���`���ɼ�)

		if (((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				&& LocalDateTime.now().getHour() < 18)
				|| (workSystem.getWorkTime().getHour() > 9 && LocalDateTime.now().getHour() < 18)) {
			attendanceStatusStr = "���+���h";
			// ���� EX: 7 : 55 ���d�W�Z 8:00 ���d�U�Z �A�o�X�����ɼƫo�O 1 �ҥH �W�L x : 30 ���� x+1
			if (workSystem.getWorkTime().getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}
			// �W�z���p�o�ͳQ-1�ɥi��|�C��0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem.setOffWorkTime(finalDateTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);

		} else if ((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				|| workSystem.getWorkTime().getHour() > 9) {
			attendanceStatusStr = "���";
			// ���� EX: 7 : 55 ���d�W�Z 8:00 ���d�U�Z �A�o�X�����ɼƫo�O 1 �ҥH �W�L x : 30 ���� x+1
			if (workSystem.getWorkTime().getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}
			// �W�z���p�o�ͳQ-1�ɥi��|�C��0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem.setOffWorkTime(finalDateTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);
		} else if (LocalDateTime.now().getHour() < 18) {
			attendanceStatusStr = "���h";
			// �H���X�{ 8:00���d 9:00���d�U�Z
			if (countOffWorkHours <= 0) {
				countOffWorkHours = 0;
			}
			workSystem.setOffWorkTime(finalDateTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		} else {
			attendanceStatusStr = "���`";
			workSystem.setOffWorkTime(finalDateTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		}
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, "�U�Z���d���\");

	}

	/*------------------------------------------------(���u)�j�M���d���*/
	@Override
	public WorkSystemRes searchWorkInfoForStaff(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();

		// �����u�s��true
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());

		// ���}�l���true
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());

		// ���������true
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());

		// ����ӬO�r�� �A �ҥH�n���W��F (���i�H���ΤF)
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";

		// ���u�u��j����ۤv����ơA�G�o��n�P�_
		if (!checkEmployeeCode) {
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

		// �}�l�B�����ɶ�������J (�P�_�O�_�ŦX�r�꥿�W��F)
		if (checkSearchStartDate && checkSearchEndDate) {
			if (!req.getSearchStartDate().matches(checkDateString)
					|| !req.getSearchEndDate().matches(checkDateString)) {
				return new WorkSystemRes("����榡���~ �п�J(yyyy-mm-dd)");
			}

			// ���U���N��i�H�����F
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);

			// ��87
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

		// �W�����S�ױ��N�����u�s���B�}�l����A�S��������� (�T�u�n�P�_�}�l����O�_�ŦX���W��F)
		if (!req.getSearchStartDate().matches(checkDateString)) {
			return new WorkSystemRes("����榡���~ �п�J(yyyy-mm-dd��)");
		}

		// �u�n�N�}�l������Y�iLocalDate
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);

		// �����ɶ��Τ���
		LocalDate endDate = LocalDate.now();
		if (endDate.isBefore(startDate)) {
			return new WorkSystemRes("�}�l�ɶ����i�j�󤵤Ѯɶ�");
		}

		// �]��worktime���榡�OLocalDateTime �ҥH�n�NLocalDate�૬��LocalDateTime (�����Ʈɶ��ܦ� 00:00:00)
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atStartOfDay();

		List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(
				req.getEmployeeCode(), startDateTime, endDateTime);
		if (workInfoList.isEmpty()) {
			return new WorkSystemRes("�d�L���");
		}
		res.setWorkInfoList(workInfoList);
		return res;
	}

	/*------------------------------------------------(�D��)�j�M���d���*/
	@Override
	public WorkSystemRes searchWorkInfoForManager(WorkSystemReq req) {
		// �o�ӥ\��O���D�ޥΪ��A�D�ޥu��ݨ�ۦP�������U���u����ơA�ҥH�ڻݭn�D�޽s��
		if (!StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�п�J�z���D�޽s��");
		}

		Optional<EmployeeInfo> employeeInfoManagerOp = employeeInfoDao.findById(req.getManagerEmployeeCode());
		if (!employeeInfoManagerOp.isPresent()) {
			return new WorkSystemRes("���ˬd�z���s��");
		}

		// ����D�޽s������
		EmployeeInfo employeeManagerInfo = employeeInfoManagerOp.get();

		// ���L�o�᪺�F��
		List<WorkSystem> workInfoAllList = new ArrayList<>();

		// �����u�s��true
		boolean checkEmployeeCode = StringUtils.hasText(req.getEmployeeCode());
		// ���}�l���true
		boolean checkSearchStartDate = StringUtils.hasText(req.getSearchStartDate());
		// ���������true
		boolean checkSearchEndDate = StringUtils.hasText(req.getSearchEndDate());

		// ����Ӧr��A�ҥH�n���W��F
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";

		// �T�̳��S��J �^�ǩҦ���T���D�ެ� (�n�L�o)
		if ((!checkEmployeeCode && !checkSearchStartDate && !checkSearchEndDate)) {
			List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();

			for (var item : workInfoList) {
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());

				// �]���h�����䦳�R���L�ۤv����ơA�ҥH�ڳo��n�P�_�p�G�b�L����½����ӦW���u�n�q�Y�j��
				if (!employeeStaffOp.isPresent()) {
					continue;
				}
				// �W���S�q�Y�N����
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

				// �P�_�O���O�@�˪�����
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
					workInfoAllList.add(item);

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
				return new WorkSystemRes("����榡���~ �п�J(yyyy-mm-dd)");
			}
		}

		// ���}�l������P�_�A���W��F���b
		if (checkSearchStartDate) {
			if (!req.getSearchStartDate().matches(checkDateString)) {
				return new WorkSystemRes("����榡���~ �п�J(yyyy-mm-dd)");
			}
		}

		// �Ө�o�̻����q�L�Ҧ����b
		// �P�_����J���u�s���A���S����J�}�l���
		if (checkEmployeeCode && !checkSearchStartDate) {
			Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeStaffOp.isPresent()) {
				return new WorkSystemRes("�䤣��ӭ��u");
			}

			// ������
			EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

			// �]������J���u�s���A�ҥH�b���olist�e�����b�Y�i
			if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
				return new WorkSystemRes("�A�P�ӦW���u���P����");

			}

			List<WorkSystem> workInfoListByEmployeeCode = workSystemDao
					.findByEmployeeCodeOrderByWorkTimeDesc(req.getEmployeeCode());
			if (workInfoListByEmployeeCode.isEmpty()) {
				return new WorkSystemRes("�d�L���");
			}

			return new WorkSystemRes(workInfoListByEmployeeCode, " ���� : " + employeeManagerInfo.getSection());
		}

		// �T�̳�����J ���u�s���B�}�l�B�������
		if (checkEmployeeCode && checkSearchStartDate && checkSearchEndDate) {
			Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeStaffOp.isPresent()) {
				return new WorkSystemRes("�䤣��ӭ��u");
			}

			// ������
			EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

			// �]������J���u�s���A�ҥH�b���olist�e�����b�Y�i
			if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
				return new WorkSystemRes("�A�P�ӦW���u���P����");

			}

			// �Ө�o�N�����]���ŦX���W��F
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);

			// ��87
			if (endDate.isBefore(startDate)) {
				return new WorkSystemRes("�����ɶ����p��}�l�ɶ�");
			}

			// �]��entity�OLocalDateTime �ҥH�ڥ��নLocalDate �A��LocalDateTime �������ܦ�00:00:00
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atStartOfDay();
			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(req.getEmployeeCode(), startDateTime,
							endDateTime);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				return new WorkSystemRes("�d�L���");
			}
			return new WorkSystemRes(workInfoListByEmployeeCodeAndDate, " ���� : " + employeeManagerInfo.getSection());

		}
		// �P�_�u�����u�s���B�}�l���
		if (checkEmployeeCode && checkSearchStartDate) {
			Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getEmployeeCode());
			if (!employeeStaffOp.isPresent()) {
				return new WorkSystemRes("�䤣��ӭ��u");
			}

			EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

			// �]������J���u�s���A�ҥH�b���olist�e�����b�Y�i
			if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
				return new WorkSystemRes("�A�P�ӦW���u���P����");

			}

			// �u�n��}�l����Y�i
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			if (LocalDate.now().isBefore(startDate)) {
				return new WorkSystemRes("�}�l�ɶ����i�j�󤵤�");
			}

			// �]��entity�OLocalDateTime �ҥH�ڥ��নLocalDate �A��LocalDateTime �������ܦ�00:00:00
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDate endDateNow = LocalDate.now();
			LocalDateTime endDateTimeNow = endDateNow.atStartOfDay();

			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(req.getEmployeeCode(), startDateTime,
							endDateTimeNow);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				return new WorkSystemRes("�d�L���");
			}
			return new WorkSystemRes(workInfoListByEmployeeCodeAndDate, " ���� : " + employeeManagerInfo.getSection());
		}

		// �W�����S�ױ��A�N��@�w�S�����u�s�� (�j�M�ɶ��϶����Ҧ����u) (�n�L�o)
		if (checkSearchStartDate && checkSearchEndDate) {

			// �N�}�l�B��������૬
			LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
			LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);

			// �]��entity�OLocalDateTime �ҥH�ڥ��নLocalDate �A��LocalDateTime �������ܦ�00:00:00
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atStartOfDay();

			// ��87
			if (endDate.isBefore(startDate)) {
				return new WorkSystemRes("�����ɶ����p��}�l�ɶ�");
			}
			List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
					.findByWorkTimeBetweenOrderByWorkTimeDesc(startDateTime, endDateTime);
			if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
				return new WorkSystemRes("�d�L���");
			}
			for (var item : workInfoListByEmployeeCodeAndDate) {
				Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());
				// �h�����ط|�R�����u��T�A�ҥH�n�P�_�䤣����u�ɡA�q�Y�j��
				if (!employeeStaffOp.isPresent()) {
					continue;
				}

				// �S�q�Y�N����
				EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

				// �P�_�O�_�P�@�ӳ���
				if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
					workInfoAllList.add(item);

				}
			}
			if (workInfoAllList.isEmpty()) {
				return new WorkSystemRes("�ӳ����S����ơA�нT�{�ӳ����O�_���ӭ��u");
			}
			return new WorkSystemRes(workInfoAllList, " ���� : " + employeeManagerInfo.getSection());
		}

		// �W�����S�ױ��N��S�����u�s���B������� ps.�u���}�l���
		// �u�n��}�l����Y�i (�n�L�o)
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		if (LocalDate.now().isBefore(startDate)) {
			return new WorkSystemRes("���Ѯɶ����i�p��}�l�ɶ�");
		}
		// �]��entity�OLocalDateTime �ҥH�ڥ��নLocalDate �A��LocalDateTime �������ܦ�00:00:00
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDate endDateNow = LocalDate.now();
		LocalDateTime endDateTimeNow = endDateNow.atStartOfDay();
		List<WorkSystem> workInfoListByEmployeeCodeAndDate = workSystemDao
				.findByWorkTimeBetweenOrderByWorkTimeDesc(startDateTime, endDateTimeNow);
		if (workInfoListByEmployeeCodeAndDate.isEmpty()) {
			return new WorkSystemRes("�d�L���");
		}
		for (var item : workInfoListByEmployeeCodeAndDate) {
			Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(item.getEmployeeCode());

			// �h�����ط|�R�����u��T�A�ҥH�n�P�_�䤣����u�ɡA�q�Y�j��
			if (!employeeStaffOp.isPresent()) {
				continue;
			}
			// �S�q�Y�N����
			EmployeeInfo employeeStaffInfo = employeeStaffOp.get();

			// �P�_�O�_�P�@�ӳ���
			if (employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
				workInfoAllList.add(item);

			}
		}
		if (workInfoAllList.isEmpty()) {
			return new WorkSystemRes("�ӳ����S����ơA�нT�{�ӳ����O�_���ӭ��u");
		}
		return new WorkSystemRes(workInfoAllList, " ���� : " + employeeManagerInfo.getSection());

	}

	/*------------------------------------------------(�j����)�R���ɶ��϶������d���*/
	@Override
	public WorkSystemRes deleteWorkInfoByDateBetween(WorkSystemReq req) {
		// �S����J����ɡA�����L�ݩҦ����
		if (!StringUtils.hasText(req.getSearchStartDate()) || !StringUtils.hasText(req.getSearchEndDate())) {
			List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();
			return new WorkSystemRes(workInfoList, "�R���e���ݬݧa");
		}

		// �W���S�ױ��N��@�w�����
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		// �P�_�O�_�ŦX���W��F
		if (!req.getSearchStartDate().matches(checkDateString) || !req.getSearchEndDate().matches(checkDateString)) {
			return new WorkSystemRes("����榡���~ �п�J(yyyy-mm-dd)");
		}
		// ������
		LocalDate startDate = LocalDate.parse(req.getSearchStartDate(), formatDate);
		LocalDate endDate = LocalDate.parse(req.getSearchEndDate(), formatDate);
		if (endDate.isBefore(startDate)) {
			return new WorkSystemRes("�}�l�ɶ����i�j�󵲧��ɶ�");
		}
		// �]��entity�OLocalDateTime �ҥH�ڥ��নLocalDate �A��LocalDateTime �������ܦ�00:00:00
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atStartOfDay();
		workSystemDao.deleteByWorkTimeBetween(startDateTime, endDateTime);
		List<WorkSystem> workInfoList = workSystemDao.findAllByOrderByWorkTimeDesc();
		return new WorkSystemRes(workInfoList, "�R�����\");
	}

	/*------------------------------------------------(�D��)�s�W�m¾�欰*/
	@Override
	public WorkSystemRes creatAbsenteeismForManager(WorkSystemReq req) {
		if (!StringUtils.hasText(req.getEmployeeCode()) || !StringUtils.hasText(req.getAbsenteeismDate())
				|| !StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�ѼƭȤ��ର��");
		}

		// �]���n�P�_���u�P�D�ެO�_�P�@����
		Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getEmployeeCode());
		Optional<EmployeeInfo> employeeManagerOp = employeeInfoDao.findById(req.getManagerEmployeeCode());
		if (!employeeStaffOp.isPresent() || !employeeManagerOp.isPresent()) {
			return new WorkSystemRes("�п�J���T�s���i�ण�s�b�ӦW���u");
		}

		// ��ӳ�����
		EmployeeInfo employeeStaffInfo = employeeStaffOp.get();
		EmployeeInfo employeeManagerInfo = employeeManagerOp.get();

		// �P�_�O�_�P�ӳ���
		if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
			return new WorkSystemRes("�z�P�ӭ��u���P����");
		}

		// �P�_�n�n�O�m¾�̻P�Q�O�m¾�̪�����
		if (employeeManagerInfo.getLevel() < employeeStaffInfo.getLevel()) {
			return new WorkSystemRes("�z���v������");
		}

		// ����x���W��F
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		String checkDateString = "^[1-9]\\d{3}-(0[1-9]|1[0-2]|[1-9])-([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-1])";
		if (!req.getAbsenteeismDate().matches(checkDateString)) {
			return new WorkSystemRes("����榡���~ �п�J(yyyy-mm-dd)");
		}

		// �q�L���W��F ����
		LocalDate absenteeismDate = LocalDate.parse(req.getAbsenteeismDate(), formatDate);

		// �]���������D���H�N�O�m¾�A�n�T�{���u���ѨS�Ӥ~��O
		if (absenteeismDate.isAfter(LocalDate.now())) {
			return new WorkSystemRes(req.getAbsenteeismDate() + "�L��~��n���m¾");
		}

		// �n���X�Ԭ����A����D�ަb���u���Ӫ����p�U�ðO�m¾
		List<WorkSystem> staffInfo = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		for (var item : staffInfo) {

			// �N�W�Z�ɶ��নLocalDate �~�i���O�_�@��
			LocalDate localDate = item.getWorkTime().toLocalDate();
			if (localDate.equals(absenteeismDate)) {
				return new WorkSystemRes("�o����u�o�Ѧ��W�Z�ΥH�O�m¾�L");
			}
		}

		// �W�����S�ױ��N��u�x�m¾�F
		LocalDateTime absenteeismDateTime = absenteeismDate.atStartOfDay();
		WorkSystem workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), absenteeismDateTime,
				absenteeismDateTime, "�m¾", 0);
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, "�s�W�m¾���\");

	}

	/*------------------------------------------------(�D��)�R���m¾�欰*/
	@Override
	public WorkSystemRes deleteAbsenteeismForManager(WorkSystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());
		if (!StringUtils.hasText(req.getUuid())) {
			return new WorkSystemRes("�ѼƭȤ����");
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
			return new WorkSystemRes("�R���m¾���\");
		}
		return new WorkSystemRes("�o����u�o�ѨS���m¾");
	}

	/*------------------------------------------------(���u)�L�X���d��T���U�Z���d��*/
	@Override
	public WorkSystemRes getWorkInfoListToday(WorkSystemReq req) {
		WorkSystemRes res = new WorkSystemRes();
		if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new WorkSystemRes("�ѼƭȤ��ର��");
		}

		// �������ܦ�00:00:00
		LocalDateTime nowDateTime = LocalDate.now().atStartOfDay();

		// �]���j�M���O�j�󵥩�"����"�ҥH�û��u���@����� ���X�N�~���� 2�~2�~^^
		List<WorkSystem> workInfoList = workSystemDao
				.findByEmployeeCodeAndWorkTimeGreaterThanEqual(req.getEmployeeCode(), nowDateTime);
		if (workInfoList.isEmpty()) {
			return new WorkSystemRes("�A�O���O�W�Z�S���d!?");
		}
		res.setWorkInfoList(workInfoList);
		return res;
	}

	/*------------------------------------------------(�D��)�L�X�m¾��T���R���m¾�欰��*/
	@Override
	public WorkSystemRes getWorkInfoListAbsenteeism(WorkSystemReq req) {
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-M-d");
		if (!StringUtils.hasText(req.getAbsenteeismDate()) || !StringUtils.hasText(req.getEmployeeCode())
				|| !StringUtils.hasText(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�ѼƭȩΤ�����ର��");
		}
		// ����ӬO�r�� ���নDate
		LocalDate absenteeismDate = LocalDate.parse(req.getAbsenteeismDate(), formatDate);
		// �����o�D�޻P���u���s��
		Optional<EmployeeInfo> employeeStaffOp = employeeInfoDao.findById(req.getManagerEmployeeCode());
		Optional<EmployeeInfo> employeeManagerOp = employeeInfoDao.findById(req.getEmployeeCode());
		if (!employeeStaffOp.isPresent() || !employeeManagerOp.isPresent()) {
			return new WorkSystemRes("�п�J���T�s���i��S���ӭ��u");
		}

		// ������
		EmployeeInfo employeeStaffInfo = employeeStaffOp.get();
		EmployeeInfo employeeManagerInfo = employeeManagerOp.get();
		if (!employeeStaffInfo.getSection().equals(employeeManagerInfo.getSection())) {
			return new WorkSystemRes("�z�P�ӭ��u���P����");
		}

		// �P�_�n�n�O�m¾�̻P�Q�O�m¾�̪�����
		if (employeeManagerInfo.getLevel() < employeeStaffInfo.getLevel()) {
			return new WorkSystemRes("�z���v������");
		}
		
		// ���ƬO00:00:00
		LocalDateTime absenteeismDateTime = absenteeismDate.atStartOfDay();
		// �]���s�W�m¾�� ���ƬO00:00:00 �ҥH�u�n������T�@�w�i�H���
		List<WorkSystem> workInfoList = workSystemDao.findByEmployeeCodeAndWorkTime(req.getEmployeeCode(),
				absenteeismDateTime);
		if (workInfoList.isEmpty()) {
			return new WorkSystemRes("�ӭ��u�o�ѥi��S���m¾");
		}
		return new WorkSystemRes(workInfoList, " ���� : " + employeeManagerInfo.getSection());
	}

	/*------------------------------------------------(�D��)���u�S���d�U�Z��D�޸ɥ��U�Z�d*/
	@Override
	public WorkSystemRes updeateWorkOffTimeForManager(WorkSystemReq req) {
		UUID uuid = UUID.fromString(req.getUuid());

		// �e�ݱ��i�Ӯ榡 YYYY-MM-DDTHH:MM
		DateTimeFormatter foemat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		if (!StringUtils.hasText(req.getUuid()) || !StringUtils.hasText(req.getOffWorkTime())) {
			return new WorkSystemRes("�ѼƭȤ����");
		}

		// �NT�������Ů� �H�K�ŦX�榡
		String reqDate = req.getOffWorkTime().replace('T', ' ');
		// �নLocalDateTime
		LocalDateTime offWorkTime = LocalDateTime.parse(reqDate, foemat);
		if (offWorkTime.getYear() != LocalDateTime.now().getYear()
				|| offWorkTime.getMonthValue() != LocalDateTime.now().getMonthValue()) {
			return new WorkSystemRes("�W�L" + offWorkTime.getMonthValue() + "��A����ɥ��d");
		}

		// �ШD�ýXuuid �A �]���W�Z�ɥ��L�d�F�A�T�|����(�e����ܮɡA�ǥ�"���s"���o�ýX)
		Optional<WorkSystem> workSystemOp = workSystemDao.findById(uuid);
		// ���o�̪����b�Sԣ�N�q�A�]���e�ݷ|��ܪ���� �A��Ʈw�@�w����uuid
		if (!workSystemOp.isPresent()) {
			return new WorkSystemRes("�Ѽƭȿ��~");
		}
		WorkSystem workSystem = workSystemOp.get();

		// �n�P�_���d�W�Z�ɶ��O���O�b�U�Z���e �٦� ���d�W�Z�O���O��L�ɥ��d���ɶ��O���O"�P�@��"
		if ((offWorkTime.isBefore(workSystem.getWorkTime()))
				|| offWorkTime.getYear() != workSystem.getWorkTime().getYear()
				|| offWorkTime.getMonthValue() != workSystem.getWorkTime().getMonthValue()
				|| offWorkTime.getDayOfMonth() != workSystem.getWorkTime().getDayOfMonth()) {
			return new WorkSystemRes("�п�J���T����ɶ�");
		}

		if (workSystem.getOffWorkTime() != null) {
			return new WorkSystemRes("�o����u�����d");
		}
		// ���`�W�U�Z���ɼ�(�{�b�ɶ�-�W�Z�ɶ�)
		int countOffWorkHours = offWorkTime.getHour() - 9;

		// ���Φ��h�ɪ��p��ɼ� (�U�Z-�L�Ӫ��ɶ�)
		int countWorkLateOrLeaveTime = offWorkTime.getHour() - workSystem.getWorkTime().getHour();

		// �r���s���p
		String attendanceStatusStr;

		// �Ĥ@��:���+���h = (�W�Z�ɶ� >= 9:00 & �W�Z���� >= 1 & �U�Z�p�� < 18) ||(�W�Z�ɶ� > 9:00 & �U�Z�ɶ� <
		// 18)
		// 1 = > (ps.�p�J�ɼƬ� : ��쪺�ɼ� ps.18=�X�I�U�Z)
		// �ĤG��:��� =�W�Z�p�� >= 9:00 & �W�Z���� >= 1 (���F��9:00) �άO �W�Z�p�� > 9:00 (ps.�p�J�ɼƬ� : ��쪺�ɼ�)
		// 2 = >��(9:00)�Τj��(10:00)�����p
		// �ĤT��:���h = �U�Z�p�ɤp��18 # ���h�S�����
		// 3 => ps.�p�J�ɼƬ����`�ɼ�
		// �ĥ|��:���` = �W�z�����X (ps.�p�J�ɼƬ� :���`���ɼ�)

		if (((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				&& offWorkTime.getHour() < 18)
				|| (workSystem.getWorkTime().getHour() > 9 && offWorkTime.getHour() < 18)) {
			attendanceStatusStr = "���+���h";

			// ���� EX: 7 : 55 ���d�W�Z 8:00 ���d�U�Z �A�o�X�����ɼƫo�O 1 �ҥH �W�L x : 30 ���� x+1
			if (workSystem.getWorkTime().getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}

			// �W�z���p�o�ͳQ-1�ɥi��|�C��0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem.setOffWorkTime(offWorkTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);

		} else if ((workSystem.getWorkTime().getHour() >= 9 && workSystem.getWorkTime().getMinute() >= 1)
				|| workSystem.getWorkTime().getHour() > 9) {
			attendanceStatusStr = "���";

			// ���� EX: 7 : 55 ���d�W�Z 8:00 ���d�U�Z �A�o�X�����ɼƫo�O 1 �ҥH �W�L x : 30 ���� x+1
			if (workSystem.getWorkTime().getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}

			// �W�z���p�o�ͳQ-1�ɥi��|�C��0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem.setOffWorkTime(offWorkTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countWorkLateOrLeaveTime);
		} else if (countOffWorkHours < 8) {
			attendanceStatusStr = "���h";

			// �H���X�{ 8:00���d 9:00���d�U�Z
			if (countOffWorkHours <= 0) {
				countOffWorkHours = 0;
			}

			workSystem.setOffWorkTime(offWorkTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		} else {
			attendanceStatusStr = "���`";
			workSystem.setOffWorkTime(offWorkTime);
			workSystem.setAttendanceStatus(attendanceStatusStr);
			workSystem.setAttendanceHours(countOffWorkHours);
		}
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, "�U���аO�o���d");
	}

	/*------------------------------------------------(�D��)���u�S���d�U�Z��D�޸ɥ��d*/
	@Override
	public WorkSystemRes forgotToPunchCard(WorkSystemReq req) {
		// �e�ݱ��i�Ӯ榡 YYYY-MM-DDTHH:MM
		DateTimeFormatter foematDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		if (!StringUtils.hasText(req.getEmployeeCode()) || !StringUtils.hasText(req.getManagerEmployeeCode())
				|| !StringUtils.hasText(req.getPunchWorkTime()) || !StringUtils.hasText(req.getOffWorkTime())) {
			return new WorkSystemRes("�ѼƭȤ��ର��");
		}

		String workDateTimeString = req.getPunchWorkTime().replace('T', ' ');
		String offWorkDateTimeString = req.getOffWorkTime().replace('T', ' ');

		// �নLocalDateTime
		LocalDateTime workDateTime = LocalDateTime.parse(workDateTimeString, foematDateTime);
		LocalDateTime offWorkDateTime = LocalDateTime.parse(offWorkDateTimeString, foematDateTime);
		if (workDateTime.getYear() != LocalDateTime.now().getYear()
				|| workDateTime.getMonthValue() != LocalDateTime.now().getMonthValue()) {
			return new WorkSystemRes("�W�L" + workDateTime.getMonthValue() + "��A����ɥ��d");
		}

		Optional<EmployeeInfo> managerEmployeeInfoOp = employeeInfoDao.findById(req.getManagerEmployeeCode());
		Optional<EmployeeInfo> employeeInfoOp = employeeInfoDao.findById(req.getEmployeeCode());

		if (!employeeInfoOp.isPresent() || !managerEmployeeInfoOp.isPresent()) {
			return new WorkSystemRes("���˹���u�s��");
		}

		EmployeeInfo employeeInfo = employeeInfoOp.get();
		EmployeeInfo managerEmployeeInfo = managerEmployeeInfoOp.get();

		if (!employeeInfo.getSection().equals(managerEmployeeInfo.getSection())) {
			return new WorkSystemRes("�A�̤��P����");
		}

		// ����ӬO�r�� ���নDate
		LocalDate workDate = workDateTime.toLocalDate();

		if ((offWorkDateTime.isBefore(workDateTime)) || offWorkDateTime.getYear() != workDateTime.getYear()
				|| offWorkDateTime.getMonthValue() != workDateTime.getMonthValue()
				|| offWorkDateTime.getDayOfMonth() != workDateTime.getDayOfMonth()) {
			return new WorkSystemRes("�п�J���T����ɶ�");
		}

		// �ǥѭ��u�s�������(�P�_���L���ƥ��d)
		List<WorkSystem> staffInfo = workSystemDao.findByEmployeeCode(req.getEmployeeCode());
		// �~���B����B�� ���@�ˮ� �N���L�d�F
		for (WorkSystem item : staffInfo) {
			LocalDate localDate = item.getWorkTime().toLocalDate();
			if (localDate.equals(workDate)) {
				return new WorkSystemRes("�o����u�b" + workDate + "�����d�A���ݭn��");
			}
		}

		// ���`�W�U�Z���ɼ�(�{�b�ɶ�-�W�Z�ɶ�)
		int countOffWorkHours = offWorkDateTime.getHour() - 9;

		// ���Φ��h�ɪ��p��ɼ� (�U�Z-�L�Ӫ��ɶ�)
		int countWorkLateOrLeaveTime = offWorkDateTime.getHour() - workDateTime.getHour();

		// �r���s���p
		String attendanceStatusStr;
		WorkSystem workSystem = new WorkSystem();

		// �Ĥ@��:���+���h = (�W�Z�ɶ� >= 9:00 & �W�Z���� >= 1 & �U�Z�p�� < 18) ||(�W�Z�ɶ� > 9:00 & �U�Z�ɶ� <
		// 18)
		// 1 = > (ps.�p�J�ɼƬ� : ��쪺�ɼ� ps.18=�X�I�U�Z)
		// �ĤG��:��� =�W�Z�p�� >= 9:00 & �W�Z���� >= 1 (���F��9:00) �άO �W�Z�p�� > 9:00 (ps.�p�J�ɼƬ� : ��쪺�ɼ�)
		// 2 = >��(9:00)�Τj��(10:00)�����p
		// �ĤT��:���h = �U�Z�p�ɤp��18 # ���h�S�����
		// 3 => ps.�p�J�ɼƬ����`�ɼ�
		// �ĥ|��:���` = �W�z�����X (ps.�p�J�ɼƬ� :���`���ɼ�)

		if (((workDateTime.getHour() >= 9 && workDateTime.getMinute() >= 1) && offWorkDateTime.getHour() < 18)
				|| (workDateTime.getHour() > 9 && offWorkDateTime.getHour() < 18)) {
			attendanceStatusStr = "���+���h";

			// ���� EX: 7 : 55 ���d�W�Z 8:00 ���d�U�Z �A�o�X�����ɼƫo�O 1 �ҥH �W�L x : 30 ���� x+1
			if (workDateTime.getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}

			// �W�z���p�o�ͳQ-1�ɥi��|�C��0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), workDateTime, offWorkDateTime,
					attendanceStatusStr, countWorkLateOrLeaveTime);

		} else if ((workDateTime.getHour() >= 9 && workDateTime.getMinute() >= 1) || workDateTime.getHour() > 9) {
			attendanceStatusStr = "���";

			// ���� EX: 7 : 55 ���d�W�Z 8:00 ���d�U�Z �A�o�X�����ɼƫo�O 1 �ҥH �W�L x : 30 ���� x+1
			if (workDateTime.getMinute() > 30) {
				countWorkLateOrLeaveTime = countWorkLateOrLeaveTime - 1;
			}

			// �W�z���p�o�ͳQ-1�ɥi��|�C��0
			if (countWorkLateOrLeaveTime <= 0) {
				countWorkLateOrLeaveTime = 0;
			}
			workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), workDateTime, offWorkDateTime,
					attendanceStatusStr, countWorkLateOrLeaveTime);
		} else if (countOffWorkHours < 8) {
			attendanceStatusStr = "���h";

			// �H���X�{ 8:00���d 9:00���d�U�Z
			if (countOffWorkHours <= 0) {
				countOffWorkHours = 0;
			}
			workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), workDateTime, offWorkDateTime,
					attendanceStatusStr, countOffWorkHours);
		} else {
			attendanceStatusStr = "���`";
			workSystem = new WorkSystem(UUID.randomUUID(), req.getEmployeeCode(), workDateTime, offWorkDateTime,
					attendanceStatusStr, countOffWorkHours);
		}
		workSystemDao.save(workSystem);
		return new WorkSystemRes(workSystem, "�P�¥D�ޡA�U���аO�o���d");

	}

}
