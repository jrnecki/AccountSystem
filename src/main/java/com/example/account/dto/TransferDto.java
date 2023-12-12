package com.example.account.dto;

import com.example.account.domain.Account;
import com.example.account.domain.Transaction;
import com.example.account.domain.Transfer;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferDto {
    private String accountNumber;
    private TransactionType transactionType;
    private TransactionResultType transactionResultType;
    private Account account;
    private Long amount;
    private String toAccountNumber;
    private Long balanceSnapshot;
    private String transactionId;
    private LocalDateTime transactedAt;

    public static TransferDto fromEntity(Transfer transfer){
        return TransferDto.builder()
                .accountNumber(transfer.getFromAccount().getAccountNumber())
                .transactionType(transfer.getTransactionType())
                .transactionResultType(transfer.getTransactionResultType())
                .amount(transfer.getAmount())
                .toAccountNumber(transfer.getToAccount())
                .balanceSnapshot(transfer.getBalanceSnapshot())
                .transactionId(transfer.getTransactionId())
                .transactedAt(transfer.getTransactedAt())
                .build();

    }
}
