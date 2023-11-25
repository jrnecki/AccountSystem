package com.example.Account.dto;
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
