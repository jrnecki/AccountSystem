package com.example.account.repository;

import com.example.account.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// spring에서 jpa를 쓰기쉽게 해주도록 만들어진 인터페이스
@Repository // bean에 등록
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Optional<Transaction> findByTransactionId(String transactionId);
}
