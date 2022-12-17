package com.example.new_hr_system.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.new_hr_system.entity.AbsenceSystem;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbsenceSystemRes {

	private UUID uuid;
	
	private String employeeCode;

	private String name;

	private String section;

	private String absenceReason;

	private LocalDate absenceDate;

	private LocalDateTime workDateTime;

	private boolean yesOrNo;

	private String message;

	private AbsenceSystem absence;

	public AbsenceSystemRes() {

	}

	public AbsenceSystemRes(AbsenceSystem absence, String message) {
		this.absence = absence;
		this.message = message;
	}

	public AbsenceSystemRes(String message) {
		this.message = message;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getAbsenceReason() {
		return absenceReason;
	}

	public void setAbsenceReason(String absenceReason) {
		this.absenceReason = absenceReason;
	}

	public LocalDateTime getWorkDateTime() {
		return workDateTime;
	}

	public void setWorkDateTime(LocalDateTime workDateTime) {
		this.workDateTime = workDateTime;
	}

	public boolean isYesOrNo() {
		return yesOrNo;
	}

	public void setYesOrNo(boolean yesOrNo) {
		this.yesOrNo = yesOrNo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public AbsenceSystem getAbsence() {
		return absence;
	}

	public void setAbsence(AbsenceSystem absence) {
		this.absence = absence;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public LocalDate getAbsenceDate() {
		return absenceDate;
	}

	public void setAbsenceDate(LocalDate absenceDate) {
		this.absenceDate = absenceDate;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	

}
