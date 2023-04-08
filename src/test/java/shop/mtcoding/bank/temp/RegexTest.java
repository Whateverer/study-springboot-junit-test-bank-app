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
        String value = "abc";
        boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]*$", value); // ^: 안에 있으면 not의 의미
        System.out.println("테스트 : " + result);
    }

    @Test
    public void 영어만된다() {
        String value = "ssar";
        boolean result = Pattern.matches("^[a-zA-Z]+$", value); // ^: 안에 있으면 not의 의미
        System.out.println("테스트 : " + result);
    }
    @Test
    public void 영어는안된다() {
        String value = "가22";
        boolean result = Pattern.matches("^[^a-zA-Z]*$", value); // ^: 안에 있으면 not의 의미
        System.out.println("테스트 : " + result);
    }
    @Test
    public void 영어와숫자만된다() {
        String value = "ssar2323";
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value); // ^: 안에 있으면 not의 의미
        System.out.println("테스트 : " + result);

    }
    @Test
    public void 영어만되고_길이는최소2최대4이다() {
        String value = "ssar";
        boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value); // ^: 안에 있으면 not의 의미
        System.out.println("테스트 : " + result);
    }

    // username, email, fullname
    // 영문, 숫자는 되고, 길이 최소 2~20자 이내
    @Test
    public void user_username_test() {
        String username = "ssar가";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,20}$", username);
        System.out.println("테스트 : " + result);
    }

    @Test
    public void user_fullname_test() {
        String fullname = "쌀낳g3";
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", fullname);
        System.out.println("테스트 : " + result);
    }

    @Test
    public void user_email_test() {
        String email = "ssar@nate.com";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,6}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", email);
        System.out.println("테스트 : " + result);
    }

}