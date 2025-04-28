package com.creditapi.infrastructure.shared.external;

import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public interface ApiClient {

  <T> ResponseEntity<T> exchange(
      String url,
      HttpMethod method,
      Map<String, String> headers,
      Object requestBody,
      Class<T> responseType);
}
