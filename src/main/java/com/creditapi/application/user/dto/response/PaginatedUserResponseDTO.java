package com.creditapi.application.user.dto.response;

import java.util.List;

public record PaginatedUserResponseDTO(
    List<UserResponseDTO> content, int page, int size, long totalElements, int totalPages) {}
