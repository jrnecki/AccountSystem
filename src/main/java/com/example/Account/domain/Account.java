package com.example.Account.domain;

import com.example.Account.type.AccountStatus;
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
// java class라기 보다는 디비에 넣을 table 설정이라고 보면된다.
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {
    // pk로 지정
    @Id
    @GeneratedValue
    Long id;

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


}
