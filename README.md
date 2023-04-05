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

## Transaction 엔티티 생성

# 스프링부트 시큐리티 세팅
## SecurityConfig 기본 설정
     
1. config class를 만들어 @Configuration 애노테이션을 붙여준다.(@Configuration이 붙어있는 class의 bean만 작동한다.)   
2. BCryptPasswordEncoder을 빈으로 등록해준다.
3. SecurityFilterChain을 만들어 http Security 설정을 해준다.
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.headers().frameOptions().disable(); // iframe 허용안함.
        http.csrf().disable(); // enable이면 post맨 작동 안함 (메타코딩 유튜브에 시큐리티 강의)
        http.cors().configurationSource(configurationSource()); // 자바스크립트로 요청되는 api는 막겠다.

        // jSessionId를 서버쪽에서 관리 안하겠다는 뜻!
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // react, 앱으로 요청할 예정
        http.formLogin().disable();
        // httpBasic은 브라우저가 팝업창을 이용해서 사용자 인증을 진행한다.
        http.httpBasic().disable();
        http.authorizeRequests()
        .antMatchers("/api/s/**").authenticated()
        .antMatchers("api/admin/**").hasRole(""+ UserEnum.ADMIN) // 최근 공식문서에서는 ROLE_ 안붙여도 됨
        .anyRequest().permitAll();

        return http.build();
        }
```
4. CorsConfigurationSource 설정을 해준다.
```java
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader(("*")); // 모든 header를 받겠다.
        configuration.addAllowedMethod(("*")); // 모든 method를 받겠다. GET, POST, PUT, DELETE (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용 (프론트엔드 IP만 허용 React)
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
```
