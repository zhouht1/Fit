package com.fit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fit.entity.Profile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfileMapper extends BaseMapper<Profile> {
}