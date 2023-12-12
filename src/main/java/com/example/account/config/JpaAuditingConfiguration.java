package com.example.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration // 자동으로 빈 등록
@EnableJpaAuditing // 스프링부트가 실행될때 켜진상태가 됨.
public class JpaAuditingConfiguration {
}
