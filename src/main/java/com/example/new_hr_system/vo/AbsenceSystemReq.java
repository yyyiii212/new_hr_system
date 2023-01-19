package com.example.new_hr_system.vo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AbsenceSystemReq {
	
	private String uuid;
	
	private String employeeCode;
	
	private String absenceReason;
	
	private String section;
	
	@JsonFormat(shape =JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate absenceDate;
	
	@JsonFormat(shape =JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate absenceStartDate;
	
	@JsonFormat(shape =JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate absenceEndDate;
	
	private String email;
	
	private int yesOrNo;
	
	private Integer year;
	
	private Integer month;
	
	private String absenceStr;
	
	public AbsenceSystemReq() {
		
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
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

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public int getYesOrNo() {
		return yesOrNo;
	}

	public void setYesOrNo(int yesOrNo) {
		this.yesOrNo = yesOrNo;
	}

	public LocalDate getAbsenceDate() {
		return absenceDate;
	}

	public void setAbsenceDate(LocalDate absenceDate) {
		this.absenceDate = absenceDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public String getAbsenceStr() {
		return absenceStr;
	}

	public void setAbsenceStr(String absenceStr) {
		this.absenceStr = absenceStr;
	}

	public LocalDate getAbsenceStartDate() {
		return absenceStartDate;
	}

	public void setAbsenceStartDate(LocalDate absenceStartDate) {
		this.absenceStartDate = absenceStartDate;
	}

	public LocalDate getAbsenceEndDate() {
		return absenceEndDate;
	}

	public void setAbsenceEndDate(LocalDate absenceEndDate) {
		this.absenceEndDate = absenceEndDate;
	}
	

}
