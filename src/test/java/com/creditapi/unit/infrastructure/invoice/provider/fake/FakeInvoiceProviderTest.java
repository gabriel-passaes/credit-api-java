package com.creditapi.unit.infrastructure.invoice.provider.fake;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.dto.response.InvoiceStatusResponseDTO;
import com.creditapi.infrastructure.invoice.provider.fake.FakeInvoiceProvider;

class FakeInvoiceProviderTest {

    private final FakeInvoiceProvider fakeInvoiceProvider = new FakeInvoiceProvider();

    @Test
    @DisplayName("Deve retornar um arquivo simulado ao fazer download de nota fiscal")
    void shouldReturnFakePdf_whenDownloadInvoice() {
        DownloadInvoiceRequestDTO request = DownloadInvoiceRequestDTO.builder()
                .invoiceNumber("12345")
                .cnpj("12345678000195")
                .build();

        byte[] result = fakeInvoiceProvider.downloadInvoice(request);

        assertArrayEquals(new byte[]{1, 2, 3}, result);
    }

    @Test
    @DisplayName("Deve retornar status simulado ao consultar status da nota fiscal")
    void shouldReturnSimulatedStatus_whenConsultInvoiceStatus() {
        DownloadInvoiceRequestDTO request = DownloadInvoiceRequestDTO.builder()
                .invoiceNumber("12345")
                .cnpj("12345678000195")
                .build();

        InvoiceStatusResponseDTO result = fakeInvoiceProvider.consultInvoiceStatus(request);

        assertEquals("SIMULATED_STATUS", result.status());
        assertEquals("Simulated invoice status for testing.", result.message());
    }
}
