package com.fit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fit.entity.PersonalRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonalRecordMapper extends BaseMapper<PersonalRecord> {
}