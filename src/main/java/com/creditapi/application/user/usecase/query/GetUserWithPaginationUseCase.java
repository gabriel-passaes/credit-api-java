package com.creditapi.application.user.usecase.query;

import com.creditapi.application.user.dto.response.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetUserWithPaginationUseCase {
  Page<UserResponseDTO> execute(Pageable pageable);
}
