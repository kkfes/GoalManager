package org.example.goalmanager.service;

import org.example.goalmanager.dto.auth.LoginRequest;
import org.example.goalmanager.dto.auth.RegisterRequest;
import org.example.goalmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User registerUser(RegisterRequest request);

    User authenticate(LoginRequest request);

    User getCurrentUser();

    User getUserById(Long id);

    User getUserByEmail(String email);

    Page<User> getAllUsers(Pageable pageable);

    void deleteUser(Long userId);

    void resetUserPassword(Long userId);

    User updateUser(User user);
}
