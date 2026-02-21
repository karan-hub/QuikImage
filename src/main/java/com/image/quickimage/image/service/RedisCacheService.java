package com.image.quickimage.image.service;

import com.image.quickimage.image.dto.ColorPaletteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {
    @Autowired
    private RedisTemplate<String , byte[]>  redisTemplate ;

    @Autowired
    private RedisTemplate<String, Object> metadataRedisTemplate;

    public void save(String key , byte[] imageByte){
        redisTemplate.opsForValue().set(
                key,
                imageByte,
                24 ,
                TimeUnit.HOURS
        );
    }

    public void saveMetadata(String key, ColorPaletteResponse colors) {
        metadataRedisTemplate.opsForValue().set("meta:" + key, colors, 24, TimeUnit.HOURS);
    }

    public byte[] get(String  key ){
        return  redisTemplate.opsForValue().get(key);
    }

    public ColorPaletteResponse getMetadata(String key) {
        return (ColorPaletteResponse) metadataRedisTemplate.opsForValue().get("meta:" + key);
    }
}
