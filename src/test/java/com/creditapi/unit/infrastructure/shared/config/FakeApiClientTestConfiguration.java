package com.creditapi.unit.infrastructure.shared.config;

import java.util.Map;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.creditapi.infrastructure.shared.external.ApiClient;

@TestConfiguration
public class FakeApiClientTestConfiguration {

    @Bean
    public ApiClient fakeApiClient() {
        return new ApiClient() {
            @Override
            public <T> ResponseEntity<T> exchange(String url, HttpMethod method, Map<String, String> headers, Object requestBody, Class<T> responseType) {
                return ResponseEntity.ok().build();
            }
        };
    }
}
