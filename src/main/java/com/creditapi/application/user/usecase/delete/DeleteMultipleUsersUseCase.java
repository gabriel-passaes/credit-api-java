package com.creditapi.application.user.usecase.delete;

import com.creditapi.domain.user.model.User;
import java.util.List;

public interface DeleteMultipleUsersUseCase {
  void execute(User executor, List<Long> userIdsToDelete);
}
