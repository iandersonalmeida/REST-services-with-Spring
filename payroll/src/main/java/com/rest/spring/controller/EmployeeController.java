package com.rest.spring.controller;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.rest.spring.model.Employee;
import com.rest.spring.model.EmployeeModelAssembler;
import com.rest.spring.repository.EmployeeRepository;
import com.rest.spring.exception.EmployeeNotFoundException;

//This annotation is a junction of 2 others: @controller and @ResponseBody;
//@Controller means that Spring will see the class as a controller in MVC layer, and @ResponseBody means that
//a method return value (data) must be bound to the web response body;
@RestController
public class EmployeeController {
	
	
	//A dependency injection is done implicitly by constructor;
	private final EmployeeRepository repository;
	
	//A dependency injection is done implicitly by constructor;
	private final EmployeeModelAssembler assembler;
	
	//The variable repository is initialized;
	public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
		this.repository = repository;	
		this.assembler = assembler;
	}
	
	//@GetMapping maps HTTP GET requests onto specific handler methods;
	//This method will retrieve a ordered collection of objects of a defined type from server;

	@GetMapping("/employees")
	//CollectionModel allows to create a wrapper (container) easily to collect a EntityModel that wraps a domain object(Employee);
	public CollectionModel<EntityModel<Employee>> all()	{
		
		//Here employees receives instances of Employee type in a sequential stream;
		List<EntityModel<Employee>> employees = repository.findAll().stream()
				
				//withRel() takes a string as argument to create a Link object that correspond to the root aggregate;
				//linkTo() create a builder to build Link objects that point to MVC controllers;				
				//To do it, linkTo() uses a wrapper by methodOn that contain a controller method, named all();
				//withSelfRel() creates a Link that corresponds to a resource as a self link;
				//Once these links have been created, of() creates a new EntityModel with links added to it (employee);
				// This new EntityModel with links is the result returned by the -> who took employee as input;
				//map() returns a sequence of objects or entities (a new stream containing such entities);	
				//this new stream is accumulated in a object Collector and returned into a new List;
				//This Collector is used by findAll() to return all the entities of the type Employee that is a collection (List);  
				/*.map(employee -> EntityModel.of(employee,
						linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
						linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
						.collect(Collectors.toList()); 	*/
				
			
				//assembler now has the responsibility to create links and to convert employees object into EntityModel;
				.map(assembler::toModel).collect(Collectors.toList());
		
				
		
		//withSelfRel() creates a Link that correspond to a resource as a self link;
		//linkTo() create a builder to build Link objects that point to MVC controllers;
		//To do it, linkTo() uses a wrapper by methodOn that contain a controller method, named all();
		//Once the link have been created, of() creates a CollectionModel object with a link added to it (employee);
		//a collection of entities is returned with links;
		return CollectionModel.of(employees,
				linkTo(methodOn(EmployeeController.class).all()).withSelfRel());		
	}
	
	//PostMapping maps POST requests onto specific handler methods;
	//This method will bind the parameter to the request body and send it to server for updating or creating a resource;
	@PostMapping("/employees")
	//ResponseEntity will add an HttpStatus status code. To do it:
	public ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {
		//1. newEmployee is saved and returned;
		//2. newEmployee is converted in RepresentationModel that it is a container for a collection of links a add such links to model;
		//3. entityModel represents such container;
		EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));
		//4. the container is used to set the body of response entity or Http response by body() and return it;
		//5. once that Http response is returned, created() creates a builder with the CREATED status and a header the a given Uri. To do it:
		//a. getRequiredLink() returns a link with a identifier for the links'context;
		//b. this link will be a current href as URI;
		//6. A response is returned with header, body and a status code.
		return ResponseEntity //
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(entityModel);
	}
	
	//@GetMapping here means a request to one item from List<Employee> or a list of employees;
	//This method binds its parameter to a URI template variable and retrieve a object with a correspondent id;
	//if the object is not found, then a exception is thrown;
	//If the object is found, so a EntityModel wrapper is created to wraps the domain object and its links and return it;
	@GetMapping("/employees/{id}")
	public EntityModel<Employee> one(@PathVariable Long id) {
		
		Employee employee = repository.findById(id)//
				.orElseThrow(()-> new EmployeeNotFoundException(id));
		
		return assembler.toModel(employee);
		
		/*return EntityModel.of(employee, //
				linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).all()).withRel("employees")); */
	}
	
	//@PutMapping maps PUT requests in this method;
	//This method, first, return a employee by id and then updates the employee with new data and save it;
	//if there is no such id, so the method will return a Optional object, describing the absence of value;
	//Second, if there is no employee with such id, the method will create a new employee and save it;
	@PutMapping("/employees/{id}")
	public Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
		return repository.findById(id)
				.map(employee -> {
					employee.setName(newEmployee.getName());
					employee.setRole(newEmployee.getRole());
					return repository.save(employee);
				})
				.orElseGet(() -> {
					newEmployee.setId(id);
					return repository.save(newEmployee);
				});		
	}
	
	//@DeletingMapping maps a request for deleting by id with the method deleteEmployee;
	//This method just will delete a employee based on its id;
	@DeleteMapping("/employees/{id}")
	//ResponseEntity will add an HttpStatus status code
	public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
		//delete the entity by id
		repository.deleteById(id);
		//build() builds and return a response entity without body;
		// noContent() creates a builder with NO CONETENT status and returns it;
		// in general this method returns a Http message with status 204 or NO CONTENT;
		return ResponseEntity.noContent().build();
	}
	
	
}
