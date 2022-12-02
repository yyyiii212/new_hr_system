package com.example.new_hr_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.new_hr_system.service.ifs.WorkSystemService;
import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

@RestController
public class WorkSystemController {
	@Autowired
	private WorkSystemService workSystemService;

	// ---kai
	@PostMapping(value = "/api/punchToWork")
	public WorkSystemRes punchToWork(@RequestBody WorkSystemReq req) {
		return workSystemService.punchToWork(req);
	}

	// ---kai
	@PostMapping(value = "/api/punchToOffWork")
	public WorkSystemRes punchToOffWork(@RequestBody WorkSystemReq req) {
		return workSystemService.punchToOffWork(req);
	}

	// ---kai
	@PostMapping(value = "/api/searchWorkInfo")
	public WorkSystemRes searchWorkInfo(@RequestBody WorkSystemReq req) {
		return workSystemService.searchWorkInfo(req);
	}
}
