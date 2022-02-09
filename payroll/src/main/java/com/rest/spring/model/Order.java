package com.rest.spring.model;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;



//@Entity becomes this class a entity;
@Entity
//@tTable defines a primary table with the name CUSTOMER_ORDER;
@Table(name = "CUSTOMER_ORDER")
public class Order {
	
	//@Id and @GeneratedValue defines primary key and auto-increment, respectively;
	private @Id @GeneratedValue Long id;
	
	private String description;
	private Status status;
	
	public Order() {}

	public Order(String description, Status status) {
		super();
		this.description = description;
		this.status = status;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	
	@Override
	//equals() compares objects and their fields to identify if they are equal or not and return true or false, respectively;
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof Order))
			return false;
		Order order = (Order)o;
		return Objects.equals(this.id, order.id)
				&& Objects.equals(this.description, order.description)
				&& this.status == order.status;
	}
		
		@Override
		//hashCode() returns a hash code value for the object (Order);
		public int hashCode() {
			return Objects.hash(this.id, this.description, this.status);
		}
		
		@Override
		//toString() returns a string representation of the object;
		public String toString() {
			return "Order{" + "id=" + this.id + ", description='" + this.description + '\'' 
					+ ", status=" + this.status + '}';
		}
				
	}
	


