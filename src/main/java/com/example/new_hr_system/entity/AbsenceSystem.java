package com.example.new_hr_system.entity;

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
	
	@Column(name = "yes_or_no")
	private boolean yesOrNo;
	
	public AbsenceSystem() {
		
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

	public boolean isYesOrNo() {
		return yesOrNo;
	}

	public void setYesOrNo(boolean yesOrNo) {
		this.yesOrNo = yesOrNo;
	}
	
}