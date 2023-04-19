package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;

public class LongTest {
    @Test
    public void long_test() {
        // given
        Long number1 = 1111L;
        Long number2 = 1111L;

        // when
        if(number1.longValue() == number2.longValue()) {
            System.out.println("테스트 : 동일하다");
        } else {
            System.out.println("테스트 : 동일하지 않다");
        }

        Long amount1 = 100L;
        Long amount2 = 1000L;

        if(amount1 < amount2) {
            System.out.println("테스트 : 작습");
        } else {
            System.out.println("테스트 : 안작습");
        }
        // then
    }
}
