package com.rest.spring.exception;

//This class throw a excpetion in runtime
public class EmployeeNotFoundException extends RuntimeException {
	
	public EmployeeNotFoundException(Long id) {
		super("Could not find employee " + id);
	}
}
