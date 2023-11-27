package com.example.Account.domain;

import com.example.Account.exception.AccountException;
import com.example.Account.type.AccountStatus;
import com.example.Account.type.ErrorCode;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account extends BaseEntity{
    @ManyToOne // 1:n
    private AccountUser accountUser;
    private String accountNumber;

    // db상에 0또는1이 아닌 문자열형태로 저장
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    // 모든 entity의 공통적인 필드
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void useBalance(Long amount){
        if(amount > balance){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        balance-=amount;
    }

    public void cancelBalance(Long amount){
        if(amount < 0){
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }
        balance+=amount;
    }


}
