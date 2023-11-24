package com.example.Account.service;

import com.example.Account.domain.Account;
import com.example.Account.type.AccountStatus;
import com.example.Account.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock // 가짜로 만들어줌.
    private AccountRepository accountRepository;

    @InjectMocks // 위에서 만든 mock을 주입시켜줌.
    private AccountService accountService;

    @Test
    @DisplayName("계좌 조회 성공")
    void testSuccessToSearchAccount() {
        // given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .accountStatus(AccountStatus.UNREGISTERED)
                                .accountNumber("5667")
                                .build()
                ));
        // Long타입의 박스를 만들어줌.
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        // when
        Account account = accountService.getAccount(5667L);
        // then
        verify(accountRepository, times(1))
                .findById(captor.capture()); // captor로 그 값을 캡쳐해버리겠다.
        verify(accountRepository, times(0))
                .save(any());
        assertEquals(5667L,captor.getValue());
        assertEquals("5667",account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    @DisplayName("계좌 조회 실패 - 음수로 조회")
    void testFailedToSearchAccount() {
        // given
        // when
        RuntimeException exception =
        assertThrows(RuntimeException.class,
                ()-> accountService.getAccount(-10L));
        // then
        assertEquals("Minus",exception.getMessage());
    }
}