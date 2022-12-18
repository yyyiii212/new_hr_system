package com.example.new_hr_system.respository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import com.example.new_hr_system.entity.SalarySystem;
@Transactional
@Repository
public interface SalarySystemDao extends JpaRepository<SalarySystem, UUID> {
	public List<SalarySystem> findByEmployeeCode(String employeeCode);

	public List<SalarySystem> findByEmployeeCodeOrderBySalaryDateDesc(String employeeCode);// 搜尋員工薪水資料

	public List<SalarySystem> findBySalaryDateBetweenOrderBySalaryDateDesc(LocalDate startDate, LocalDate endDate);

	public List<SalarySystem> findByEmployeeCodeAndSalaryDateBetweenOrderBySalaryDateDesc(String employeeCode,
			LocalDate startDate, LocalDate endDate);
	public List<SalarySystem> findByOrderBySalaryDateDesc();

}
