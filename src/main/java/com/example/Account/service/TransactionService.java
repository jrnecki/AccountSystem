package com.example.Account.service;

import com.example.Account.domain.Account;
import com.example.Account.domain.AccountUser;
import com.example.Account.domain.Transaction;
import com.example.Account.dto.AccountDto;
import com.example.Account.dto.TransactionDto;
import com.example.Account.exception.AccountException;
import com.example.Account.repository.AccountRepository;
import com.example.Account.repository.AccountUserRespository;
import com.example.Account.repository.TransactionRepository;
import com.example.Account.type.AccountStatus;
import com.example.Account.type.ErrorCode;
import com.example.Account.type.TransactionResultType;
import com.example.Account.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.Account.type.TransactionResultType.*;
import static com.example.Account.type.TransactionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private  final TransactionRepository transactionRepository;
    private final AccountUserRespository accountUserRespository;
    private final AccountRepository accountRepository;

    /**
     *
     * 사용자가 없는 경우, 계좌가 없는 경우, 사용자 아이디와 계좌 소유주가 다른 경우,
     * 계좌가 이미 해지 상태인 경우, 거래금액이 잔액보다 큰 경우,
     * 거래 금액이 너무 작거나 큰 경우 실패 응답
     */
    @Transactional
    public TransactionDto useBalance
            (Long userId, String accountNumber, Long amount){
        AccountUser user = accountUserRespository.findById(userId)
                .orElseThrow(()-> new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        validateUseBalance(user,account,amount);

        account.useBalance(amount);

        return TransactionDto.fromEntity(transactionRepository.save(
                saveAndGetTransaction(S,account,amount)
        ));

    }


    private void validateUseBalance(AccountUser user, Account account, Long amount) {
        if(!Objects.equals(user.getId(), account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }
        if(account.getAccountStatus() != AccountStatus.IN_USE){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance() < amount){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
    }
    @Transactional
    public void saveFaileUseTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(F,account,amount);
    }
    private  Transaction saveAndGetTransaction(
            TransactionResultType transactionResultType, Account account, Long amount) {
            return transactionRepository.save(
                    Transaction.builder()
                            .transactionType(USE)
                            .transactionResultType(transactionResultType)
                            .account(account)
                            .amount(amount)
                            .balanceSnapshot(account.getBalance())
                            .transactionalId(UUID.randomUUID().toString().replace("-",""))
                            .transactedAt(LocalDateTime.now())
                            .build()
            );
    }

}
