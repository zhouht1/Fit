package com.fit.controller;

import com.fit.common.Result;
import com.fit.dto.AuthResponse;
import com.fit.dto.LoginRequest;
import com.fit.dto.RegisterRequest;
import com.fit.entity.User;
import com.fit.service.AuthService;
import com.fit.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return Result.success(response);
    }

    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return Result.success(response);
    }

    @PostMapping("/logout")
    public Result<?> logout(@AuthenticationPrincipal User user) {
        authService.logout(user.getId());
        return Result.success();
    }

    @GetMapping("/me")
    public Result<UserVO> me(@AuthenticationPrincipal User user) {
        UserVO vo = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
        return Result.success(vo);
    }
}