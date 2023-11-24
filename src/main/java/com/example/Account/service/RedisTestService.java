package com.example.Account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor // 자동으로 final field 주입.
public class RedisTestService {
    private final RedissonClient redissonClient;

    public String getLock(){
        RLock lock = redissonClient.getLock("samplelock");
        try{
            boolean isLock = lock.tryLock(1,5, TimeUnit.SECONDS);
            if(!isLock){
                return "Lock Failed";
            }
        }catch (Exception e){
            log.error("Redis lock failed.");
        }
        return "Lock Success";
    }
}
