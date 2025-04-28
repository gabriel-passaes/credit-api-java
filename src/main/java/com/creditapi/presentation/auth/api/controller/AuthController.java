package com.creditapi.presentation.auth.api.controller;

import com.creditapi.application.auth.dto.request.LoginRequestDTO;
import com.creditapi.application.auth.dto.request.RecoverPasswordRequestDTO;
import com.creditapi.application.auth.dto.request.RefreshTokenRequestDTO;
import com.creditapi.application.auth.dto.request.RegisterRequestDTO;
import com.creditapi.application.auth.dto.request.ResetPasswordRequestDTO;
import com.creditapi.application.auth.dto.request.SocialLoginRequestDTO;
import com.creditapi.application.auth.dto.response.LoginResponseDTO;
import com.creditapi.application.auth.dto.response.PasswordRecoveryResponseDTO;
import com.creditapi.application.auth.dto.response.RefreshResponseDTO;
import com.creditapi.application.auth.dto.response.RegisterResponseDTO;
import com.creditapi.application.auth.usecase.login.LoginUserUseCase;
import com.creditapi.application.auth.usecase.login.social.LoginWithSocialUseCase;
import com.creditapi.application.auth.usecase.recover.RecoverPasswordUseCase;
import com.creditapi.application.auth.usecase.recover.ResetPasswordUseCase;
import com.creditapi.application.auth.usecase.refresh.InvalidateTokenUseCase;
import com.creditapi.application.auth.usecase.refresh.RefreshTokenUseCase;
import com.creditapi.application.auth.usecase.register.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints de autenticação, registro e tokens")
public class AuthController {

  private final LoginUserUseCase loginUseCase;
  private final LoginWithSocialUseCase socialLoginUseCase;
  private final RegisterUserUseCase registerUseCase;
  private final RecoverPasswordUseCase recoverUseCase;
  private final ResetPasswordUseCase resetUseCase;
  private final RefreshTokenUseCase refreshUseCase;
  private final InvalidateTokenUseCase invalidateUseCase;

  public AuthController(
      LoginUserUseCase loginUseCase,
      LoginWithSocialUseCase socialLoginUseCase,
      RegisterUserUseCase registerUseCase,
      RecoverPasswordUseCase recoverUseCase,
      ResetPasswordUseCase resetUseCase,
      RefreshTokenUseCase refreshUseCase,
      InvalidateTokenUseCase invalidateUseCase) {
    this.loginUseCase = loginUseCase;
    this.socialLoginUseCase = socialLoginUseCase;
    this.registerUseCase = registerUseCase;
    this.recoverUseCase = recoverUseCase;
    this.resetUseCase = resetUseCase;
    this.refreshUseCase = refreshUseCase;
    this.invalidateUseCase = invalidateUseCase;
  }

  @PostMapping(
      value = "/login",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Login com e-mail e senha")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Login realizado com sucesso",
        content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
  })
  public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
    LoginResponseDTO response = loginUseCase.execute(dto);
    response.setAuthSource("EMAIL");
    return ResponseEntity.ok(response);
  }

  @PostMapping(
      value = "/social",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Login via provedor social (Google/Facebook)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Login social bem-sucedido",
        content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Token social inválido")
  })
  public ResponseEntity<LoginResponseDTO> socialLogin(
      @Valid @RequestBody SocialLoginRequestDTO dto) {
    LoginResponseDTO response = socialLoginUseCase.execute(dto);
    response.setAuthSource(dto.getProvider().name());
    return ResponseEntity.ok(response);
  }

  @PostMapping(
      value = "/register",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Registra um novo usuário")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Cadastro realizado com sucesso",
        content = @Content(schema = @Schema(implementation = RegisterResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Dados de registro inválidos")
  })
  public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
    RegisterResponseDTO response = registerUseCase.execute(dto);
    response.setAuthSource("EMAIL");
    return ResponseEntity.status(201).body(response);
  }

  @PostMapping(
      value = "/recover",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Inicia fluxo de recuperação de senha")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "E-mail de recuperação enviado",
        content = @Content(schema = @Schema(implementation = PasswordRecoveryResponseDTO.class))),
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  public ResponseEntity<PasswordRecoveryResponseDTO> recover(
      @Valid @RequestBody RecoverPasswordRequestDTO dto) {
    return ResponseEntity.ok(recoverUseCase.execute(dto));
  }

  @PostMapping(
      value = "/reset",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Redefine senha usando token de recuperação")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Senha redefinida com sucesso",
        content = @Content(schema = @Schema(implementation = RefreshResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Token inválido ou expirado"),
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  public ResponseEntity<RefreshResponseDTO> reset(@Valid @RequestBody ResetPasswordRequestDTO dto) {
    RefreshResponseDTO response = resetUseCase.execute(dto);
    response.setAuthSource("RESET");
    return ResponseEntity.ok(response);
  }

  @PostMapping(
      value = "/refresh",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Renova access e refresh tokens")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Tokens renovados",
        content = @Content(schema = @Schema(implementation = RefreshResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
  })
  public ResponseEntity<RefreshResponseDTO> refresh(
      @Valid @RequestBody RefreshTokenRequestDTO dto) {
    RefreshResponseDTO response = refreshUseCase.execute(dto);
    response.setAuthSource("REFRESH");
    return ResponseEntity.ok(response);
  }

  @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Logout: invalida um refresh token")
  @ApiResponses({@ApiResponse(responseCode = "204", description = "Logout realizado com sucesso")})
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO dto) {
    invalidateUseCase.execute(dto);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(value = "/logout/all")
  @Operation(summary = "Logout de todos os tokens do usuário")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Todos os tokens invalidados"),
    @ApiResponse(responseCode = "401", description = "Não autenticado")
  })
  public ResponseEntity<Void> logoutAll(@Parameter(hidden = true) Authentication auth) {
    Long userId = (Long) auth.getPrincipal();
    invalidateUseCase.executeAll(userId);
    return ResponseEntity.noContent().build();
  }
}
