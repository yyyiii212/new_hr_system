package com.example.new_hr_system.vo;

import java.util.List;

import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.entity.WorkSystem;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkSystemRes {
	private String message;
	private WorkSystem workSystem;
	private EmployeeInfo employeeInfo;
	private List<WorkSystem> workInfoList;

	public WorkSystemRes() {
	}

	public WorkSystemRes(String message) {
		this.message = message;
	}
	public WorkSystemRes(List<WorkSystem> workInfoList, String message) {
		this.workInfoList = workInfoList;
		this.message = message;
	}

	public WorkSystemRes(WorkSystem workSystem, String message) {
		this.workSystem = workSystem;
		this.message = message;
	}

	public WorkSystemRes(EmployeeInfo employeeInfo, String message) {
		this.employeeInfo = employeeInfo;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public WorkSystem getWorkSystem() {
		return workSystem;
	}

	public void setWorkSystem(WorkSystem workSystem) {
		this.workSystem = workSystem;
	}

	public List<WorkSystem> getWorkInfoList() {
		return workInfoList;
	}

	public void setWorkInfoList(List<WorkSystem> workInfoList) {
		this.workInfoList = workInfoList;
	}

	public EmployeeInfo getEmployeeInfo() {
		return employeeInfo;
	}

	public void setEmployeeInfo(EmployeeInfo employeeInfo) {
		this.employeeInfo = employeeInfo;
	}

}
