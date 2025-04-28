package com.creditapi.domain.credit.factory;

import com.creditapi.application.credit.dto.request.CreateCreditRequestDTO;
import com.creditapi.application.credit.dto.request.UpdateCreditRequestDTO;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class CreditFactory {

  public Credit fromRequest(CreateCreditRequestDTO dto, User user) {
    return Credit.builder()
        .id(null)
        .creditNumber(dto.creditNumber())
        .nfseNumber(dto.nfseNumber())
        .constitutionDate(dto.constitutionDate())
        .issqnAmount(dto.issqnAmount())
        .creditType(dto.creditType())
        .simpleNational(dto.simpleNational())
        .rate(dto.rate())
        .billedAmount(dto.billedAmount())
        .deductionAmount(dto.deductionAmount())
        .calculationBase(dto.calculationBase())
        .user(user)
        .build();
  }

  public Credit updateFromRequest(Credit existingCredit, UpdateCreditRequestDTO dto) {
    existingCredit.setConstitutionDate(dto.constitutionDate());
    existingCredit.setIssqnAmount(dto.issqnAmount());
    existingCredit.setCreditType(dto.creditType());
    existingCredit.setSimpleNational(dto.simpleNational());
    existingCredit.setRate(dto.rate());
    existingCredit.setBilledAmount(dto.billedAmount());
    existingCredit.setDeductionAmount(dto.deductionAmount());

    if (!existingCredit.getUser().getId().equals(dto.userId())) {
      if (existingCredit.getUser().getRole() != Role.SUPER_ADMIN) {
        existingCredit.setUser(new User(dto.userId()));
      }
    }

    return existingCredit;
  }
}
