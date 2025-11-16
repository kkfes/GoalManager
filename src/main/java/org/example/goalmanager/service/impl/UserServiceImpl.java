package org.example.goalmanager.service.impl;

import org.example.goalmanager.dto.auth.LoginRequest;
import org.example.goalmanager.dto.auth.RegisterRequest;
import org.example.goalmanager.entity.User;
import org.example.goalmanager.entity.UserRole;
import org.example.goalmanager.exception.BadRequestException;
import org.example.goalmanager.exception.NotFoundException;
import org.example.goalmanager.repository.UserRepository;
import org.example.goalmanager.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already in use");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .active(true)
                .build();
        return userRepository.save(user);
    }

    @Override
    public User authenticate(LoginRequest request) {
        // Аутентификация выполняется в AuthController через AuthenticationManager,
        // здесь можно просто вернуть пользователя по email.
        return userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotFoundException("User not authenticated");
        }
        String usernameOrEmail = authentication.getName();
        return userRepository.findByEmail(usernameOrEmail)
                .orElseGet(() -> userRepository.findByUsername(usernameOrEmail)
                        .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public void resetUserPassword(Long userId) {
        User user = getUserById(userId);
        // Простейшая логика: сбросить пароль на "password". В реальном мире нужно отправить письмо и т.д.
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
