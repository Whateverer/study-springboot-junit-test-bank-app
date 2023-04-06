package shop.mtcoding.bank.domain.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mtcoding.bank.domain.user.User;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
