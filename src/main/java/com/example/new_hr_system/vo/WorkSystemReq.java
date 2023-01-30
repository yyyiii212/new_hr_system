package com.example.new_hr_system.vo;

public class WorkSystemReq {
	private String uuid;
	private String employeeCode;
	private String searchStartDate;
	private String searchEndDate;
	private String password;
	private String absenteeismDate;
	private String managerEmployeeCode;
	private String offWorkTime;
	private String punchWorkTime;
	private int deleteYear;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(String SearchStartDate) {
		this.searchStartDate = SearchStartDate;
	}

	public String getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(String searchEndDate) {
		this.searchEndDate = searchEndDate;
	}

	public int getDeleteYear() {
		return deleteYear;
	}

	public void setDeleteYear(int deleteYear) {
		this.deleteYear = deleteYear;
	}

	public String getAbsenteeismDate() {
		return absenteeismDate;
	}

	public void setAbsenteeismDate(String absenteeismDate) {
		this.absenteeismDate = absenteeismDate;
	}

	public String getManagerEmployeeCode() {
		return managerEmployeeCode;
	}

	public void setManagerEmployeeCode(String managerEmployeeCode) {
		this.managerEmployeeCode = managerEmployeeCode;
	}

	public String getOffWorkTime() {
		return offWorkTime;
	}

	public void setOffWorkTime(String offWorkTime) {
		this.offWorkTime = offWorkTime;
	}

	public String getPunchWorkTime() {
		return punchWorkTime;
	}

	public void setPunchWorkTime(String punchWorkTime) {
		this.punchWorkTime = punchWorkTime;
	}

}
