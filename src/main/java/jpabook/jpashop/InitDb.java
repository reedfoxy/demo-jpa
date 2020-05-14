package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }


    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public Member createMember(String name, Address address){
            Member member = new Member();
            member.setName(name);
            member.setAddress(address);
            em.persist(member);
            return member;
        }

        public Item createBook(String name, int price, int quantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(quantity);
            em.persist(book);
            return book;
        }

        public Order order(Member member, OrderItem... orderItems) {

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());

            Order order = Order.createOrder(member, delivery, orderItems);
            em.persist(order);
            return order;
        }
        public void dbInit1(){

            Member member = createMember("userA", new Address("서울", "1", "1111"));

            Item book = createBook("JPA1 BOOK", 10000, 100);
            Item book2 = createBook("JPA2 BOOK", 20000, 100);

            OrderItem orderItem = OrderItem.createOrderItem(book, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            order(member, orderItem, orderItem2);
        }

        public void dbInit2(){

            Member member = createMember("userB", new Address("서울", "2", "22222"));

            Item book = createBook("Spring1 BOOK", 20000, 200);
            Item book2 = createBook("Spring2 BOOK", 40000, 300);

            OrderItem orderItem = OrderItem.createOrderItem(book, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            order(member, orderItem, orderItem2);
        }
    }
}