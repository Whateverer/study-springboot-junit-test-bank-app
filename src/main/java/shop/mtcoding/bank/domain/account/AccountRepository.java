package shop.mtcoding.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // jpa query method
    // select * from account where number =: number
    // TODO : 리팩토링 해야함!!
    Optional<Account> findByNumber(Long number);
}
