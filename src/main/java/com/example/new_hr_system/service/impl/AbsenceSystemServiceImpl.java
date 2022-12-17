package com.example.new_hr_system.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.new_hr_system.constants.AbsenceSystemRtnCode;
import com.example.new_hr_system.entity.AbsenceSystem;
import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.entity.WorkSystem;
import com.example.new_hr_system.respository.AbsenceSystemDao;
import com.example.new_hr_system.respository.EmployeeInfoDao;
import com.example.new_hr_system.respository.WorkSystemDao;
import com.example.new_hr_system.service.ifs.AbsenceSystemService;
import com.example.new_hr_system.vo.AbsenceSystemReq;
import com.example.new_hr_system.vo.AbsenceSystemRes;
import com.example.new_hr_system.vo.AbsenceSystemResList;

@Service
public class AbsenceSystemServiceImpl implements AbsenceSystemService {

	@Autowired
	private AbsenceSystemDao absenceSystemDao;

	@Autowired
	private EmployeeInfoDao employeeInfoDao;

	@Autowired
	private WorkSystemDao workSystemDao;

	@Autowired
	private JavaMailSender mailSender;

	// 創建請假表單,並寄送email提醒主管批准
	@Override
	public AbsenceSystemRes addAbsence(AbsenceSystemReq req) {

		if (!StringUtils.hasText(req.getAbsenceReason())) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.ABSENCE_REASON_REQOIRED.getMessage());
		} else if (!StringUtils.hasText(req.getEmployeeCode())) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
		}

		EmployeeInfo employee = employeeInfoDao.findById(req.getEmployeeCode()).get();

		if (!employee.getEmployeeCode().equalsIgnoreCase(req.getEmployeeCode())) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
		}

		AbsenceSystem absence = new AbsenceSystem(UUID.randomUUID(), req.getEmployeeCode(), req.getAbsenceReason(),
				req.getAbsenceDate());

		absenceSystemDao.save(absence);

		EmployeeInfo employeeInfo = employeeInfoDao.findById(req.getEmployeeCode()).get();

		SimpleMailMessage gmail = new SimpleMailMessage();

		gmail.setFrom("kennymax22581997@gmail.com");
		gmail.setTo(req.getEmail());
		gmail.setSubject("主旨：柬埔寨旅遊團員工假單批准");
		gmail.setText("員工: " + employeeInfo.getEmployeeCode() + " " + employeeInfo.getName() + " 有假單需要批准");
		mailSender.send(gmail);

		return new AbsenceSystemRes(AbsenceSystemRtnCode.SUCCESSFUL.getMessage());
	}

	// 刪除假單
	@Override
	public AbsenceSystemRes deleteAbsence(AbsenceSystemReq req) {

		UUID uuid = UUID.fromString(req.getUuid());
		Optional<AbsenceSystem> absenceOp = absenceSystemDao.findById(uuid);

		if (!absenceOp.isPresent()) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.ABSENCE_EMPTY.getMessage());
		}

		absenceSystemDao.deleteById(absenceOp.get().getUuid());

		return new AbsenceSystemRes(AbsenceSystemRtnCode.Delete_SUCCESSFUL.getMessage());
	}

	// 員工顯示自己的假單
	@Override
	public AbsenceSystemResList getAbsenceByEmployeeCode(HttpSession httpSession) {

		Object attValue = httpSession.getAttribute("employee_code");

		if (attValue != null) {
			String employeeCode = attValue.toString();
			List<AbsenceSystem> absenceList = absenceSystemDao.findAllByEmployeeCode(employeeCode);
			AbsenceSystemResList res = new AbsenceSystemResList();
			res.setAbsenceSystemList(absenceList);
			return res;
		}
		return new AbsenceSystemResList(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
	}

	// 員工輸入年月尋找當月假單
	@Override
	public AbsenceSystemResList getAbsenceByEmployeeCodeAndDate(AbsenceSystemReq req, HttpSession httpSession) {

		if (req.getYear() == null || req.getMonth() == null) {
			return new AbsenceSystemResList(AbsenceSystemRtnCode.DATE_EMPTY.getMessage());
		}
		Object attValue = httpSession.getAttribute("employee_code");

		if (attValue != null) {
			String employeeCode = attValue.toString();
			List<AbsenceSystem> absenceList = absenceSystemDao.findAllByEmployeeCode(employeeCode);
			List<AbsenceSystem> chosenList = new ArrayList<>();

			for (AbsenceSystem item : absenceList) {
				int itemYear = item.getAbsenceDate().getYear();
				int itemMonth = item.getAbsenceDate().getMonthValue();

				if (req.getYear() == itemYear && req.getMonth() == itemMonth) {
					chosenList.add(item);
				}
			}
			if (CollectionUtils.isEmpty(chosenList)) {
				return new AbsenceSystemResList(AbsenceSystemRtnCode.DATE_OF_ABSENCE_EMPTY.getMessage());
			}

			AbsenceSystemResList res = new AbsenceSystemResList();
			res.setAbsenceSystemList(chosenList);
			return res;
		}
		return new AbsenceSystemResList(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());

	}

	// 依照主管等級和所屬部門顯示假單
	@Override
	public AbsenceSystemResList getAbsenceBySectionAndLevel(HttpSession httpSession) {

		Object attValue = httpSession.getAttribute("employee_code");

		if (!StringUtils.hasText(attValue.toString())) {
			return new AbsenceSystemResList(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
		}
		// 批假單的人的資料
		EmployeeInfo manager = employeeInfoDao.findById(attValue.toString()).get();

		// 所有員工資料
		List<EmployeeInfo> employeeList = employeeInfoDao.findAll();

		// 所有假單
		List<AbsenceSystem> absenceList = absenceSystemDao.findAll();

		// 要顯示的假單
		List<AbsenceSystemRes> absenceResList = new ArrayList<>();

		for (AbsenceSystem absenceItem : absenceList) {

			for (EmployeeInfo employeeItem : employeeList) {

				if (absenceItem.getEmployeeCode().equalsIgnoreCase(employeeItem.getEmployeeCode())) {

					if (manager.getSection().equalsIgnoreCase(employeeItem.getSection())
							&& manager.getLevel() > employeeItem.getLevel() && absenceItem.getYesOrNo() == 0) {

						//多加uuid
						AbsenceSystemRes res = new AbsenceSystemRes();
						res.setUuid(absenceItem.getUuid());
						res.setEmployeeCode(employeeItem.getEmployeeCode());
						res.setName(employeeItem.getName());
						res.setSection(employeeItem.getSection());
						res.setAbsenceReason(absenceItem.getAbsenceReason());
						res.setAbsenceDate(absenceItem.getAbsenceDate());
						absenceResList.add(res);
					}
				}
			}
		}
		AbsenceSystemResList finalRes = new AbsenceSystemResList();
		finalRes.setAbsenceSystemResList(absenceResList);
		return finalRes;
	}

	// 主管批示假單,並寄送email給員工
	@Override
	public AbsenceSystemRes checkYesOrNo(AbsenceSystemReq req) {

		UUID uuid = UUID.fromString(req.getUuid());
		Optional<AbsenceSystem> absenceOp = absenceSystemDao.findById(uuid);

		AbsenceSystem absence = absenceOp.get();

		if (!absenceOp.isPresent()) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.ABSENCE_EMPTY.getMessage());
		}

		if (req.getYesOrNo() == 1 && absenceOp.isPresent()) {

			WorkSystem absenceEmployee = new WorkSystem();
			absenceEmployee.setUuid(UUID.randomUUID());
			absenceEmployee.setEmployeeCode(absenceOp.get().getEmployeeCode());
			absenceEmployee.setAttendanceStatus("請假");
			LocalDateTime localDateTime1 = absenceOp.get().getAbsenceDate().atStartOfDay();
			absenceEmployee.setWorkTime(localDateTime1);
			absenceEmployee.setOffWorkTime(localDateTime1);
			absenceEmployee.setAttendanceHours(0);

			workSystemDao.save(absenceEmployee);

			absenceOp.get().setYesOrNo(1);
			absenceSystemDao.save(absenceOp.get());

			EmployeeInfo employee = employeeInfoDao.findById(absenceOp.get().getEmployeeCode()).get();

			SimpleMailMessage gmail = new SimpleMailMessage();

			gmail.setFrom("kennymax22581997@gmail.com");
			gmail.setTo(employee.getEmployeeEmail());
			gmail.setSubject("主旨：柬埔寨旅遊團假單回覆");
			gmail.setText("您在 " + absence.getAbsenceDate() + " 的休假已批准");
			mailSender.send(gmail);

			AbsenceSystemRes res = new AbsenceSystemRes();
			res.setYesOrNo(true);
			res.setMessage(AbsenceSystemRtnCode.ABSENCE_ACCEPT.getMessage());
			return res;
		}
		EmployeeInfo employee = employeeInfoDao.findById(absence.getEmployeeCode()).get();

		absence.setYesOrNo(2);
		absenceSystemDao.save(absence);

		SimpleMailMessage gmail = new SimpleMailMessage();

		gmail.setFrom("kennymax22581997@gmail.com");
		gmail.setTo(employee.getEmployeeEmail());
		gmail.setSubject("主旨：柬埔寨旅遊團假單回覆");
		gmail.setText("您在 " + absence.getAbsenceDate() + " 的休假遭到拒絕");
		mailSender.send(gmail);
		return new AbsenceSystemRes(AbsenceSystemRtnCode.ABSENCE_REJECT.getMessage());
	}

	// 判斷員工階級(小於2不能點)
	@Override
	public boolean checkEmployeeLevel(HttpSession httpSession) {

		Object attValue = httpSession.getAttribute("employee_code");

		if (attValue != null) {
			String employeeCode = attValue.toString();
			EmployeeInfo employeeInfo = employeeInfoDao.findById(employeeCode).get();
			if (employeeInfo.getLevel() > 1) {
				return true;
			}
			return false;
		}

		return false;
	}

}
