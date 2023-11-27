package com.example.Account.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import javax.persistence.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class AccountUser extends BaseEntity{
    private String name;

}
