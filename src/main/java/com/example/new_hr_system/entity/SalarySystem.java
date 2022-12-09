package com.example.new_hr_system.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "salary_system")
public class SalarySystem {

	@Id
	@Column(name = "uuid")
	@Type(type = "uuid-char")
	private UUID uuid;
	
	@Column(name = "employee_code")
	private String employeeCode;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "salary_date")
	private Date salaryDate;
	
	@Column(name = "salary")
	private int salary;
	
	@Column(name = "raise_pay")
	private int raisePay;
	
	@Column(name = "manager_raise_pay")
	private int managerRaisePay;
	
	@Column(name = "salary_deduct")
	private int salaryDeduct;
	
	@Column(name = "total_salary")
	private int totalSalary;
	
	public SalarySystem() {
		
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
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

	public Date getSalaryDate() {
		return salaryDate;
	}

	public void setSalaryDate(Date salaryDate) {
		this.salaryDate = salaryDate;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public int getRaisePay() {
		return raisePay;
	}

	public void setRaisePay(int raisePay) {
		this.raisePay = raisePay;
	}

	public int getManagerRaisePay() {
		return managerRaisePay;
	}

	public void setManagerRaisePay(int managerRaisePay) {
		this.managerRaisePay = managerRaisePay;
	}

	public int getSalaryDeduct() {
		return salaryDeduct;
	}

	public void setSalaryDeduct(int salaryDeduct) {
		this.salaryDeduct = salaryDeduct;
	}

	public int getTotalSalary() {
		return totalSalary;
	}

	public void setTotalSalary(int totalSalary) {
		this.totalSalary = totalSalary;
	}
	
}
