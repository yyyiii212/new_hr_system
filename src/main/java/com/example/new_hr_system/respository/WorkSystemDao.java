package com.example.new_hr_system.respository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.new_hr_system.entity.WorkSystem;

@Repository
public interface WorkSystemDao extends JpaRepository<WorkSystem, UUID> {
	List<WorkSystem> findByEmployeeCodeOrderByWorkTimeDesc(String employeeCode);
}
