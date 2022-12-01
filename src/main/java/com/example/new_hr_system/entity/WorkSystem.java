package com.example.new_hr_system.entity;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "work_system")
public class WorkSystem {
	
	@Id
	@Column(name = "uuid")
	@Type(type = "uuid-char")
	private UUID uuid;
	
	@Column(name = "employee_code")
	private String employeeCode;
	
	@Column(name = "work_time")
	private LocalDate workTime;
	
	@Column(name = "off_work_time")
	private LocalDate offWorkTime;
	
	@Column(name = "attendance_status")
	private String attendanceStatus;
	
	@Column(name = "attendance_hours")
	private int attendanceHours;
	
	public WorkSystem() {
		
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

	public LocalDate getWorkTime() {
		return workTime;
	}

	public void setWorkTime(LocalDate workTime) {
		this.workTime = workTime;
	}

	public LocalDate getOffWorkTime() {
		return offWorkTime;
	}

	public void setOffWorkTime(LocalDate offWorkTime) {
		this.offWorkTime = offWorkTime;
	}

	public String getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(String attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	public int getAttendanceHours() {
		return attendanceHours;
	}

	public void setAttendanceHours(int attendanceHours) {
		this.attendanceHours = attendanceHours;
	}
	
}
