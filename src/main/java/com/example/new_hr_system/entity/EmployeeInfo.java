package com.example.new_hr_system.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "employee_info")
public class EmployeeInfo {

	@Id
	@Column(name = "employee_code")
	private String employeeCode;

	@Column(name = "name")
	private String name;

	@Column(name = "id")
	private String id;

	@Column(name = "employee_email")
	private String employeeEmail;

	@Column(name = "section")
	private String section;

	@Column(name = "level")
	private Integer level;

	@Column(name = "seniority")
	private int seniority;

	@Column(name = "situation")
	private String situation;

	@Column(name = "join_time")
	private Date joinTime;

	public EmployeeInfo() {

	}

	public EmployeeInfo(String name, String id, String employeeEmail, String section, String situation) {
		this.name = name;
		this.id = id;
		this.employeeEmail = employeeEmail;
		this.section = section;
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

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getSeniority() {
		return seniority;
	}

	public void setSeniority(int seniority) {
		this.seniority = seniority;
	}

	public String getSituation() {
		return situation;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}

	public Date getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}

	public String getEmployeeEmail() {
		return employeeEmail;
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}

}
