package com.example.Account.repository;

import com.example.Account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// spring에서 jpa를 쓰기쉽게 해주도록 만들어진 인터페이스
@Repository // bean에 등록
public interface AccountRepository extends JpaRepository<Account,Long> {
    // 자동으로 쿼리 생성
    Optional<Account> findFirstByOrderByIdDesc(); // 맨 첫번째 값, id 내림차순으로

}
