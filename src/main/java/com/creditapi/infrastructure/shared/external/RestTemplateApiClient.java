package com.creditapi.infrastructure.shared.external;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateApiClient implements ApiClient {

  private final RestTemplate restTemplate;

  public RestTemplateApiClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public <T> ResponseEntity<T> exchange(
      String url,
      HttpMethod method,
      Map<String, String> headers,
      Object requestBody,
      Class<T> responseType) {

    HttpHeaders httpHeaders = new HttpHeaders();
    if (headers != null) {
      headers.forEach(httpHeaders::add);
    }

    HttpEntity<Object> entity = new HttpEntity<>(requestBody, httpHeaders);

    return restTemplate.exchange(url, method, entity, responseType);
  }
}
