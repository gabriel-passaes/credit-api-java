package com.creditapi.presentation.user.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.creditapi.application.user.dto.request.CreateUserRequestDTO;
import com.creditapi.application.user.dto.request.UpdateUserRequestDTO;
import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.dto.search.UserSearchDTO;
import com.creditapi.application.user.usecase.create.CreateUserUseCase;
import com.creditapi.application.user.usecase.delete.DeleteMultipleUsersUseCase;
import com.creditapi.application.user.usecase.delete.DeleteUserUseCase;
import com.creditapi.application.user.usecase.query.GetUserByEmailUseCase;
import com.creditapi.application.user.usecase.query.GetUserByIdUseCase;
import com.creditapi.application.user.usecase.query.GetUserWithPaginationUseCase;
import com.creditapi.application.user.usecase.search.SearchUserUseCase;
import com.creditapi.application.user.usecase.update.UpdateUserUseCase;
import com.creditapi.application.user.usecase.upload.UploadUserAvatarUseCase;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "Endpoints relacionados à gestão de usuários")
public class UserController {

  private final CreateUserUseCase createUser;
  private final UpdateUserUseCase updateUser;
  private final DeleteUserUseCase deleteUser;
  private final DeleteMultipleUsersUseCase deleteMultiple;
  private final GetUserByIdUseCase getById;
  private final GetUserByEmailUseCase getByEmail;
  private final GetUserWithPaginationUseCase getAll;
  private final SearchUserUseCase search;
  private final UploadUserAvatarUseCase upload;
  private final UserRepository repository;

  public UserController(
      CreateUserUseCase createUser,
      UpdateUserUseCase updateUser,
      DeleteUserUseCase deleteUser,
      DeleteMultipleUsersUseCase deleteMultiple,
      GetUserByIdUseCase getById,
      GetUserByEmailUseCase getByEmail,
      GetUserWithPaginationUseCase getAll,
      SearchUserUseCase search,
      UploadUserAvatarUseCase upload,
      UserRepository repository) {
    this.createUser = createUser;
    this.updateUser = updateUser;
    this.deleteUser = deleteUser;
    this.deleteMultiple = deleteMultiple;
    this.getById = getById;
    this.getByEmail = getByEmail;
    this.getAll = getAll;
    this.search = search;
    this.upload = upload;
    this.repository = repository;
  }

  @PostMapping
  @Operation(summary = "Cria um novo usuário")
  @ApiResponses({
    @ApiResponse(
      responseCode = "201",
      description = "Usuário criado com sucesso",
      content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Erro de validação",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        examples = @ExampleObject(value = "{\"message\": \"Dados de requisição inválidos\"}")
      )
    )
  })
  public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid CreateUserRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(createUser.execute(request));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Atualiza um usuário", parameters = @Parameter(name = "id", description = "ID do usuário"))
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Usuário atualizado com sucesso",
      content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        examples = @ExampleObject(value = "{\"message\": \"Dados inválidos para atualização\"}")
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Usuário não encontrado",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}")
      )
    )
  })
  public ResponseEntity<UserResponseDTO> update(
      @PathVariable Long id, @RequestBody @Valid UpdateUserRequestDTO request) {
    return ResponseEntity.ok(updateUser.execute(id, request));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Remove um usuário", parameters = @Parameter(name = "id", description = "ID do usuário"))
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
    @ApiResponse(
      responseCode = "404",
      description = "Usuário não encontrado",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}")
      )
    )
  })
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    deleteUser.execute(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping
  @Operation(summary = "Remove múltiplos usuários (somente SUPER_ADMIN)",
      description = "<p>Deve ser executado apenas por usuários com perfil SUPER_ADMIN.</p>")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Usuários removidos com sucesso"),
    @ApiResponse(
      responseCode = "403",
      description = "Permissão negada",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        examples = @ExampleObject(value = "{\"message\": \"Permissão negada\"}")
      )
    )
  })
  public ResponseEntity<Void> deleteMultipleUsers(
      @RequestBody List<Long> userIds, @Parameter(hidden = true) Authentication auth) {

    Long executorId = (Long) auth.getPrincipal();
    User executor = repository.findById(executorId)
        .orElseThrow(() -> new RuntimeException("Usuário executor não encontrado"));

    deleteMultiple.execute(executor, userIds);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Busca um usuário pelo ID", parameters = @Parameter(name = "id", description = "ID do usuário"))
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Usuário encontrado",
      content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Usuário não encontrado",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}")
      )
    )
  })
  public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(getById.execute(id));
  }

  @GetMapping("/email/{email}")
  @Operation(summary = "Busca um usuário pelo e-mail", parameters = @Parameter(name = "email", description = "E-mail do usuário"))
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Usuário encontrado",
      content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Usuário não encontrado",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}")
      )
    )
  })
  public ResponseEntity<UserResponseDTO> getByEmail(@PathVariable String email) {
    return ResponseEntity.ok(getByEmail.execute(email));
  }

  @GetMapping
  @Operation(summary = "Lista usuários paginados", parameters = {
    @Parameter(name = "page", description = "Número da página"),
    @Parameter(name = "size", description = "Tamanho da página")
  })
  @ApiResponse(
    responseCode = "200",
    description = "Lista paginada retornada com sucesso",
    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
  )
  public ResponseEntity<Page<UserResponseDTO>> getAll(Pageable pageable) {
    return ResponseEntity.ok(getAll.execute(pageable));
  }

  @PostMapping("/search")
  @Operation(summary = "Busca avançada de usuários", description = "Permite filtrar usuários por nome, email ou data de cadastro.")
  @ApiResponse(
    responseCode = "200",
    description = "Busca realizada com sucesso",
    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
  )
  public ResponseEntity<Page<UserResponseDTO>> search(
      @RequestBody @Valid UserSearchDTO searchDto, Pageable pageable) {
    return ResponseEntity.ok(search.execute(searchDto, pageable));
  }

  @PostMapping("/{id}/upload")
  @Operation(summary = "Faz upload do avatar de um usuário", parameters = @Parameter(name = "id", description = "ID do usuário"))
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Upload realizado com sucesso"),
    @ApiResponse(
      responseCode = "400",
      description = "Erro ao processar o arquivo",
      content = @Content(
        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
        examples = @ExampleObject(value = "{\"message\": \"Tipo de arquivo inválido\"}")
      )
    )
  })
  public ResponseEntity<Void> upload(
      @PathVariable Long id, @RequestParam("file") MultipartFile file) {
    upload.execute(id, file);
    return ResponseEntity.ok().build();
  }
}
