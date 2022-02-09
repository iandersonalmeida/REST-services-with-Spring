package com.rest.spring.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity //It means that this class is entity now or a domain object for storing;
public class Employee {
	
	//converts this instance variable in a property (attribute) of the entity as primary key with auto increment;
	private @Id @GeneratedValue Long id; 
	
	private String name;
	private String firstName;
	private String lastName;
	private String role;
	
	public Employee() {}
	
	

	public Employee(String firstName, String lastName, String role) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}



	/*public Employee(String name, String role) {
		super();
		this.name = name;
		this.role = role;
	}*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return firstName + " " +lastName;
	}

	public void setName(String name) {
		String[] parts = name.split(" ");
		this.firstName = parts[0];
		this.lastName = parts[1];
		//this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
		
	public String getFirstName() {
		return firstName;
	}



	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	//This method compare data and returns if is equal or not;
	@Override
	public boolean equals (Object o) {
		if(this == o) {
			return true;			
		}
		if(!(o instanceof Employee)) {
			return false;
		}
		
		Employee employee = (Employee) o;
		return Objects.equals(this.id, employee.id)
				//&& Objects.equals(this.name, employee.name)
				&& Objects.equals(this.firstName, employee.firstName)
				&& Objects.equals(this.lastName, employee.lastName)
				&& Objects.equals(this.role, employee.role);
	}
	
	//This method generates a hash code for a sequence of input values;
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.firstName, this.lastName, this.role);
	}
	
	//This method returns a string with data of the object;
	@Override
	public String toString() {
		return  "Employee{" + "id = " + this.id 
				+ ", firstName='" + this.firstName + '\'' 
				+ ", lastName='" + this.lastName +  '\''
				+ ", role='" + this.role + '\'' + '}';
	}
}
