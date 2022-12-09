package com.example.new_hr_system.vo;

import java.util.List;

import com.example.new_hr_system.entity.SalarySystem;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalarySystemRes {
	private String message;
	private SalarySystem salarySystem;
	private List<SalarySystem> SalarySystemList;

	public SalarySystemRes() {

	}

	public SalarySystemRes(SalarySystem salarySystem, String message) {
		this.salarySystem = salarySystem;
		this.message = message;
	}

	public SalarySystemRes(String message) {

		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public SalarySystem getSalarySystem() {
		return salarySystem;
	}

	public void setSalarySystem(SalarySystem salarySystem) {
		this.salarySystem = salarySystem;
	}

	public List<SalarySystem> getSalarySystemList() {
		return SalarySystemList;
	}

	public void setSalarySystemList(List<SalarySystem> salarySystemList) {
		SalarySystemList = salarySystemList;
	}

}
