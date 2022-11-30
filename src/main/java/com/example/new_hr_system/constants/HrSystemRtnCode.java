package com.example.new_hr_system.constants;

public enum HrSystemRtnCode {
	SUCCESSFUL("200", "Successful !"),
	Employee_CODE("400","EmployeeCode cannot be null or empty!!"),
	Employee_NAME("400","EmployeeName cannot be null or empty!!"),
	Employee_ID("400","EmployeeId cannot be null or empty!!"),
	Employee_EMAIL("400","EmployeeEmail cannot be null or empty!!"),
	Employee_SECTION("400","EmployeeSection cannot be null or empty!!"),
	Employee_SITUATION("400","EmployeeSituation cannot be null or empty!!"),
	Employee_LEVEL("400","EmployeeLevel just can be 0-2 !!"),
	Employee_SENIORITY("400","EmployeeSeniority cannot be null or 0 !!"),
	ERROR("400","Not found !!");

	private String code;

	private String message;
	
	private HrSystemRtnCode(String code,String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
