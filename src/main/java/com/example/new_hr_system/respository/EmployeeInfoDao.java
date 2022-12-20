package com.example.new_hr_system.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.new_hr_system.entity.EmployeeInfo;

@Repository
public interface EmployeeInfoDao extends JpaRepository<EmployeeInfo, String> {
public List<EmployeeInfo> findByEmployeeCodeIn(List<String> empcode);
	
	public List<EmployeeInfo> findAllById(String Id);
}
