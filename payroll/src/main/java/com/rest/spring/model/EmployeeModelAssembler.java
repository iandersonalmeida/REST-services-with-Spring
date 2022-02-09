package com.rest.spring.model;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.rest.spring.controller.EmployeeController;

// This class is a "component"; What does it mean in the Spring world? It means that it is a bean and Spring will detect it automatically;
@Component
//This class object converts types (employee) into RepresentationModel by toModel() method;
public class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {

		
	@Override
	public EntityModel<Employee> toModel(Employee employee) {
		
		//withRel() takes a string as argument to create a Link object that correspond to the root aggregate;
		//linkTo() create a builder to build Link objects that point to MVC controllers;				
		//To do it, linkTo() uses a wrapper by methodOn that contain a controller method, named all();
		//withSelfRel() creates a Link that correspond to a resource as a self link;
		//Once these links have been created, of() creates a new EntityModel with links added to it (employee);
		return EntityModel.of(employee,//
				linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}

}
