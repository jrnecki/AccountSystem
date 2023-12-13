package com.example.account.domain;

import com.example.account.type.Bank;
import lombok.*;

import javax.persistence.Entity;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class AccountUser extends BaseEntity{
    private String name;
}
