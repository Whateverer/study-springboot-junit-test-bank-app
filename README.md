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

## SecurityConfig Junit 테스트
스프링 시큐리티에 인증에 대한 예외는 authenticationEntryPoint가 가로챈다.
이것을 임의로 설정하기 위해서는 SecurityConfig에 다음과 같이 작성한다.
```java
    // Exception 가로채기
    http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(403);
        response.getWriter().println("error"); // 예쁘게 메시지를 포장하는 공통적인 응답 DTO를 만들어보자!!
    });
```

## 공통 DTO 만들기
응답을 처리하는 공통 DTO를 만들자.
```java
@RequiredArgsConstructor
@Getter
public class ResponseDto<T> {
    // final 이유 : 응답의 dto는 한번 만들어지면 수정될 이유가 없음.
    private final Integer code; // 1 성공, -1 실패
    private final String msg;
    private final T data;
}
```

# 스프링부트 회원가입 
## 회원가입 서비스 만들기
JPA의 QueryMethod 참고하기

예외처리하기 - CustomApiException이 발생하는 순간 아래의 apiException 메소드가 실행된다.
```java
    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiException(CustomApiException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1, e.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
```

## 회원가입 서비스 테스트
#### 테스트 시 Mockito의 MockitoExtension과 함께 테스트 하기.
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

}
```

#### 가짜 객체를 넣는 애노테이션과 진짜 객체를 넣는 애노테이션
- @InjectMocks : 가짜 객체 주입
- @Mock : 가짜 객체 생성
- @Spy : 진짜 객체 가져오기
```java
    @InjectMocks
    private UserService userService;

    @Mock // 실제로 띄울 필요X, 가짜로 메모리에 띄워서 위의 가짜 userService에 inject해준다.
    private UserRepository userRepository;

    @Spy // 진짜 passwordEncoder를 가져오는 것
    private BCryptPasswordEncoder passwordEncoder;
```
