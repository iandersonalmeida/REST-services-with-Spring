package com.rest.spring.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rest.spring.exception.OrderNotFoundException;
import com.rest.spring.model.Order;
import com.rest.spring.model.OrderModelAssembler;
import com.rest.spring.model.Status;
import com.rest.spring.repository.OrderRepository;


//This annotation is a junction of 2 others: @controller and @ResponseBody;
//@Controller means that Spring will see the class as a controller in MVC layer, and @ResponseBody means that
//a method return value (data) must be bound to the web response body;
@RestController
public class OrderController {
	
	//Injection of Dependency
	private final OrderRepository orderRepository;
	private final OrderModelAssembler assembler;
	
	
	// Constructor for initializing the fields;
	public OrderController(OrderRepository orderRepository, OrderModelAssembler assembler) {
		super();
		this.orderRepository = orderRepository;
		this.assembler = assembler;
	}
	
	//----------------------------------------------------------------------------------------------------------------------
	
	//GetMapping associates HTTP GET requests in this "/orders" path to a specific method, in our case, all();
	@GetMapping("/orders")
	//CollectionModel helps to create a container for collecting entities of Order'type;
	public CollectionModel<EntityModel<Order>> all(){
		
		//stream() makes this new stream sequential in according of entry order;
		//findAll() returns all the instances of the Order type. In database world, we could call such instances of rows in table maybe;
		List<EntityModel<Order>> orders = orderRepository.findAll().stream()
				
				//map() returns a new stream of RepresentationModel;			
				.map(assembler::toModel)
				
				//toList() returns a Collector. This Collector accumulates input elements into a new List;
				//collect() performs an accumulation of input elements in a container that can be mutable. For that, it uses a Collector;
				//collect returns a container with elements;
				.collect(Collectors.toList());
		
		//all() returns a CollectionModel object with entities and correspondents links;
		return CollectionModel.of(orders,
				linkTo(methodOn(OrderController.class).all()).withSelfRel());
	}
	
	//----------------------------------------------------------------------------------------------------------------------------
	
	//GetMapping() associates Http requests onto method one() with a determined URI template variable id for getting a resource;
	@GetMapping	("/orders/{id}")
	
	//one() has a paramater that corresponds to the URI template variable;
	public EntityModel<Order> one(@PathVariable Long id){
		
		//findById() returns a entity with a given id or null if not;
		Order order = orderRepository.findById(id)
				//() -> return a object that represents that there is no determined resource;
				//orElseThrow() returns a value if it exists. If not, it returns a exception;
				.orElseThrow(() -> new OrderNotFoundException(id));
		
		//toModel takes such entity and converts it in a RepresentationModel and return it;
		return assembler.toModel(order);
	}
	
    //----------------------------------------------------------------------------------------------------------------------------------------
	
	//PutMapping() associates requests for "/orders" onto newOrder;
	@PutMapping("/orders")
	
	//newOrder() has a parameter that corresponds to the body of the web request;
	ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order){
		
		// setStatus() defines status of the Order as IN_PROGRESS;
		order.setStatus(Status.IN_PROGRESS);
		
		// orderRepository() saves an Order and returns it to newOrder; 
		Order newOrder = orderRepository.save(order);
		
		//newOrder() returns a response with CREATED status;
		return ResponseEntity
				
				//methodOn() returns a object of type modeled by Class object, or OrderControler;
				//on the basis of the OrderController object, we call one() and define the id. See OrderModelAssembler class for comments;
				//with linkTo() returns a link that points to the controller method, one();
				//toUri() creates a URI of the created link;
				//created() returns a builder with status CREATED. I would say it returns a CREATED	 message;
				.created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
				
				//toModel() converts the entity newOrder in RepresentationModel;
				//and body() set it as body of response entity;
				.body(assembler.toModel(newOrder));
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------
	
	
	//DeleteMapping() associates HTTP DELETE requests onto cancel();
	@DeleteMapping("/orders/{id}/cancel")
	//cancel() has a paramater that corresponds to the URI template variable, allowing to get a certain resource;
	public ResponseEntity<?> cancel(@PathVariable Long id){
		// findById() returns a object Order that it is a entity. If such entity exists, so assign it to order, if not, a exception;
		Order order = orderRepository.findById(id)
				//()-> gets a result and return it or in our case, a exception;
				// this exception is just returned if the value does not exist;
				//if value exists, then orElseThrow() returns it;
				.orElseThrow(()-> new OrderNotFoundException(id));
		
		//save() return a entity and toModel() converts it in RepresentationModel;
		//ok() creates a EntityResponse with OK status and return it; 
		if(order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.CANCELLED);
			return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
		}
		
		
		
		//cancel() returns a HTTP response that includes a status code and carry details of a Problem;
		return ResponseEntity
				//status() creates a builder with 405 status and return it;
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				//with that builder, header() adds a header with a name (headers request) and
				//value (hypermedia) and defines it in response entity and returns this builder;
				.header(org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
				//and on basis of this builder, body() sets a body of the response entity and return it;
				//the type of this body is empty Problem object. This object represents a way to convey, beyond of status code,
				// details of errors that might occur and such problems details are carried by a HTTP response;
				//Problem  will have some attributes like title, withTitle(), and details, withDetail();
				.body(Problem.create()
						.withTitle("Method not allowed")
						.withDetail("You can't cancel an order that is in the " + order.getStatus() + " status"));
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------
	
	//PutMapping() associates HTTP PUT requests to the complete() method;
	@PutMapping("/orders/{id}/complete")
	//complete() has a parameter that indicates it is bound to the URI template variable or {id}; 
	// So what happens with parameter is reflected in template variable;
	public ResponseEntity<?> complete(@PathVariable Long id){
		
		//idem as in cancel() method;
		Order order = orderRepository.findById(id)
				.orElseThrow(()-> new OrderNotFoundException(id));
		
		//idem as in cancel() method;
		if(order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.COMPLETED);
			return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
		}
		
		//idem as in cancel() method;
		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.header(org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
				.body(Problem.create()
						.withTitle("Method not allowed")
						.withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));			
		
	}
	
}
