package com.rest.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rest.spring.model.Order;

// OrderRepository is a JpaRepository that it is a JPA specific extension of Repository;
//This interface creates a central repository that captures the domain type and domain id's type to manage by operations as CRUD;
public interface OrderRepository extends JpaRepository<Order, Long> {

}
