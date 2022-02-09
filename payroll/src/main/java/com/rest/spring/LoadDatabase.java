package com.rest.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rest.spring.model.Employee;
import com.rest.spring.model.Order;
import com.rest.spring.model.Status;
import com.rest.spring.repository.EmployeeRepository;
import com.rest.spring.repository.OrderRepository;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;

//A configuration class for declaring beans that will be managed by spring container;
// this class will be initialized with application;
@Configuration
public class LoadDatabase {
	
	//A class variable that receives a logger object corresponding to the class;
	//.class returns a object of the LoadDatabase type;
	//getLogger() "transforms" the object in a Logger and return it;
	//Logger is final and static, so Logger never changes and there is only a copy of its variable does not matter how many
	//times LoadDatabase is instantiate. For all instances of LoadDatabase, there is only a copy of Logger;
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
	
	//This method produces a bean that will be managed by Spring container;
	//It will run in SpringBootApplicaiton when it is detected or scanned by Spring;
	//it will log a string message and it will run in runtime;			
	@Bean
	CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {
		
		//return args refers to main method args. So everything is defined between "{}" will be displayed in runtime in console;
		return args -> {
			
			//save() saves and returns a saved entity;
			employeeRepository.save(new Employee("Maria Lucia", "Almeida", "Teacher"));
			employeeRepository.save(new Employee("Ian", "Almeida", "Systems analyst"));
			
			//findAll() returns all the instances (entities) of the  Employee type;
			//forEach() performs an action in each element and return a message for each employee;
			// -> () takes a employee and returns a employee with a INFO string;
			employeeRepository.findAll().forEach(employee -> log.info("Preloaded " + employee));
			
			//save() saves and returns a saved entity;
			orderRepository.save(new Order("MacBook Pro", Status.COMPLETED));
			orderRepository.save(new Order("iphone", Status.IN_PROGRESS));
			
			//idem as before
			orderRepository.findAll().forEach(order -> log.info("Preloaded " +order)
			);	
			
		};
	}
	
}

