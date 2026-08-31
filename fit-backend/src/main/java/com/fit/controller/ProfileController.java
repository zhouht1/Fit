package com.fit.controller;

import com.fit.common.Result;
import com.fit.dto.ProfileRequest;
import com.fit.entity.User;
import com.fit.service.ProfileService;
import com.fit.vo.ProfileVO;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public Result<ProfileVO> getProfile(@AuthenticationPrincipal User user) {
        ProfileVO profile = profileService.getProfile(user.getId());
        return Result.success(profile);
    }

    @PutMapping
    public Result<ProfileVO> updateProfile(@AuthenticationPrincipal User user,
                                           @Valid @RequestBody ProfileRequest request) {
        ProfileVO profile = profileService.updateProfile(user.getId(), request);
        return Result.success(profile);
    }
}