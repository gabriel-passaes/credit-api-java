package com.creditapi.infrastructure.invoice.provider.fake;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.dto.response.InvoiceStatusResponseDTO;
import com.creditapi.domain.invoice.provider.InvoiceProvider;

@Component
@Profile("test")
public class FakeInvoiceProvider implements InvoiceProvider {

    @Override
    public byte[] downloadInvoice(DownloadInvoiceRequestDTO requestDTO) {
        return new byte[]{1, 2, 3};
    }

    @Override
    public InvoiceStatusResponseDTO consultInvoiceStatus(DownloadInvoiceRequestDTO requestDTO) {
        return InvoiceStatusResponseDTO.builder()
                .status("SIMULATED_STATUS")
                .message("Simulated invoice status for testing.")
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
