package com.example.new_hr_system.constants;

public enum AbsenceSystemRtnCode {
	
	SUCCESSFUL("200", "成功"), 
	CREATE_SUCCESSFUL("200", " 創建成功"), 
	UPDATE_SUCCESSFUL("200", " 更新成功"),
	Delete_SUCCESSFUL("200", " 刪除完成"),
	ABSENCE_ACCEPT("200", "批准請假,完成"),
	ABSENCE_REJECT("200", "拒絕請假,完成"),
	DATA_REQOIRED("417", "請填寫完整資料"),
	UUID_EMPTY("417", "UUID為空"),
	EMPLOYEE_CODE_REQOIRED("417", "員工編號為空,或錯誤"),
	ABSENCE_REASON_REQOIRED("417", "假別為空,或錯誤"),
	DATE_EMPTY("400", "輸入日期為空"),
	DATE_OF_ABSENCE_EMPTY("400", "查無當月假單"),
	ABSENCE_EMPTY("400", "無此假單");

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
