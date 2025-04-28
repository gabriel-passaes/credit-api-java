package com.creditapi.application.user.dto.search;

import lombok.Builder;

@Builder
public record UserSearchDTO(String name, String email, String document, String role) {}
