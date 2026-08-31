package com.fit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fit.dto.ProfileRequest;
import com.fit.entity.Profile;
import com.fit.vo.ProfileVO;

public interface ProfileService extends IService<Profile> {
    ProfileVO getProfile(Long userId);
    ProfileVO updateProfile(Long userId, ProfileRequest request);
}