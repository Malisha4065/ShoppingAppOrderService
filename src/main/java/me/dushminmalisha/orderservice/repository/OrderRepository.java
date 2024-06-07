package me.dushminmalisha.orderservice.repository;

import me.dushminmalisha.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
