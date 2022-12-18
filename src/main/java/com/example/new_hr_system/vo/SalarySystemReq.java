package com.example.new_hr_system.vo;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SalarySystemReq {
	private String uuid;
	private String employeeCode;
	private String salaryEmployeeCode;
	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd")
	private LocalDate salaryDate;
	private Integer salary;
	private String name;
	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd")
	private LocalDate searchStartDate;
	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd")
	private LocalDate searchEndDate;
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

	public LocalDate getSalaryDate() {
		return salaryDate;
	}

	public void setSalaryDate(LocalDate salaryDate) {
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

	public LocalDate getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(LocalDate startDate) {
		this.searchStartDate = startDate;
	}

	public LocalDate getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(LocalDate endDate) {
		this.searchEndDate = endDate;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getSalaryEmployeeCode() {
		return salaryEmployeeCode;
	}

	public void setSalaryEmployeeCode(String salaryEmployeeCode) {
		this.salaryEmployeeCode = salaryEmployeeCode;
	}
	

}
