package com.creditapi.infrastructure.credit.persistence.entity;

import com.creditapi.infrastructure.user.persistence.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "credit_note")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "numero_credito", nullable = false)
  private String creditNumber;

  @Column(name = "numero_nfse", nullable = false)
  private String nfseNumber;

  @Column(name = "data_constituicao", nullable = false)
  private LocalDate constitutionDate;

  @Column(name = "valor_issqn", nullable = false)
  private BigDecimal issqnAmount;

  @Column(name = "tipo_credito", nullable = false)
  private String creditType;

  @Column(name = "simples_nacional", nullable = false)
  private boolean simpleNational;

  @Column(name = "aliquota", nullable = false)
  private BigDecimal rate;

  @Column(name = "valor_faturado", nullable = false)
  private BigDecimal billedAmount;

  @Column(name = "valor_deducao", nullable = false)
  private BigDecimal deductionAmount;

  @Column(name = "base_calculo", nullable = false)
  private BigDecimal calculationBase;

  @Column(name = "nome_arquivo")
  private String uploadedFileName;

  @Column(name = "caminho_arquivo")
  private String uploadedFilePath;

  @Column(name = "nota_enviada")
  private boolean invoiceUploaded;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private UserEntity user;
}
