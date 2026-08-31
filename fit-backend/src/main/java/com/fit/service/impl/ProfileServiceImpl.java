package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.Profile;
import com.fit.mapper.ProfileMapper;
import com.fit.service.ProfileService;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl extends ServiceImpl<ProfileMapper, Profile> implements ProfileService {
}