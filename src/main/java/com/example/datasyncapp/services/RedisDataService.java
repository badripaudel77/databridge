package com.example.datasyncapp.services;

import com.example.datasyncapp.dtos.ProviderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * We have two choices of Java clients that you can use with Redis:
 * 1. Jedis, for synchronous applications.
 * 2. Lettuce, for asynchronous and reactive applications.
 */

@Slf4j
@Service
public class RedisDataService {
    private RedisTemplate<String, ?> redisTemplate;

    // Hash Key is String, each key in a hash key is String and Value is Object (ProviderDTO)
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    public RedisDataService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
    }

    public void saveProvidersToRedis(String redisHashName, List<ProviderDTO> providers, int offset, int pageSize) {
        providers.
        forEach(providerDTO ->
                saveProviderToRedis(redisHashName, "providers:" + providerDTO.providerId(), providerDTO)
        );
    }

    private void saveProviderToRedis(String rootKey, String field, ProviderDTO value) {
        hashOperations.put(rootKey, field, value);
    }

    /**
     * @param redisHashName
     * @returns the list of all providers under redisHashName given
     * Key for each is String and corresponding value is ProviderDTO
     */
    List<ProviderDTO> getAllProvidersFromRedis(String redisHashName) {
        Map<String, Object> allEntries = hashOperations.entries(redisHashName);
        Set<Map.Entry<String, Object>> entrySet = allEntries.entrySet();
        List<ProviderDTO> providerDTOS = new ArrayList<>();
        for(Map.Entry<String, Object> entry: entrySet) {
            providerDTOS.add((ProviderDTO) entry.getValue());
        }
        return providerDTOS;
    }
    /**
     * @param redisHashName
     * @param providerDTO
     * This method retrieves the Provider (only one) details stored in the hash based on provider object (DTO) passed.
     * Root key is redisHashName
     * Key under root key is providers:{providerId}
     */
    ProviderDTO getProviderFromRedis(String redisHashName, ProviderDTO providerDTO) {
        Object providerFromRedis = hashOperations.get(redisHashName, "providers:".concat(providerDTO.providerId().toString()));
        return (ProviderDTO) providerFromRedis;
    }

    // Not used for now.
    private boolean isHashKeyEmpty(String hashName) {
        Set<String> keys = this.hashOperations.keys(hashName);
        return keys.isEmpty();
    }
}

