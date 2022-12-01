package com.example.new_hr_system.vo;

import java.util.Date;

import javax.persistence.Column;

public class EmployeeInfoReq {

	private String employeeCode;

	private String name;

	private String id;

	private String employeeEmail;

	private String section;

	private Integer level;

	private Integer seniority;

	private String situation;

	public EmployeeInfoReq() {

	}

	public EmployeeInfoReq(String employeeCode, String name, String id, String employeeEmail, String section, int level,
			int seniority, String situation) {
		this.employeeCode = employeeCode;
		this.name = name;
		this.id = id;
		this.employeeEmail = employeeEmail;
		this.section = section;
		this.level = level;
		this.seniority = seniority;
		this.situation = situation;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmployeeEmail() {
		return employeeEmail;
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getSeniority() {
		return seniority;
	}

	public void setSeniority(Integer seniority) {
		this.seniority = seniority;
	}

	public String getSituation() {
		return situation;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}
}
