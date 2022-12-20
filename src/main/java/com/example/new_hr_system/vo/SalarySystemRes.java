package com.example.new_hr_system.vo;

import java.util.List;

import com.example.new_hr_system.entity.SalarySystem;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalarySystemRes {
	private String message;
	private SalarySystem salarySystem;
	private List<SalarySystem> salarySystemList;

	public SalarySystemRes() {

	}
	public SalarySystemRes(List<SalarySystem> salarySystemList,String message) {
		this.salarySystemList = salarySystemList;
		this.message = message;
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
		return salarySystemList;
	}

	public void setSalarySystemList(List<SalarySystem> salarySystemList) {
		this.salarySystemList = salarySystemList;
	}

}
