package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRespository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.account.type.TransactionResultType.*;
import static com.example.account.type.TransactionType.*;

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
    public TransactionDto useBalance(
            Long userId, String accountNumber, Long amount,
            String toAccountNumber, String bank,
            String transactionMessage) {

        // 보내는 사람
        AccountUser fromUser = accountUserRespository.findById(userId)
                .orElseThrow(()->new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        // 받는 계좌
        Account toAccount = accountRepository.
                findByAccountNumberAndBank(toAccountNumber,Bank.valueOf(bank))
                .orElseThrow(()->new AccountException(ErrorCode.USER_NOT_FOUND));

        log.info("상대이름: "+toAccount.getAccountUser().getName());

        account.useBalance(amount);
        toAccount.addBalance(amount);
        saveAndGetTransaction(SEND,S,account,amount,transactionMessage);

        return TransactionDto.fromEntity(
                Transaction.builder()
                        .account(account)
                        .amount(amount)
                        .toAccount(toAccount.getAccountNumber())
                        .transactionMessage(transactionMessage)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-",""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }



    private void validateUseBalance(AccountUser user, Account account,
                                    Long amount, Account toAccount, String bank) {
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
    @Transactional
    public void saveFaileUseTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(USE,F,account,amount,"");
    }
    private  Transaction saveAndGetTransaction(
            TransactionType transactionType,
            TransactionResultType transactionResultType,
            Account account,
            Long amount, String transactionMessage) {
            return transactionRepository.save(
                    Transaction.builder()
                            .transactionType(transactionType)
                            .transactionResultType(transactionResultType)
                            .account(account)
                            .amount(amount)
                            .transactionMessage(transactionMessage)
                            .balanceSnapshot(account.getBalance())
                            .transactionId(UUID.randomUUID().toString().replace("-",""))
                            .transactedAt(LocalDateTime.now())
                            .build()
            );
    }

    @Transactional
    public TransactionDto cancelBalance(
            String transactionId, String accountNumber, Long amount) {
        Transaction trasaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateCancelBalance(trasaction,account,amount);
        account.cancelBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(CANCEL,S,account,amount,""));
    }

    private void validateCancelBalance(Transaction trasaction, Account account, Long amount) {
        if(!Objects.equals(trasaction.getAccount().getId(),account.getId())){
            throw new AccountException(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH);
        }
        if(!Objects.equals(trasaction.getAmount(), amount)){
            throw new AccountException(ErrorCode.CANCEL_MUST_FULLY);
        }
        if(trasaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))){
            throw new AccountException(ErrorCode.TOO_OLD_ORDER_TO_CANCEL);
        }
    }

    @Transactional
    public void saveFaileCancelTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(CANCEL,F,account,amount,"");
    }

    public TransactionDto queryTransaction(String transactionId) {
        return TransactionDto.fromEntity(transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND))
        );
    }
}
