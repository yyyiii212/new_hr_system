package com.example.new_hr_system.respository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.new_hr_system.entity.WorkSystem;

@Transactional
@Repository
public interface WorkSystemDao extends JpaRepository<WorkSystem, UUID> {
	// 上班打卡
	public List<WorkSystem> findByEmployeeCode(String employeeCode);

	// 查詢上班狀況
	public List<WorkSystem> findByEmployeeCodeOrderByWorkTimeDesc(String employeeCode);

	// 查詢上班狀況 時間區間
	public List<WorkSystem> findByWorkTimeBetweenOrderByWorkTimeDesc(LocalDateTime lcalDateStart,
			LocalDateTime lcalDateEnd);

	// 刪除上班狀況 時間區間
	public void deleteByWorkTimeBetween(LocalDateTime lcalDateStart, LocalDateTime lcalDateEnd);

	// 查詢上班狀況 時間區間、員工編號
	public List<WorkSystem> findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(String employeeCode,
			LocalDateTime lcalDateStart, LocalDateTime lcalDateEnd);

	public List<WorkSystem> findByEmployeeCodeAndWorkTimeGreaterThanEqual(String employeeCode,
			LocalDateTime lcalDateStart);

	public List<WorkSystem> findAllByOrderByWorkTimeDesc();

	public List<WorkSystem> findByEmployeeCodeAndWorkTime(String employeeCode, LocalDateTime lcalDateStart);

}
