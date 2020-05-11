package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    @Transactional
    public void 상품주문() {
        //given
        Member member = createMember("회원1", new Address("서울","강가","123-123"));
        int quantity = 10;
        int price = 10000;
        Book book = createBook("시골 JPA", price, quantity);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확 해야 한다.");
        assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "주문한 가격은 가격 * 수량 이다.");
        assertEquals(quantity - orderCount, book.getStockQuantity(), "주문한 수량 만큼 재고가 줄어야 한다.");
        
    }

    @Test
    @Transactional
    public void 상품주문_재고수량초과(){
        //given
        Member member = createMember("회원1", new Address("서울","강가","123-123"));
        int quantity = 10;
        int price = 10000;
        Book book = createBook("시골 JPA", price, quantity);

        //when
        int orderCount = 11;

        //then
        Assertions.assertThrows(NotStockException.class, () -> {
            orderService.order(member.getId(), book.getId(), orderCount);
        });

    }

    @Test
    @Transactional
    public void 상품취소() {

        //given
        Member member = createMember("회원1", new Address("서울","강가","123-123"));
        int quantity = 10;
        int price = 10000;
        Book book = createBook("시골 JPA", price, quantity);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "상품 취소시 상태는 CANCEL");
        assertEquals(quantity, book.getStockQuantity(), "주문 취소된 상품은 재고가 증가해야 한다.");

    }

    private Book createBook(String name, int price, int quantity){
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name, Address address){
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        em.persist(member);
        return member;
    }
}