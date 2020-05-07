package jpabook.jpashop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class JpashopApplicationTests {


	@Test
	@Rollback(false)
	void contextLoads() {

	}

}
