package com.example.Account.controller;

import com.example.Account.domain.Transaction;
import com.example.Account.dto.TransactionDto;
import com.example.Account.dto.UseBalance;
import com.example.Account.exception.AccountException;
import com.example.Account.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {
    private  final TransactionService transactionService;
    @PostMapping("/transaction/use")
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    ){
        try{
            return UseBalance.Response.from(transactionService.useBalance(
                    request.getUserId(), request.getAccountNumber(), request.getAmount()
            ));
        }catch(AccountException e){
            log.error("Failed to use balance.");
            transactionService.saveFaileUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e;
        }

    }
}
