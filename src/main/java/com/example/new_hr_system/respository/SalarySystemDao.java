package com.example.new_hr_system.respository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.new_hr_system.entity.SalarySystem;
@Repository
public interface SalarySystemDao extends JpaRepository<SalarySystem, UUID>{

}
