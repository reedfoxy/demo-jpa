package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberTest {

    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    void testMember(){
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Member findMember = memberRepository.save(member);

        //then
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }
}