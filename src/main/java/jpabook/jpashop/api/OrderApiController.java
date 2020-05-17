package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.query.OrderFlatDto;
import jpabook.jpashop.repository.query.OrderQueryDto;
import jpabook.jpashop.repository.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            for(OrderItem orderItem : orderItems) {
                orderItem.getItem().getName();
            }
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> all = orderRepository.findAll(new OrderSearch());
        List<OrderDto> collect = all.stream()
                .map( o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;

    }
    @GetMapping("/api/v3/orders") // 한방에 끝내서 좋기는 한데, 페이징이 안됨
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();

        for ( Order order : orders ) {
            System.out.println("order ref = " + order);
            System.out.println("order id = " + order.getId());
        }

        List<OrderDto> collect = orders.stream()
                .map( o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v3.1/orders") // 일대다 개수 만큼 쿼리가 증가하긴 하지만 페이징 가능 ( 사용 권장 1 )
    public List<OrderDto> ordersV3Page(
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        for ( Order order : orders ) {
            System.out.println("order ref = " + order);
            System.out.println("order id = " + order.getId());
        }

        List<OrderDto> collect = orders.stream()
                .map( o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v4/orders") // select 할때 바로 DTO 를 변환해서 받고 싶을 때 사용. 단 N + 1 문제 발생, 페이징 가능
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders") // N+1 문제를 해결 했으나, 여전히 일대다 개수 만큼 쿼리가 증가함 , 페이징 가능 ( 사용 권장 2 )
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDtoOptimization();
    }

    @GetMapping("/api/v6/orders") // 쿼리 한방에 가져올 수 있으나, 페이징이 안된다., Dto 원하는 스펙을 맞추기 위해서 직접 하나씩 변환 해야 한다.
    public List<OrderFlatDto> ordersV6() {
        return orderQueryRepository.findAllByDtoFlat();
    }

    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            this.orderItems = order.getOrderItems()
                    .stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {

        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }

    }
}
