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

## 회원가입 서비스 코드 리팩토링
static으로 만들어두었던 JoinReqDto, JoinRespDto를 UserReqDto, UserRespDto를 만들어 각각에 옮겨준다.
또, test시 마다 User객체를 만들어내는 것이 번거로우니 DummyObject class를 만들어 UserServiceTest에 상속시킨다.
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest extends DummyObject {
    User ssar = newMockUser(1L, "ssar", "쌀");
}

public class DummyObject {
    protected User newMockUser(Long id, String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username + "@sdfs.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }    
}
```

## 회원가입 컨트롤러 만들기
Spring에서 파라미터에 RequestBody를 붙이지 않으면 기본 전략은 x-www-form-urlencoded이기 때문에, 우리는 json 형태로 파라미터를 받을거라 @RequestBody를 붙여준다.

유효성검사 - parameter에 @Valid를 붙이고 해당 parameter 클래스에 유효성 검사를 걸어준다. 유효성 검사 실패 시 에러는 BindResult 객체로 받아준다.
```java
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid JoinReqDto joinReqDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(new ResponseDto<>(-1, "유효성 검사 실패", errorMap), HttpStatus.BAD_REQUEST);
        }

        JoinRespDto joinRespDto = userService.회원가입(joinReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "회원가입 성공", joinRespDto), HttpStatus.CREATED);
    }
```
```java
public static class JoinReqDto {
    // 유효성 검사
    @NotEmpty // null이거나, 공백일 수 없다.
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String email;
    @NotEmpty
    private String fullname;
}
```

## 회원가입 컨트롤러 유효성검사 AOP 적용
#### AOP : 관점 지향 프로그래밍, **관심사를 분리시킨다**  
- PointCut : 공통으로 들어갈 메서드의 위치 
- Advise : 구현할 코드는 무엇인가
- JoinPoint : PointCut이 위치할 각각의 메서드

get, delete, post(body), put(body) => Body가 있는 post와 put 메서드에 유효성검사 AOP를 적용한다.

1. 먼저 적용할 AOP의 클래스를 만든 후, @Component (Spring에 등록), @Aspect (AOP 사용) 애노테이션을 추가한다.
```java
@Component
@Aspect
public class CustomValidationAdvice {
    
}
```
2. Pointcut을 지정해준다. (PostMapping과 PutMapping 애노테이션에 지정)
```java
@Component
@Aspect
public class CustomValidationAdvice {
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping() {}
}
```
3. AOP에는 Before, After, Around 세 가지가 있다.
- Before : 해당 메서드 전에 실행 / After : 해당 메서드 후에 실행
- Around : joinPoint의 전후 제어 가능
```java
    @Around("postMapping() || putMapping()") // postMapping과 putMapping에 AOP적용하겠다. / joinPoint의 전후 제어
    public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs(); // joinPoint의 매개변수
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult bindingResult = (BindingResult) arg;

                if(bindingResult.hasErrors()) {
                    Map<String, String> errorMap = new HashMap<>();

                    for (FieldError error : bindingResult.getFieldErrors()) {
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }
                    throw new CustomValidationException("유효성검사 실패", errorMap);
                }
            }
        }
        return proceedingJoinPoint.proceed(); // 정상적으로 해당 메서드를 실행해라!
    }
```
4. joinPoint 제어 가능한 Around로 postMapping이나 putMapping이 실행될 때 BindingResult에 error가 있다면 유효성검사 Exception을 던져준다.

# 스프링부트 JWT 인증과 인가
## Jwt 토큰 생성을 위한 세팅
username과 password를 json으로 서버에 전달   
HS256이면 대칭키로 서버에서만 키를 들고있으면 된다.

스프링 시큐리티 적용한 로그인
1. LoginUser 생성 (UserDetails를 implement 후 사용에 맞게 수정)
2. LoginService 생성 (UserDetailsService를 implement) 후, loadUserByUsername(username) 메서드를 override
3. loadUserByUsername(username)의 리턴값은 LoginUser로 세팅

JWT 토큰 프로세스 로직
1. JwtVO (interface) 생성
```java
public interface JwtVO {
    public static final String SECRET = "메타코딩"; // HS256
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 *24 * 7; // 일주일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
}
```
2. JwtProcess.java 생성 - 토큰을 생성하고 검증하는 로직
```java
public class JwtProcess {
    private final Logger log = LoggerFactory.getLogger(getClass());
    // 토큰 생성
    public static String create(LoginUser loginUser) {
        String jwtToken = JWT.create()
                .withSubject("bank")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME))
                .withClaim("id", loginUser.getUsername())
                .withClaim("role", loginUser.getUser().getRole() + "")
                .sign(Algorithm.HMAC512(JwtVO.SECRET));
        return JwtVO.TOKEN_PREFIX+jwtToken;
    }

    // 토큰 검증 (return 되는 LoginUser 객체를 강제로 시큐리티 세션에 직접 주입할 예정)
    public static LoginUser verify(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtVO.SECRET)).build().verify(token);
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString();
        User user = User.builder().id(id).role(UserEnum.valueOf(role)).build();
        LoginUser loginUser = new LoginUser(user);
        return loginUser;
    }

}
```