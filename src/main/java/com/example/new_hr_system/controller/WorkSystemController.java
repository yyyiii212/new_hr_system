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

	// ---���d�W�Z
	@PostMapping(value = "/api/punchToWork")
	public WorkSystemRes punchToWork(@RequestBody WorkSystemReq req) {
		return workSystemService.punchToWork(req);
	}

	// ---�U�Z���d
	@PostMapping(value = "/api/punchToOffWork")
	public WorkSystemRes punchToOffWork(@RequestBody WorkSystemReq req) {
		return workSystemService.punchToOffWork(req);
	}

	// ---�����u���j�M
	@PostMapping(value = "/api/searchWorkInfoForStaff")
	public WorkSystemRes searchWorkInfoForStaff(@RequestBody WorkSystemReq req) {
		return workSystemService.searchWorkInfoForStaff(req);
	}

	// ---���D�ު��j�M
	@PostMapping(value = "/api/searchWorkInfoForManager")
	public WorkSystemRes searchWorkInfoForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.searchWorkInfoForManager(req);
	}

	// ---���D�ު��R�� (�Ω�M�żƾڮw�A�L���ɷ|���Ӯį�)
	@PostMapping(value = "/api/deleteWorkInfoByDateBetween")
	public WorkSystemRes deleteWorkInfoByDateBetween(@RequestBody WorkSystemReq req) {
		return workSystemService.deleteWorkInfoByDateBetween(req);
	}

	// ---���D�ު��s�W�m¾�欰
	@PostMapping(value = "/api/creatAbsenteeismForManager")
	public WorkSystemRes creatAbsenteeismForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.creatAbsenteeismForManager(req);
	}

	// ---���D�ު��R��<�m¾>�欰
	@PostMapping(value = "/api/deleteAbsenteeismForManager")
	public WorkSystemRes deleteAbsenteeismForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.deleteAbsenteeismForManager(req);
	}

}
