package com.creditapi.application.credit.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateCreditRequestDTO(
    @NotNull(message = "O ID do crédito é obrigatório para atualização") Long id,
    @NotBlank(message = "O número da NFS-e é obrigatório") String nfseNumber,
    @NotNull(message = "A data de constituição é obrigatória")
        @PastOrPresent(message = "A data de constituição não pode ser futura")
        LocalDate constitutionDate,
    @NotNull(message = "O valor do ISSQN é obrigatório")
        @Positive(message = "O valor do ISSQN deve ser positivo")
        BigDecimal issqnAmount,
    @NotBlank(message = "O tipo de crédito é obrigatório") String creditType,
    boolean simpleNational,
    @NotNull(message = "A alíquota é obrigatória")
        @DecimalMin(value = "0.0", inclusive = false, message = "A alíquota deve ser maior que 0")
        BigDecimal rate,
    @NotNull(message = "O valor faturado é obrigatório")
        @Positive(message = "O valor faturado deve ser positivo")
        BigDecimal billedAmount,
    @NotNull(message = "O valor de dedução é obrigatório")
        @PositiveOrZero(message = "O valor de dedução não pode ser negativo")
        BigDecimal deductionAmount,
    @NotNull(message = "A base de cálculo é obrigatória")
        @Positive(message = "A base de cálculo deve ser positiva")
        BigDecimal calculationBase,
    @NotNull(message = "O ID do usuário é obrigatório") Long userId) {}
