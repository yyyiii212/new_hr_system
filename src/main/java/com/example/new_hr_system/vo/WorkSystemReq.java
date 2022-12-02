package com.example.new_hr_system.vo;

public class WorkSystemReq {
	private String uuid;
	private String employeeCode;
	private Integer searchMonth;
	private Integer searchYear;

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

	public Integer getSearchMonth() {
		return searchMonth;
	}

	public void setSearchMonth(Integer searchMonth) {
		this.searchMonth = searchMonth;
	}

	public Integer getSearchYear() {
		return searchYear;
	}

	public void setSearchYear(Integer searchYear) {
		this.searchYear = searchYear;
	}

}
