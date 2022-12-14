package com.example.new_hr_system.vo;

import java.util.List;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeInfoRes {

	private String message;

	private EmployeeInfo employeeInfo;
	
	private List<EmployeeInfo> employeeInfoList;

	public EmployeeInfoRes() {

	}
	
	public EmployeeInfoRes(String message) {
		this.message = message;
	}
	

	public EmployeeInfoRes(EmployeeInfo employeeInfo, String message) {
		this.employeeInfo = employeeInfo;
		this.message = message;
	}

	public EmployeeInfoRes(List<EmployeeInfo> employeeInfoList, String message) {
		this.employeeInfoList = employeeInfoList;
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

	public List<EmployeeInfo> getEmployeeInfoList() {
		return employeeInfoList;
	}

	public void setEmployeeInfoList(List<EmployeeInfo> employeeInfoList) {
		this.employeeInfoList = employeeInfoList;
	}
	
}
