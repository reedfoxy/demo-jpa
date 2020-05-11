package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    // 저장
    public void save(Order order) {
        em.persist(order);
    }

    // 하나 조회
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 검색 조회
    public List<Order> findAll(OrderSearch orderSearch) {

        return em.createQuery("select o from  Order o join o.member m where o.status = :status and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000) // 최대 1000건
                .getResultList();
    }
}