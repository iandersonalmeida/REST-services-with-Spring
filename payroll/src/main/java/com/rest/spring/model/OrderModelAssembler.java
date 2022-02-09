package com.rest.spring.model;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.rest.spring.controller.OrderController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

//OrderModelAssembler implements a interface that converts a domain type in a RepresentationModel;
@Component
//This class is a "component"; What does it mean in the Spring world? It means that it is a bean and Spring will detect it automatically;
public class OrderModelAssembler implements RepresentationModelAssembler<Order,EntityModel<Order>> {

	@Override
	//toModel() will convert Order in a RepresentationModel; Oder will be a DTO to collect links;
	public EntityModel<Order> toModel(Order order) {
		
		//
		EntityModel<Order> orderModel = EntityModel.of(order,
				//methodOn() has as argument named OrderController.class. OrderController.class means that the Class object models the type
				// of the class, in our case, OrderController. So, for OrderController.class, we have Class<OrderController>;
				//if the type of the class being modeled was unknown, so we would use <?>; 
				//.class returns a Class or object that corresponds to the type OrderController;
				//methodOn() takes the object and makes it a proxy. This proxy will record the method invocation and expose it in the proxy
				//created for the type of return of the method;
				//This proxy will invoke a dummy method of the target method (one()), and it will record in proxy object created;
				//linkTo() creates a link and returns it that point to a controller method (one()). So we build a mapping between a link and a method;
				//withSelfRel() create the link from the Link built that corresponds to the self link of the resource;
				linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
				//idem for methodOn(), linkTo() and withRel();
				//withRel() creates a Link object with a given link relation named "orders" that corresponds to the resource;
				linkTo(methodOn(OrderController.class).all()).withRel("orders"));
		if(order.getStatus() == Status.IN_PROGRESS) {
			//add() adds a given link to the resource;
			//idem for methodOn(), linkTo() and withRel();
			orderModel.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
			orderModel.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
		}
		return orderModel;
	}

}
