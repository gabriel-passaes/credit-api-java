package com.creditapi.application.credit.service.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.creditapi.application.credit.usecase.upload.UploadCreditFileUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.infrastructure.shared.storage.StorageService;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.observation.annotation.Observed;

@Service
public class UploadCreditFileService implements UploadCreditFileUseCase {

  private static final Logger logger = LoggerFactory.getLogger(UploadCreditFileService.class);

  private final StorageService storageService;
  private final CreditRepository creditRepository;

  public UploadCreditFileService(
      @Qualifier("creditStorageServiceImpl") StorageService storageService,
      CreditRepository creditRepository
  ) {
    this.storageService = storageService;
    this.creditRepository = creditRepository;
  }

  @Override
  @Observed(name = "credit.upload-file")
  @RateLimiter(name = "creditService")
  @CacheEvict(
      value = {"credit-by-id"},
      key = "#creditId"
  )
  public void execute(Long creditId, MultipartFile file) {
    logger.info("📎 Iniciando upload de nota fiscal para o crédito ID {}", creditId);

    if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
      logger.warn("⚠️ Arquivo inválido ou nome ausente no upload para crédito {}", creditId);
      throw new IllegalArgumentException("Arquivo inválido ou nome ausente");
    }

    String path = "credits/" + creditId + "/" + file.getOriginalFilename();

    storageService.upload(path, file);
    logger.info("✅ Upload concluído com sucesso para o crédito ID {}. Salvando informações...", creditId);

    Credit credit =
        creditRepository.findAll().stream()
            .filter(c -> creditId.equals(c.getId()))
            .findFirst()
            .orElseThrow(() -> {
              logger.error("❌ Crédito não encontrado para ID {}", creditId);
              return new CreditNotFoundException("Crédito não encontrado: ID " + creditId);
            });

    credit.setUploadedFileName(file.getOriginalFilename());
    credit.setUploadedFilePath(path);
    credit.setInvoiceUploaded(true);

    creditRepository.save(credit);
    logger.info("📥 Crédito {} atualizado com sucesso após upload de nota fiscal.", credit.getCreditNumber());
  }
}
