package com.example.account.controller;

import com.example.account.aop.Accountlock;
import com.example.account.dto.SendBalance;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.service.TransactionService;
import com.example.account.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;

    @PostMapping("/transfer/send")
    @Accountlock
    public SendBalance.Response sendBalance(
            @Valid @RequestBody SendBalance.Request request
    ) throws InterruptedException {
        try{
            Thread.sleep(5000L);
            return SendBalance.Response.from(
                    transferService.sendBalance(
                            request.getUserId(),
                            request.getAccountNumber(),
                            request.getAmount(),
                            request.getToAccountNumber()
                    )
            );

        }catch(AccountException | InterruptedException e){
            log.error("Failed to send balance");
            transactionService.saveFaileUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e;
        }

    }
}
