package com.example.account.dto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountInfo { // client <-> controller
    private String accountNumber;
    private Long balance;
}
