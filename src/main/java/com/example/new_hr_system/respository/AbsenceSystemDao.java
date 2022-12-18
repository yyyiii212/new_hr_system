package com.example.new_hr_system.respository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.new_hr_system.entity.AbsenceSystem;

@Repository
public interface AbsenceSystemDao extends JpaRepository<AbsenceSystem, UUID> {

	public List<AbsenceSystem> findByEmployeeCode(String employeeCode);
	
	public List<AbsenceSystem> findAllByEmployeeCode(String employeeCode);
	
	public List<AbsenceSystem> findByEmployeeCodeOrderByAbsenceDateDesc(String employeeCode);
	
	public List<AbsenceSystem> findAllByOrderByAbsenceDateDesc();


}
