package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class RegexTest {

    @Test
    public void 한글만된다() {
        String value = "한글";
        boolean result = Pattern.matches("^[가-힣]+$", value);
        System.out.println("테스트 : " + result);
    }

    @Test
    public void 한글은안된다() {

    }

    @Test
    public void 영어만된다() {

    }
    @Test
    public void 영어는안된다() {

    }
    @Test
    public void 영어와숫자만된다() {

    }
    @Test
    public void 영어만되고_길이는최소2최대4이다() {

    }

    // username, email, fullname
}
