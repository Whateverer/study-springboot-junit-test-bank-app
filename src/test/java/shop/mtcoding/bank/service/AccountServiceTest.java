package shop.mtcoding.bank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;
import static shop.mtcoding.bank.service.AccountService.*;

@ExtendWith(MockitoExtension.class) // 전체 환경이 필요없기 때문에 mockito로 생성
class AccountServiceTest extends DummyObject {
    @InjectMocks // 모든 Mock들이 InjectMocks로 주입됨
    private AccountService accountService;

    @Mock // Mock 애노테이션 붙여진 것들이 @InjectMocks쪽으로 주입된다.
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Spy // 진짜 객체를 InjectMocks에 주입한다.
    private ObjectMapper om;

    @Test
    public void 계좌등록_test() throws JsonProcessingException {
        // given
        Long userId = 1L;

        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(1111L);
        accountSaveReqDto.setPassword(1234L);

        // stub 필요 (accountService에서 DB를 거치는 로직에 대한 stub)

        // stub 1
        User ssar = newMockUser(userId, "ssar", "쌀");
        when(userRepository.findById(any())).thenReturn(Optional.of(ssar));

        // stub 2
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        // stub 3
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.save(any())).thenReturn(ssarAccount);

        // when
        AccountSaveRespDto accountSaveRespDto = accountService.계좌등록(accountSaveReqDto, userId);
        String responseBody = om.writeValueAsString(accountSaveRespDto);
        System.out.println("테스트 : " + responseBody);

        // then
        assertThat(accountSaveRespDto.getNumber()).isEqualTo(1111L);
    }

    @Test
    void 계좌목록보기_test() {
        // given

        // when

        // then
    }

    @Test
    void 계좌삭제_test() {
        // given
        Long number = 1111L;
        Long userId = 2L;

        // stub
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount));

        // when
//        accountService.계좌삭제(number, userId);

        // then
        assertThrows(CustomApiException.class, () -> accountService.계좌삭제(number, userId));
    }

    // Account -> balance 변경됐는지
    // Transaction -> balance 잘 기록됐는지
    @Test
    void 계좌입금_test() {
        // given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01088887777");

        // stub 1
        User ssar = newMockUser(1L, "ssar", "쌀"); // 실행됨
        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar); // 실행됨 - ssarAccount1 -> 1000원
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount1)); // 실행안됨 -> service 호출 뒤 실행됨

        // stub 2 (스텁이 진행될 때 마다 연관된 객체는 새로 만들어서 주입하기 - 타이밍 때문에 꼬인다.)
        Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar);
        Transaction transaction = newMockDepositTransaction(1L, ssarAccount2); // 실행됨 (ssarAccount1 -> 1100원)
        when(transactionRepository.save(any())).thenReturn(transaction); // 실행안됨

        // when
        AccountDepositRespDto accountDepositRespDto = accountService.계좌입금(accountDepositReqDto);

        // then
        assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);
        assertThat(accountDepositRespDto.getTransaction().getDepositAccountBalance()).isEqualTo(1100L);
    }

    @Test
    void 계좌입금_test2() throws JsonProcessingException {
        // given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01088887777");

        // stub 1
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount1));

        // stub 2 (스텁이 진행될 때 마다 연관된 객체는 새로 만들어서 주입하기 - 타이밍 때문에 꼬인다.)
        User ssar2 = newMockUser(1L, "ssar2", "쌀");
        Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar2);
        Transaction transaction = newMockDepositTransaction(1L, ssarAccount2);
        when(transactionRepository.save(any())).thenReturn(transaction);

        // when
        AccountDepositRespDto accountDepositRespDto = accountService.계좌입금(accountDepositReqDto);
        String responseBody = om.writeValueAsString(accountDepositRespDto);
        System.out.println("테스트 : " + responseBody);

        // then
        assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);

    }

    // 서비스 테스트를 보여준 것은 기술적인 테크닉!
    // 진짜 서비스를 테스트 하고 싶으면, 내가 지금 무엇을 여기서 테스트해야할지 명확히 구분 (책임 분리)
    // DTO를 만드는 책임 -> 서비스에 있지만!! (서비스에서 DTO 검증 안할래!! - Controller 테스트 해볼 것이니까)
    // DB 관련된 것도 -> 서비스 것이 아니야... 불필요해
    
    // DB 스텁, DB 스텁 (가짜로 DB 만들어서 deposit 검증... 0원 검증)
    @Test
    void 계좌입금_test3() {
        // given
        Account account = newMockAccount(1L, 1111L, 1000L, null);
        Long amount = 100L;

        // when
        if(amount <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }

        account.deposit(100L);

        // then
        assertThat(account.getBalance()).isEqualTo(1100L);
    }
    
    // 계좌 출금_테스트 (서비스)
    
    // 계좌 이체_테스트 (서비스)
    
    // 계좌목록보기_유저별_테스트 (서비스)
    
    // 계좌상세보기_테스트 (서비스)
}