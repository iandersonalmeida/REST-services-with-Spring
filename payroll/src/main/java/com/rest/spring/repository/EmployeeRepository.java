package com.rest.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rest.spring.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
