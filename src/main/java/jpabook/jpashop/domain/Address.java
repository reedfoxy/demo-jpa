package jpabook.jpashop.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address () {} // Embeddable 은 기본 생성자 로 생성 금지
}
