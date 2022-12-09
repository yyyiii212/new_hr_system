package com.example.new_hr_system.constants;

public enum AbsenceSystemRtnCode {
	
	SUCCESSFUL("200", "���\"), 
	CREATE_SUCCESSFUL("200", " �Ыئ��\"), 
	UPDATE_SUCCESSFUL("200", " ��s���\"),
	Delete_SUCCESSFUL("200", " �R������"),
	ABSENCE_ACCEPT("200", "���а�,����"),
	ABSENCE_REJECT("200", "�ڵ��а�,����"),
	DATA_REQOIRED("417", "�ж�g������"),
	EMPLOYEE_CODE_REQOIRED("417", "���u�s������,�ο��~"),
	ABSENCE_REASON_REQOIRED("417", "���O����,�ο��~"),
	DATE_EMPTY("400", "��J�������"),
	DATE_OF_ABSENCE_EMPTY("400", "�d�L��밲��"),
	ABSENCE_EMPTY("400", "�L������");

	private String code;

	private String message;

	private AbsenceSystemRtnCode(String code, String message) {
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
