package com.creditapi.application.credit.mapper;

import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.credit.persistence.entity.CreditEntity;
import com.creditapi.infrastructure.user.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class CreditMapper {

  public Credit toDomain(CreditEntity entity) {
    User user =
        User.builder()
            .id(entity.getUser().getId())
            .name(entity.getUser().getName())
            .email(entity.getUser().getEmail())
            .document(entity.getUser().getDocument())
            .role(Role.valueOf(entity.getUser().getRole()))
            .build();

    return Credit.builder()
        .id(entity.getId())
        .creditNumber(entity.getCreditNumber())
        .nfseNumber(entity.getNfseNumber())
        .constitutionDate(entity.getConstitutionDate())
        .issqnAmount(entity.getIssqnAmount())
        .creditType(entity.getCreditType())
        .simpleNational(entity.isSimpleNational())
        .rate(entity.getRate())
        .billedAmount(entity.getBilledAmount())
        .deductionAmount(entity.getDeductionAmount())
        .calculationBase(entity.getCalculationBase())
        .uploadedFileName(entity.getUploadedFileName())
        .uploadedFilePath(entity.getUploadedFilePath())
        .invoiceUploaded(entity.isInvoiceUploaded())
        .user(user)
        .build();
  }

  public CreditEntity toEntity(Credit credit) {
    UserEntity userEntity = new UserEntity();
    userEntity.setId(credit.getUser().getId());

    return CreditEntity.builder()
        .id(credit.getId())
        .creditNumber(credit.getCreditNumber())
        .nfseNumber(credit.getNfseNumber())
        .constitutionDate(credit.getConstitutionDate())
        .issqnAmount(credit.getIssqnAmount())
        .creditType(credit.getCreditType())
        .simpleNational(credit.isSimpleNational())
        .rate(credit.getRate())
        .billedAmount(credit.getBilledAmount())
        .deductionAmount(credit.getDeductionAmount())
        .calculationBase(credit.getCalculationBase())
        .uploadedFileName(credit.getUploadedFileName())
        .uploadedFilePath(credit.getUploadedFilePath())
        .invoiceUploaded(credit.isInvoiceUploaded())
        .user(userEntity)
        .build();
  }
}
