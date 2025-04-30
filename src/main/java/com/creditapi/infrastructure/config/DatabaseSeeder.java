package com.creditapi.infrastructure.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.creditapi.infrastructure.credit.persistence.entity.CreditEntity;
import com.creditapi.infrastructure.credit.persistence.repository.CreditJpaRepository;
import com.creditapi.infrastructure.user.persistence.entity.UserEntity;
import com.creditapi.infrastructure.user.persistence.repository.UserJpaRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {

  private final UserJpaRepository userJpaRepository;
  private final CreditJpaRepository creditJpaRepository;

  public DatabaseSeeder(UserJpaRepository userJpaRepository, CreditJpaRepository creditJpaRepository) {
    this.userJpaRepository = userJpaRepository;
    this.creditJpaRepository = creditJpaRepository;
  }

  @Override
  public void run(String... args) {
    if (creditJpaRepository.count() > 0) return;

    System.out.println("📘 Populando usuários e créditos de exemplo...");

    UserEntity user1 = userJpaRepository.findByEmail("superadmin@empresa.com")
      .orElseGet(() -> userJpaRepository.save(
        UserEntity.builder()
          .name("Super Admin")
          .email("superadmin@empresa.com")
          .document("12345678900")
          .password("senha_superadmin")
          .role("SUPER_ADMIN")
          .createdAt(LocalDateTime.now())
          .updatedAt(LocalDateTime.now())
          .build()
      ));

    UserEntity user2 = userJpaRepository.findByEmail("admin@empresa.com")
      .orElseGet(() -> userJpaRepository.save(
        UserEntity.builder()
          .name("Admin User")
          .email("admin@empresa.com")
          .document("98765432100")
          .password("senha_admin")
          .role("ADMIN")
          .createdAt(LocalDateTime.now())
          .updatedAt(LocalDateTime.now())
          .build()
      ));

    UserEntity user3 = userJpaRepository.findByEmail("user@empresa.com")
      .orElseGet(() -> userJpaRepository.save(
        UserEntity.builder()
          .name("Regular User")
          .email("user@empresa.com")
          .document("12312312300")
          .password("senha_user")
          .role("USER")
          .createdAt(LocalDateTime.now())
          .updatedAt(LocalDateTime.now())
          .build()
      ));

    for (int i = 1; i <= 50; i++) {
      CreditEntity credit = CreditEntity.builder()
        .creditNumber(String.format("CREDIT-%03d", i))
        .nfseNumber(String.format("NFSE-%03d", i))
        .constitutionDate(LocalDate.now())
        .issqnAmount(BigDecimal.valueOf(1000 + i * 10))
        .creditType("ISSQN")
        .simpleNational(true)
        .rate(BigDecimal.valueOf(5.00))
        .billedAmount(BigDecimal.valueOf(2000 + i * 100))
        .deductionAmount(BigDecimal.valueOf(500 + i * 10))
        .calculationBase(BigDecimal.valueOf(1500 + i * 50))
        .uploadedFileName(null)
        .uploadedFilePath(null)
        .invoiceUploaded(false)
        .user(i % 3 == 0 ? user3 : i % 2 == 0 ? user2 : user1)
        .build();

      creditJpaRepository.save(credit);
    }

    System.out.println("✅ Usuários e créditos populados com sucesso.");
  }
}
