package com.creditapi.application.credit.usecase.download;

public interface DownloadCreditInvoiceUseCase {
  byte[] execute(String creditNumber);
}
