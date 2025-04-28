package com.creditapi.infrastructure.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
@Profile("!test") 
public class RedisConfig {

  @Bean
  @Primary
  public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    RedisSerializationContext.SerializationPair<Object> jsonSerializer =
        RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer());

    RedisCacheConfiguration defaultConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(jsonSerializer)
            .entryTtl(Duration.ofMinutes(15));

    Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
    cacheConfigs.put("credit-by-number", defaultConfig.entryTtl(Duration.ofMinutes(30)));
    cacheConfigs.put("credits-by-nfse", defaultConfig.entryTtl(Duration.ofMinutes(10)));
    cacheConfigs.put("paginated-credits", defaultConfig.entryTtl(Duration.ofMinutes(5)));

    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(defaultConfig)
        .withInitialCacheConfigurations(cacheConfigs)
        .build();
  }
}
