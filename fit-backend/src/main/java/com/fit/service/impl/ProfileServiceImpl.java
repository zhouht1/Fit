package com.fit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.dto.ProfileRequest;
import com.fit.entity.Profile;
import com.fit.mapper.ProfileMapper;
import com.fit.service.ProfileService;
import com.fit.vo.ProfileVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl extends ServiceImpl<ProfileMapper, Profile> implements ProfileService {

    @Override
    public ProfileVO getProfile(Long userId) {
        Profile profile = getOne(new LambdaQueryWrapper<Profile>()
                .eq(Profile::getUserId, userId));
        if (profile == null) {
            return null;
        }
        return toVO(profile);
    }

    @Override
    @Transactional
    public ProfileVO updateProfile(Long userId, ProfileRequest request) {
        Profile profile = getOne(new LambdaQueryWrapper<Profile>()
                .eq(Profile::getUserId, userId));

        if (profile == null) {
            profile = new Profile();
            profile.setUserId(userId);
        }

        if (request.getName() != null) profile.setName(request.getName());
        if (request.getAge() != null) profile.setAge(request.getAge());
        if (request.getHeight() != null) profile.setHeight(request.getHeight());
        if (request.getWeight() != null) profile.setWeight(request.getWeight());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getFitnessGoal() != null) profile.setFitnessGoal(request.getFitnessGoal());
        if (request.getTrainingFrequency() != null) profile.setTrainingFrequency(request.getTrainingFrequency());
        if (request.getExperience() != null) profile.setExperience(request.getExperience());

        saveOrUpdate(profile);
        return toVO(profile);
    }

    private ProfileVO toVO(Profile profile) {
        return ProfileVO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .name(profile.getName())
                .age(profile.getAge())
                .height(profile.getHeight())
                .weight(profile.getWeight())
                .gender(profile.getGender())
                .fitnessGoal(profile.getFitnessGoal())
                .trainingFrequency(profile.getTrainingFrequency())
                .experience(profile.getExperience())
                .build();
    }
}