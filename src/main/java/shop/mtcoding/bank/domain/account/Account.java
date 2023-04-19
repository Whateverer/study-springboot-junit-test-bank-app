package shop.mtcoding.bank.domain.account;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor // 스프링이 User 객체생성할 때 빈생성자로 new를 하기 때문!!
@Getter
@EntityListeners(AuditingEntityListener.class) // createDate, LastModifiedDate를 동작하게 해줌
@Table(name = "account_tb")
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 4)
    private Long number; // 계좌번호
    @Column(nullable = false, length = 4)
    private Long password; // 계좌비번
    @Column(nullable = false)
    private Long balance; // 잔액 (기본값:1000원)

    // 항상 ORM에서 fk의 주인은 Many Entity 쪽이다.
    @ManyToOne(fetch = FetchType.LAZY) // account.getUser().아무필드호출() == Lazy 발동
    private User user; // user_id

    @CreatedDate // Insert 시 날짜가 자동으로 들어감
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate // Insert, Update 시 날짜가 자동으로 들어감
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Account(long id, long number, long password, long balance, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void checkOwner(Long userId) {
        /*String testUsername = user.getUsername(); // lazy 로딩이 되어야 함.
        System.out.println("테스트 : " + testUsername);*/
        if(user.getId() != userId) { // lazy 로딩이어도 id를 조회할 때는 select 쿼리가 날라기자 않는다.
            throw new CustomApiException("계좌 소유자가 아닙니다.");
        }
    }

    public void deposit(Long amount) {
        this.balance += amount;
    }

    public void checkSamePassword(Long password) {
        if(this.password.longValue() != password.longValue()) {
            throw new CustomApiException("계좌 비밀번호 검증에 실패했습니다.");
        }
    }

    public void checkBalance(Long amount) {
        if(this.balance < amount) {
            throw new CustomApiException("계좌 잔액이 부족합니다.");
        }
    }

    public void withdraw(Long amount) {
        checkBalance(amount);
        this.balance -= amount;
    }
}
