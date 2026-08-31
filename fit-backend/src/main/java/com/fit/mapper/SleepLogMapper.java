package com.fit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fit.entity.SleepLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SleepLogMapper extends BaseMapper<SleepLog> {
}