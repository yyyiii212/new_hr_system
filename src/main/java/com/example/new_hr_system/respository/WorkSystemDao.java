package com.example.new_hr_system.respository;

import java.time.LocalDate;
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
	// �W�Z���d
	public List<WorkSystem> findByEmployeeCode(String employeeCode);

	// �d�ߤW�Z���p
	public List<WorkSystem> findByEmployeeCodeOrderByWorkTimeDesc(String employeeCode);

	// �d�ߤW�Z���p �ɶ��϶�
	public List<WorkSystem> findByWorkTimeBetweenOrderByWorkTimeDesc(LocalDateTime lcalDateStart,
			LocalDateTime lcalDateEnd);

	// �R���W�Z���p �ɶ��϶�
	public void deleteByWorkTimeBetween(LocalDateTime lcalDateStart, LocalDateTime lcalDateEnd);

	// �d�ߤW�Z���p �ɶ��϶��B���u�s��
	public List<WorkSystem> findByEmployeeCodeAndWorkTimeBetweenOrderByWorkTimeDesc(String employeeCode,
			LocalDateTime lcalDateStart, LocalDateTime lcalDateEnd);

	public List<WorkSystem> findByEmployeeCodeAndWorkTimeGreaterThanEqual(String employeeCode,
			LocalDateTime lcalDateStart);

	public List<WorkSystem> findAllByOrderByWorkTimeDesc();

	// �R���m¾
	public List<WorkSystem> findByEmployeeCodeAndWorkTime(String employeeCode, LocalDateTime lcalDateStart);

}
