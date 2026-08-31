package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.WaterLog;
import com.fit.mapper.WaterLogMapper;
import com.fit.service.WaterLogService;
import org.springframework.stereotype.Service;

@Service
public class WaterLogServiceImpl extends ServiceImpl<WaterLogMapper, WaterLog> implements WaterLogService {
}