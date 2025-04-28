package com.creditapi.unit.application.user.service.upload;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.user.service.upload.UploadUserAvatarService;
import com.creditapi.domain.user.exception.UserNotFoundException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.shared.storage.StorageService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UploadUserAvatarServiceTest {

  @Mock private UserRepository repository;

  @Mock private StorageService storage;

  @InjectMocks private UploadUserAvatarService service;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(service, "avatarBaseUrl", "https://cdn.test/");
  }

  @Test
  @DisplayName("Deve realizar upload e atualizar avatar do usuário")
  void shouldUploadAvatarSuccessfully() {
    User user = User.builder().id(1L).build();
    MockMultipartFile file =
        new MockMultipartFile("avatar", "avatar.png", "image/png", new byte[] {1, 2});

    when(repository.findById(1L)).thenReturn(Optional.of(user));

    service.execute(1L, file);

    verify(storage).upload("avatars/avatar.png", file);
    verify(repository).save(user);
  }

  @Test
  @DisplayName("Deve lançar exceção se usuário não for encontrado")
  void shouldThrowWhenUserNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());
    MockMultipartFile file =
        new MockMultipartFile("avatar", "avatar.png", "image/png", new byte[] {1, 2});

    assertThatThrownBy(() -> service.execute(99L, file)).isInstanceOf(UserNotFoundException.class);
  }
}
