package com.example.new_hr_system.vo;

import java.util.List;

import com.example.new_hr_system.entity.AbsenceSystem;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbsenceSystemResList {

	private String message;

	private List<AbsenceSystem> absenceSystemList;

	private List<AbsenceSystemRes> absenceSystemResList;

	public AbsenceSystemResList() {

	}

	public AbsenceSystemResList(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<AbsenceSystemRes> getAbsenceSystemResList() {
		return absenceSystemResList;
	}

	public void setAbsenceSystemResList(List<AbsenceSystemRes> absenceSystemResList) {
		this.absenceSystemResList = absenceSystemResList;
	}

	public List<AbsenceSystem> getAbsenceSystemList() {
		return absenceSystemList;
	}

	public void setAbsenceSystemList(List<AbsenceSystem> absenceSystemList) {
		this.absenceSystemList = absenceSystemList;
	}

}
