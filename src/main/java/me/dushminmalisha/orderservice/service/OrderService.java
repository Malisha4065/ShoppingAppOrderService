package me.dushminmalisha.orderservice.service;

import lombok.RequiredArgsConstructor;
import me.dushminmalisha.orderservice.dto.OrderLineItemsDto;
import me.dushminmalisha.orderservice.dto.OrderRequest;
import me.dushminmalisha.orderservice.model.Order;
import me.dushminmalisha.orderservice.model.OrderLineItems;
import me.dushminmalisha.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {
         Order order = new Order();
         order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        // Call inventory service, place order if the product is in-stock
        Boolean result = webClient.get()
                .uri("http://localhost:8082/api/inventory")
                .retrieve().bodyToMono(Boolean.class).block();

        if(result) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock. Please try again later.");
        }
    }

    private OrderLineItems mapDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();

        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }
}
