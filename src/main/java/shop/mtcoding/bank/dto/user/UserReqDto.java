package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class UserReqDto {
    @Getter
    @Setter
    public static class JoinReqDto {
        // 유효성 검사
        // 영문, 숫자는 되고, 길이 최소 2~20자 이내
        @Pattern(regexp = "", message = "영문/숫자 2~20자 이내로 작성해주세요")
        @NotEmpty // null이거나, 공백일 수 없다.
        private String username;
        // 길이 4~20
        @NotEmpty
        private String password;
        // 이메일형식
        @NotEmpty
        private String email;
        // 영어, 한글, 1~20
        @NotEmpty
        private String fullname;

        // dto 값으로 entity를 바로 만들어낼 수 있다.
        public User toEntity(BCryptPasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
