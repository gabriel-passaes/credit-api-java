package com.creditapi.domain.credit.model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditFilter {

  private String nfseNumber;
  private String creditNumber;
  private String creditType;
  private LocalDate createdAfter;
  private LocalDate createdBefore;
  private Long userId;
}
