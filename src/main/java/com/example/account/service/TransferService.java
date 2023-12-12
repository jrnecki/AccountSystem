package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.domain.Transfer;
import com.example.account.dto.TransactionDto;
import com.example.account.dto.TransferDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRespository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.account.type.TransactionResultType.S;
import static com.example.account.type.TransactionType.SEND;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountUserRespository accountUserRespository;
    @Transactional
    public TransferDto sendBalance(
            Long userId, String accountNumber, Long amount, String toAccountNumber) {
        // 보내는 사람
        AccountUser user = accountUserRespository.findById(userId)
                .orElseThrow(()->new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        // 받는 계좌
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(()-> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateSendBalance(user,account,amount,toAccount);
        account.useBalance(amount);
        toAccount.addBalance(amount);
        saveAndGetTransaction(SEND,S,account,amount);

        return TransferDto.fromEntity(
                Transfer.builder()
                        .fromAccount(account)
                        .amount(amount)
                        .toAccount(toAccount.getAccountNumber())
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-",""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }

    private void validateSendBalance
            (AccountUser user, Account account, Long amount, Account toAccount) {

        if(!Objects.equals(user.getId(), account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }
        if(account.getAccountStatus() != AccountStatus.IN_USE){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance() < amount){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        if(toAccount.getAccountStatus() != AccountStatus.IN_USE){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
    }

    private Transaction saveAndGetTransaction(
            TransactionType transactionType,
            TransactionResultType transactionResultType,
            Account account,
            Long amount) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-",""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }
}
