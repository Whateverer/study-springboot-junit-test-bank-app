package shop.mtcoding.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountRespDto {

    @Getter
    @Setter
    public static class AccountTransferRespDto {
        private Long id; // 계좌 ID
        private Long number; // 계좌번호
        private Long balance;
        private TransactionDto transaction;

        public AccountTransferRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.balance = account.getBalance();
            this.number = account.getNumber();
            this.transaction = new TransactionDto(transaction); // Controller 단에 Entity를 노출하면 안된다.
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;
            @JsonIgnore
            private Long depositAccountBalance;
            private String createAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.createAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    // DTO가 똑같아도 재사용하지 않기 (나중에 만약에 출금할 때 뭔가 조금 DTO가 달라져야 하면 DTO를 공유하면 수정 잘못하면 망한다 - 독립적으로 만들기)
    @Getter
    @Setter
    public static class AccountWithdrawRespDto {
        private Long id; // 계좌 ID
        private Long number; // 계좌번호
        private Long balance;
        private TransactionDto transaction;

        public AccountWithdrawRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.balance = account.getBalance();
            this.number = account.getNumber();
            this.transaction = new TransactionDto(transaction); // Controller 단에 Entity를 노출하면 안된다.
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;
            private String createAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    @Setter
    public static class AccountDepositRespDto {
        private Long id; // 계좌 ID
        private Long number; // 계좌번호
        private TransactionDto transaction;

        public AccountDepositRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transaction = new TransactionDto(transaction); // Controller 단에 Entity를 노출하면 안된다.
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;
            private String tel;
            private String createAt;
            @JsonIgnore
            private Long depositAccountBalance; // 클라이언트에게 전달X - 서비스단에서 테스트 용도

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

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
    @Getter
    @Setter
    public static class AccountDetailRespDto {
        private Long id;
        private Long number;
        private Long balance;
        private List<TransactionDto> transactions = new ArrayList<>();

        public AccountDetailRespDto(Account account, List<Transaction> transactions) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transactions = transactions.stream()
                    .map((transaction) -> new TransactionDto(transaction, account.getNumber()))
                    .collect(Collectors.toList());
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private Long amount;

            private String sender;
            private String receiver;

            private String tel;
            private String createAt;
            private Long balance;

            public TransactionDto(Transaction transaction, Long accountNumber) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.amount = transaction.getAmount();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.createAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
                this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();

                if(transaction.getDepositAccount() == null) {
                    this.balance = transaction.getWithdrawAccountBalance();
                } else if (transaction.getWithdrawAccount() == null){
                    this.balance = transaction.getDepositAccountBalance();
                } else {
                    // 1111 계좌의 입출금 내역 (출금계좌 = 값, 입금계좌 = 값)
                    if(accountNumber.longValue() == transaction.getDepositAccount().getNumber()) {
                        this.balance = transaction.getDepositAccountBalance();
                    } else {
                        this.balance = transaction.getWithdrawAccountBalance();
                    }
                }
            }
        }
    }
}
