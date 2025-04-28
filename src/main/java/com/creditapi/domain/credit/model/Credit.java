package com.creditapi.domain.credit.model;

import com.creditapi.domain.user.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credit {

  private Long id;
  private String creditNumber;
  private String nfseNumber;
  private LocalDate constitutionDate;
  private BigDecimal issqnAmount;
  private String creditType;
  private boolean simpleNational;
  private BigDecimal rate;
  private BigDecimal billedAmount;
  private BigDecimal deductionAmount;
  private BigDecimal calculationBase;
  private String uploadedFileName;
  private String uploadedFilePath;
  private boolean invoiceUploaded;
  private User user;
}
