package com.image.quickimage.image.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    @Autowired
    private  ProxyManager<String> proxyManager ;

    public Bucket  resolveBucket(String key){
        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(5).refillGreedy(5 , Duration.ofMinutes(1)))
                .build();
        return  proxyManager.builder().build(key , configuration);
    }
}
