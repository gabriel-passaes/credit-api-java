package com.creditapi.application.credit.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class CreditFilterRequestDTO {

  private String nfseNumber;
  private String creditNumber;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate createdAfter;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate createdBefore;

  private Long userId;
}
