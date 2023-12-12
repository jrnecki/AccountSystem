package com.example.account.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;

@Slf4j
@Configuration
public class LocalRedisConfig {
    @Value("${spring.redis.port}") // application의 설정값을
    private int redisPort; // 여기에 담아준다.

    @Value("${spring.redis.memory}")
    private String redisMemory;

    private RedisServer redisServer;
    @PostConstruct
    public void startRedis(){
        try{
            redisServer = RedisServer.builder()
                    .port(redisPort)
                    .build();
            redisServer.start();
        }catch (Exception e){
            log.error("",e);
        }
    }

    @PreDestroy
    public void stopRedis(){
        if(redisServer != null){
            redisServer.stop();
        }
    }
}
