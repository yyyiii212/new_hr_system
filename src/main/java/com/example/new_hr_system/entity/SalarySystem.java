package com.example.new_hr_system.entity;

import java.time.LocalDate;
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
	private LocalDate salaryDate; // �~��~��

	@Column(name = "salary")
	private int salary=20000; // ���~ �w�]20000

	@Column(name = "raise_pay")
	private int raisePay; // �@��[�� �w�]0

	@Column(name = "manager_raise_pay")
	private int managerRaisePay; // �D�ޥ[�� �w�]0

	@Column(name = "salary_deduct")
	private int salaryDeduct; // �~�ꦩ�� �w�]0

	@Column(name = "total_salary")
	private int totalSalary = (getSalary() + getRaisePay() + getManagerRaisePay() + (getSalaryDeduct())); // �w�]20000

	public SalarySystem() {

	}

	public SalarySystem(UUID uuid, String employeeCode, String name, LocalDate salaryDate, int salary, int raisePay,
			int managerRaisePay, int salaryDeduct, int totalSalary) {
		this.uuid = uuid;
		this.employeeCode = employeeCode;
		this.name = name;
		this.salaryDate = salaryDate;
		this.salary = salary;
		this.raisePay = raisePay;
		this.managerRaisePay = managerRaisePay;
		this.salaryDeduct = salaryDeduct;
		this.totalSalary = totalSalary;
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

	public LocalDate getSalaryDate() {
		return salaryDate;
	}

	public void setSalaryDate(LocalDate salaryDate) {
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
