package com.example.datasyncapp.services;

import com.example.datasyncapp.dtos.ProviderDTO;
import com.example.datasyncapp.models.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ProviderService {
    private final EntityManager entityManager;

    @Value("${com.example.datasyncapp.mongo.collection.name}")
    private String collectionName;

    @Value("${com.example.datasyncapp.redis.hash.name}")
    private String redisHashName;

    private final MongoDataService mongoDataService;
    private final RedisDataService redisDataService;

    @Autowired
    public ProviderService(MongoDataService mongoDataService, RedisDataService redisDataService, EntityManager entityManager) {
        this.mongoDataService = mongoDataService;
        this.redisDataService = redisDataService;
        this.entityManager = entityManager;
    }

    @Transactional
    public void fetchAndSyncProvidersToMongo() {
        int offset = 0;
        int pageSize = 10;
        Long totalDataCount = entityManager
                .createQuery("SELECT DISTINCT COUNT(p.id) FROM Provider p", Long.class)
                .getSingleResult();
        log.info("<<< Fetching from the RDS started, total records to process : " + totalDataCount + " will be synced to mongo collection : " + collectionName);
        while (offset <= totalDataCount) {
            String fetchProvidersQuery = String.format("""
                    SELECT p FROM Provider p
                    ORDER BY p.id
                    OFFSET %d ROWS FETCH NEXT %d ROWS ONLY
                """, offset, pageSize);

            Query selectProvidersQuery = entityManager.createQuery(fetchProvidersQuery, Provider.class);
            List<ProviderDTO> providers = getProcessedProviders(selectProvidersQuery);
            // HANDLE situation line if data is already present in mongo, NETWORK LATENCY, INTERRUPTION ETC.
            mongoDataService.insertProvidersToCollection(collectionName, providers, offset, pageSize);
            providers.clear();
            offset += pageSize;
        }
        log.info("<<< All providers fetched from RDS and synced to mongo db : " + collectionName + " collection >>>");
    }

    @Transactional
    public void fetchAndSyncProvidersToRedis() {
        int offset = 0;
        int pageSize = 10;
        Long totalDataCount = entityManager.createQuery("SELECT DISTINCT COUNT(p.id) FROM Provider p", Long.class)
                .getSingleResult();
        log.info("<<< Fetching from the RDS started, total records to process : " + totalDataCount + " will be synced to redis hash : " + redisHashName);
        while (offset <= totalDataCount) {
            String fetchProvidersQuery = String.format("""
                    SELECT p FROM Provider p
                    ORDER BY p.id
                    OFFSET %d ROWS FETCH NEXT %d ROWS ONLY
                """, offset, pageSize);

            Query selectProvidersQuery = entityManager.createQuery(fetchProvidersQuery, Provider.class);
            List<ProviderDTO> providers = getProcessedProviders(selectProvidersQuery); // providers.clear() can't be done directly since it is unmodifiable list.
            // HANDLE situation line if data is already present in mongo, NETWORK LATENCY, INTERRUPTION ETC.
            redisDataService.saveProvidersToRedis(redisHashName, providers, offset, pageSize);
            providers.clear();
            offset += pageSize;
        }
        log.info(">>> All providers fetched from RDS and synced to redis hash : {}", redisHashName);
    }

    public List<ProviderDTO> getProcessedProviders(Query selectProvidersQuery) {
        Stream<Provider> providersStream = selectProvidersQuery.getResultStream();
        List<ProviderDTO> providers = providersStream
                .map(provider ->
                        new ProviderDTO(
                                provider.getId(), provider.getName(),
                                provider.getSpecialty(),
                                provider.getProviderTIN()) // can use mapper library as well which converts from one class to another.
                )
                .collect(Collectors.toList());
        log.debug("Providers fetched from the database with size {} ", providers.size());
        return providers;
    }

}
