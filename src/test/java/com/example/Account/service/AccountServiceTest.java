package com.example.Account.service;

import com.example.Account.domain.Account;
import com.example.Account.domain.AccountUser;
import com.example.Account.dto.AccountDto;
import com.example.Account.exception.AccountException;
import com.example.Account.repository.AccountUserRespository;
import com.example.Account.type.AccountStatus;
import com.example.Account.repository.AccountRepository;
import com.example.Account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock // 가짜로 만들어줌.
    private AccountRepository accountRepository;
    @Mock
    private AccountUserRespository accountUserRespository;

    @InjectMocks // 위에서 만든 mock을 주입시켜줌.
    private AccountService accountService;

    @Test
    void createAccountSuccess(){
        // given
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .id(12L)
                .build();

        given(accountUserRespository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountNumber("100000012")
                        .build()));
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("100000013")
                        .build());

        ArgumentCaptor<Account>captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto accountDto = accountService.createAccount(1L,1000L);
        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L,accountDto.getUserId());
        assertEquals("100000013",accountDto.getAccountNumber());

    }

    @Test
    void createAccountFirst(){
        // given
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .id(15L)
                .build();

        given(accountUserRespository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("100000015")
                        .build());

        ArgumentCaptor<Account>captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto accountDto = accountService.createAccount(1L,1000L);
        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(15L,accountDto.getUserId());
        assertEquals("1000000000",captor.getValue().getAccountNumber());

    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void createAccount_UserNotFound(){
        // given
        given(accountUserRespository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException = assertThrows(AccountException.class,
                ()-> accountService.createAccount(1L,1000L));
        // then
        assertEquals(ErrorCode.USER_NOT_FOUND,accountException.getErrorCode());

    }

    @Test
    @DisplayName(("유저 당 최대 계좌는 10개"))
    void createAccount_maxAccountIs10(){
        // given
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("Pobi").build();
        given(accountUserRespository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);
        // when
        AccountException exception = assertThrows(AccountException.class,
                ()->accountService.createAccount(1L,1000L));
        // then
        assertEquals(ErrorCode.MAX_COUNT_PER_USER,exception.getErrorCode());
    }

    @Test
    void deleteAccountSuccess(){
        // given
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .id(12L)
                .build();

        given(accountUserRespository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(0L)
                        .accountNumber("100000012")
                        .build()));
        ArgumentCaptor<Account>captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto accountDto = accountService.deleteAccount(1L,"1234567890");
        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L,accountDto.getUserId());
        assertEquals("100000012",captor.getValue().getAccountNumber());;
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
    }
    @Test
    @DisplayName("해당 유저 없음 - 계좌 해지 실패")
    void deleteAccount_UserNotFound(){
        // given
        given(accountUserRespository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException = assertThrows(AccountException.class,
                ()-> accountService.deleteAccount(1L,"1234567890"));
        // then
        assertEquals(ErrorCode.USER_NOT_FOUND,accountException.getErrorCode());

    }
    @Test
    @DisplayName("해당 계좌 없음 - 계좌 해지 실패")
    void deleteAccount_AccountNotFound(){
        // given
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .id(12L)
                .build();
        given(accountUserRespository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException = assertThrows(AccountException.class,
                ()-> accountService.deleteAccount(1L,"1234567890"));
        // then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND,accountException.getErrorCode());

    }
    @Test
    @DisplayName("사용자와 카드 소유주 불일치")
    void deleteAccountFailed_userUnMatch(){
        // given
        AccountUser pobi = AccountUser.builder()
                .name("Pobi")
                .id(12L)
                .build();

        AccountUser harry = AccountUser.builder()
                .name("Harry")
                .id(13L)
                .build();
        given(accountUserRespository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(harry)
                        .balance(0L)
                        .accountNumber("100000012")
                        .build()));

        // when
        AccountException accountException = assertThrows(AccountException.class,
                ()-> accountService.deleteAccount(1L,"1234567890"));
        // then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH,accountException.getErrorCode());

    }
    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 한다.")
    void deleteAccountFailed_balanceNotEmpty(){
        // given
        AccountUser pobi = AccountUser.builder()
                .name("Pobi")
                .id(12L)
                .build();
        given(accountUserRespository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)
                        .balance(100L)
                        .accountNumber("100000012")
                        .build()));

        // when
        AccountException accountException = assertThrows(AccountException.class,
                ()-> accountService.deleteAccount(1L,"1234567890"));
        // then
        assertEquals(ErrorCode.BALANCE_NOT_EMPTY,accountException.getErrorCode());

    }
    @Test
    @DisplayName("해지 계좌는 해지할 수 없다.")
    void deleteAccountFailed_alreadyUnregistered(){
        // given
        AccountUser pobi = AccountUser.builder()
                .name("Pobi")
                .id(12L)
                .build();
        given(accountUserRespository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .balance(0L)
                        .accountNumber("100000012")
                        .build()));

        // when
        AccountException accountException = assertThrows(AccountException.class,
                ()-> accountService.deleteAccount(1L,"1234567890"));
        // then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED,accountException.getErrorCode());

    }
}