package com.example.new_hr_system.vo;

import java.time.LocalDate;
import java.util.UUID;

public class SalarySystemReq {
	private String uuid;
	private String employeeCode;
	private String salaryDate;
	private Integer salary;
	private String name;
	private String searchStartDate;
	private String searchEndDate;
	private String pwd;

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

	public String getSalaryDate() {
		return salaryDate;
	}

	public void setSalaryDate(String salaryDate) {
		this.salaryDate = salaryDate;
	}

	public Integer getSalary() {
		return salary;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(String startDate) {
		this.searchStartDate = startDate;
	}

	public String getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(String endDate) {
		this.searchEndDate = endDate;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
