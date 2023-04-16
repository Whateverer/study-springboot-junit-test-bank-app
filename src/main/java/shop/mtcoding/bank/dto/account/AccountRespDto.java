package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountRespDto {
    @Getter
    @Setter
    public static class AccountSaveRespDto {
        private Long id;
        private Long number;
        private Long balance;

        public AccountSaveRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }

    @Getter
    @Setter
    public static class AccountListRespDto {
        private String fullname;
        private List<AccountDto> accounts = new ArrayList<>();

        public AccountListRespDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
//            this.accounts = accounts.stream().map((account) -> new AccountDto(account)).collect(Collectors.toList());
            this.accounts = accounts.stream().map((AccountDto::new)).collect(Collectors.toList());
            // [account, account]
        }

        @Getter
        @Setter
        public class AccountDto {
            private Long id;
            private Long password;
            private Long balance;

            // Entity 객체를 Dto로 옮기는 작업
            // 이유 : Entity를 Controller로 넘기면 json이 모든 필드에 대한 getter를 생성, lazy loading이 생길 수 있어서
            public AccountDto(Account account) {
                this.id = account.getId();
                this.password = account.getPassword();
                this.balance = account.getBalance();
            }
        }
    }
}
