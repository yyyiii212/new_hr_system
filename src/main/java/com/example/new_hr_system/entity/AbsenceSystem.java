package com.example.new_hr_system.entity;

import java.time.LocalDate;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "absence_system")
public class AbsenceSystem {

	@Id
	@Column(name = "uuid")
	@Type(type = "uuid-char")
	private UUID uuid;

	@Column(name = "employee_code")
	private String employeeCode;

	@Column(name = "absence_reason")
	private String absenceReason;

	@Column(name = "absence_date")
	private LocalDate absenceDate;

	@Column(name = "yes_or_no")
	private int yesOrNo;
	
	public AbsenceSystem() {

	}

	public AbsenceSystem(UUID uuid, String employeeCode, String absenceReason, LocalDate absenceDate) {
		this.uuid = uuid;
		this.employeeCode = employeeCode;
		this.absenceReason = absenceReason;
		this.absenceDate = absenceDate;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
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

	public LocalDate getAbsenceDate() {
		return absenceDate;
	}

	public void setAbsenceDate(LocalDate absenceDate) {
		this.absenceDate = absenceDate;
	}

	public int getYesOrNo() {
		return yesOrNo;
	}
	

	public void setYesOrNo(int yesOrNo) {
		this.yesOrNo = yesOrNo;
	}

}
