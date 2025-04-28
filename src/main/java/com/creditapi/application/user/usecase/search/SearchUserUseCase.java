package com.creditapi.application.user.usecase.search;

import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.dto.search.UserSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchUserUseCase {
  Page<UserResponseDTO> execute(UserSearchDTO search, Pageable pageable);
}
