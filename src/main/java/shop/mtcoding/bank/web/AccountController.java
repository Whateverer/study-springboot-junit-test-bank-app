package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.service.AccountService;

import javax.validation.Valid;

import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;
import static shop.mtcoding.bank.service.AccountService.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountSaveReqDto accountSaveReqDto,
                                         BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {
        AccountSaveRespDto accountSaveRespDto = accountService.계좌등록(accountSaveReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌등록 성공", accountSaveRespDto), HttpStatus.CREATED);
    }

    // 인증이 필요하고, account 테이블에 1번 row를 주세요!!
    // 인증이 필요하고, account 테이블 데이터 다 주세요!!
    // 권한 처리 때문에 선호하지 않는다.

    // 인증이 필요하고, account 테이블에 login한 유저의 계좌만 주세요.
    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser) {
        AccountListRespDto accountRespDto = accountService.계좌목록보기_유저별(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }

    @DeleteMapping("/s/account/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long number, @AuthenticationPrincipal LoginUser loginUser) {
        accountService.계좌삭제(number, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 완료", null), HttpStatus.OK);
    }

    @PostMapping("/account/deposit")
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountDepositReqDto accountDepositReqDto, BindingResult bindingResult) {
        AccountDepositRespDto accountDepositRespDto = accountService.계좌입금(accountDepositReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료", accountDepositRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public ResponseEntity<?> withdrawAccount(@RequestBody @Valid AccountWithdrawReqDto accountWithdrawReqDto,
                                             BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {
        AccountWithdrawRespDto accountDepositReqDto = accountService.계좌출금(accountWithdrawReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 출금 완료", accountDepositReqDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/transfer")
    public ResponseEntity<?> transferAccount(@RequestBody @Valid AccountTransferReqDto accountTransferReqDto,
                                             BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {
        AccountTransferRespDto accountTransferRespDto = accountService.계좌이체(accountTransferReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 이체 완료", accountTransferRespDto), HttpStatus.CREATED);
    }
}
