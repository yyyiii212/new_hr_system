package com.example.new_hr_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.new_hr_system.entity.WorkSystem;
import com.example.new_hr_system.service.ifs.WorkSystemService;
import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

@RestController
public class WorkSystemController {
	@Autowired
	private WorkSystemService workSystemService;

	// ---打卡上班
	@PostMapping(value = "/api/punchToWork")
	public WorkSystemRes punchToWork(@RequestBody WorkSystemReq req) {
		return workSystemService.punchToWork(req);
	}

	// ---下班打卡
	@PostMapping(value = "/api/punchToOffWork")
	public WorkSystemRes punchToOffWork(@RequestBody WorkSystemReq req) {
		return workSystemService.punchToOffWork(req);
	}

	// ---給員工的搜尋
	@PostMapping(value = "/api/searchWorkInfoForStaff")
	public WorkSystemRes searchWorkInfoForStaff(@RequestBody WorkSystemReq req) {
		return workSystemService.searchWorkInfoForStaff(req);
	}

	// ---給主管的搜尋
	@PostMapping(value = "/api/searchWorkInfoForManager")
	public WorkSystemRes searchWorkInfoForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.searchWorkInfoForManager(req);
	}

	// ---給主管的刪除 (用於清空數據庫，過滿時會消耗效能)
	@PostMapping(value = "/api/deleteWorkInfoByDateBetween")
	public WorkSystemRes deleteWorkInfoByDateBetween(@RequestBody WorkSystemReq req) {
		return workSystemService.deleteWorkInfoByDateBetween(req);
	}

	// ---給主管的新增曠職行為
	@PostMapping(value = "/api/creatAbsenteeismForManager")
	public WorkSystemRes creatAbsenteeismForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.creatAbsenteeismForManager(req);
	}

	// ---給主管的刪除<曠職>行為
	@PostMapping(value = "/api/deleteAbsenteeismForManager")
	public WorkSystemRes deleteAbsenteeismForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.deleteAbsenteeismForManager(req);
	}

}
