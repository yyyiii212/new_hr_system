package com.example.new_hr_system.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.new_hr_system.entity.EmployeeInfo;

@Repository
public interface EmployeeInfoDao extends JpaRepository<EmployeeInfo, String>{

}
