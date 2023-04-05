# study-springboot-junit-test-bank-app
인프런 강의 스프링부트 JUnit 테스트 - 시큐리티를 활용한 Bank 애플리케이션 정리

# 스프링부트 프로젝트 생성
## 테이블 생성
user_tb (유저)    
account_tb (계좌)     
transaction_tb (거래내역)

## User 엔티티 생성
### Jpa LocalDateTime 자동으로 생성하는 법
- @EnableJpaAuditing (Main 클래스)
- @EntityListeners(AuditingEntityListener.class) (Entity 클래스)
```java
@CreatedDate // Insert 시 날짜가 자동으로 들어감
@Column(nullable = false)
private LocalDateTime createdAt;

@LastModifiedDate // Insert, Update 시 날짜가 자동으로 들어감
@Column(nullable = false)
private LocalDateTime updatedAt;
```

## Account 엔티티 생성
항상 ORM에서 fk의 주인은 Many Entity 쪽이다.
(User(One) <-> Account(Many))
```java
public class Account {
    // 필드들... 
    @ManyToOne(fetch = FetchType.LAZY) // account.getUser().아무필드호출() == Lazy 발동
    private User user; // user_id    
}
```