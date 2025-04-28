package com.creditapi.unit.application.credit.service.create;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.dto.request.CreateCreditRequestDTO;
import com.creditapi.application.credit.dto.response.CreateCreditResponseDTO;
import com.creditapi.application.credit.service.create.CreateCreditService;
import com.creditapi.domain.credit.exception.DuplicateCreditException;
import com.creditapi.domain.credit.exception.UnauthorizedCreditCreateException;
import com.creditapi.domain.credit.factory.CreditFactory;
import com.creditapi.domain.credit.gateway.event.CreditEventPublisher;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.security.AuthenticatedUserProvider;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("Testes para CreateCreditService")
class CreateCreditServiceTest {

  @Mock private CreditRepository creditRepository;
  @Mock private CreditFactory creditFactory;
  @Mock private CreditEventPublisher eventPublisher;
  @Mock private AuthenticatedUserProvider authenticatedUserProvider;

  @InjectMocks private CreateCreditService createCreditService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve criar crédito com sucesso quando payload é válido")
  void shouldCreateCreditSuccessfully_whenValidRequest_thenReturnResponse() {
    CreateCreditRequestDTO request = createValidRequest();
    User authenticatedUser = createAuthenticatedUser(request.userId());
    Credit credit = createMockCredit(request);

    when(authenticatedUserProvider.getRequiredUser()).thenReturn(authenticatedUser);
    when(creditRepository.findByCreditNumber(request.creditNumber())).thenReturn(Optional.empty());
    when(creditFactory.fromRequest(any(), any())).thenReturn(credit);
    when(creditRepository.save(any())).thenReturn(credit);

    CreateCreditResponseDTO response = createCreditService.execute(request);

    assertNotNull(response);
    assertEquals("CREDIT001", response.creditNumber());
    verify(creditRepository).save(any());
    verify(eventPublisher).publishCreditCreated(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar criar crédito duplicado")
  void shouldThrowException_whenCreditIsDuplicated_thenAbort() {
    CreateCreditRequestDTO request = createValidRequest();
    Credit credit = createMockCredit(request);

    when(creditRepository.findByCreditNumber(request.creditNumber()))
        .thenReturn(Optional.of(credit));

    assertThrows(DuplicateCreditException.class, () -> createCreditService.execute(request));
    verify(creditRepository, never()).save(any());
    verify(eventPublisher, never()).publishCreditCreated(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao usuário não autorizado criar crédito para outro")
  void shouldThrowException_whenUnauthorizedUserTriesToCreateCredit() {
    CreateCreditRequestDTO request = createValidRequest();
    User authenticatedUser = createAuthenticatedUser(999L);

    when(authenticatedUserProvider.getRequiredUser()).thenReturn(authenticatedUser);
    when(creditRepository.findByCreditNumber(request.creditNumber())).thenReturn(Optional.empty());

    assertThrows(
        UnauthorizedCreditCreateException.class, () -> createCreditService.execute(request));
    verify(creditRepository, never()).save(any());
    verify(eventPublisher, never()).publishCreditCreated(any());
  }

  @Test
  @DisplayName("Deve lançar exceção se authenticated user for nulo")
  void shouldThrowException_whenAuthenticatedUserIsNull() {
    CreateCreditRequestDTO request = createValidRequest();

    when(authenticatedUserProvider.getRequiredUser()).thenReturn(null);

    assertThrows(NullPointerException.class, () -> createCreditService.execute(request));
  }

  @Test
  @DisplayName("Deve lançar exceção se CreditFactory retornar nulo")
  void shouldThrowException_whenCreditFactoryReturnsNull() {
    CreateCreditRequestDTO request = createValidRequest();
    User authenticatedUser = createAuthenticatedUser(request.userId());

    when(authenticatedUserProvider.getRequiredUser()).thenReturn(authenticatedUser);
    when(creditRepository.findByCreditNumber(request.creditNumber())).thenReturn(Optional.empty());
    when(creditFactory.fromRequest(any(), any())).thenReturn(null);

    assertThrows(NullPointerException.class, () -> createCreditService.execute(request));
  }

  @Test
  @DisplayName("Deve lançar exceção se CreditRepository.save retornar nulo")
  void shouldThrowException_whenCreditSaveReturnsNull() {
    CreateCreditRequestDTO request = createValidRequest();
    User authenticatedUser = createAuthenticatedUser(request.userId());
    Credit credit = createMockCredit(request);

    when(authenticatedUserProvider.getRequiredUser()).thenReturn(authenticatedUser);
    when(creditRepository.findByCreditNumber(request.creditNumber())).thenReturn(Optional.empty());
    when(creditFactory.fromRequest(any(), any())).thenReturn(credit);
    when(creditRepository.save(any())).thenReturn(null);

    assertThrows(NullPointerException.class, () -> createCreditService.execute(request));
  }

  @Test
  @DisplayName("Deve criar crédito mesmo se nome e e-mail do usuário forem nulos")
  void shouldCreateCredit_whenUserNameOrEmailIsNull() {
    CreateCreditRequestDTO request = createValidRequest();
    User authenticatedUser = createAuthenticatedUser(request.userId());
    Credit credit = createMockCredit(request);
    credit.getUser().setName(null);
    credit.getUser().setEmail(null);

    when(authenticatedUserProvider.getRequiredUser()).thenReturn(authenticatedUser);
    when(creditRepository.findByCreditNumber(request.creditNumber())).thenReturn(Optional.empty());
    when(creditFactory.fromRequest(any(), any())).thenReturn(credit);
    when(creditRepository.save(any())).thenReturn(credit);

    CreateCreditResponseDTO response = createCreditService.execute(request);

    assertNotNull(response);
    assertEquals("CREDIT001", response.creditNumber());
  }

  @Test
  @DisplayName("Deve lançar exceção se eventPublisher falhar")
  void shouldStillSaveCredit_whenEventPublisherFails() {
    CreateCreditRequestDTO request = createValidRequest();
    User authenticatedUser = createAuthenticatedUser(request.userId());
    Credit credit = createMockCredit(request);

    when(authenticatedUserProvider.getRequiredUser()).thenReturn(authenticatedUser);
    when(creditRepository.findByCreditNumber(request.creditNumber())).thenReturn(Optional.empty());
    when(creditFactory.fromRequest(any(), any())).thenReturn(credit);
    when(creditRepository.save(any())).thenReturn(credit);
    doThrow(new RuntimeException("Erro no evento"))
        .when(eventPublisher)
        .publishCreditCreated(any());

    assertDoesNotThrow(() -> createCreditService.execute(request));
  }

  @Test
  @DisplayName("Deve permitir criação de crédito por SUPER_ADMIN para outro usuário")
  void shouldAllowSuperAdminToCreateCreditForOtherUser() {
    CreateCreditRequestDTO request = createValidRequest();
    User authenticatedUser = createSuperAdminUser();

    when(authenticatedUserProvider.getRequiredUser()).thenReturn(authenticatedUser);
    when(creditRepository.findByCreditNumber(request.creditNumber())).thenReturn(Optional.empty());
    when(creditFactory.fromRequest(any(), any())).thenReturn(createMockCredit(request));
    when(creditRepository.save(any())).thenReturn(createMockCredit(request));

    CreateCreditResponseDTO response = createCreditService.execute(request);

    assertNotNull(response);
    verify(creditRepository).save(any());
  }

  private CreateCreditRequestDTO createValidRequest() {
    return new CreateCreditRequestDTO(
        "CREDIT001",
        "NF001",
        LocalDate.now(),
        BigDecimal.valueOf(1000),
        "ISSQN",
        true,
        BigDecimal.valueOf(5.0),
        BigDecimal.valueOf(10000),
        BigDecimal.valueOf(500),
        BigDecimal.valueOf(9500),
        1L);
  }

  private User createAuthenticatedUser(Long id) {
    return User.builder().id(id).role(Role.USER).build();
  }

  private User createSuperAdminUser() {
    return User.builder().id(999L).role(Role.SUPER_ADMIN).build();
  }

  private Credit createMockCredit(CreateCreditRequestDTO request) {
    User user =
        User.builder().id(request.userId()).name("Fulano").email("fulano@email.com").build();
    return Credit.builder()
        .id(1L)
        .creditNumber(request.creditNumber())
        .nfseNumber(request.nfseNumber())
        .constitutionDate(request.constitutionDate())
        .issqnAmount(request.issqnAmount())
        .creditType(request.creditType())
        .simpleNational(request.simpleNational())
        .rate(request.rate())
        .billedAmount(request.billedAmount())
        .deductionAmount(request.deductionAmount())
        .calculationBase(request.calculationBase())
        .user(user)
        .build();
  }
}
