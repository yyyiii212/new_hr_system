package com.example.new_hr_system.vo;

import com.example.new_hr_system.entity.EmployeeInfo;

public class EmployeeInfoRes {

	private String message;

	private EmployeeInfo employeeInfo;

	public EmployeeInfoRes() {

	}
	
	public EmployeeInfoRes(String message) {
		this.message = message;
	}

	public EmployeeInfoRes(EmployeeInfo employeeInfo, String message) {
		this.employeeInfo = employeeInfo;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EmployeeInfo getEmployeeInfo() {
		return employeeInfo;
	}

	public void setEmployeeInfo(EmployeeInfo employeeInfo) {
		this.employeeInfo = employeeInfo;
	}
}
