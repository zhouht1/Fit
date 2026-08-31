package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.SleepLog;
import com.fit.mapper.SleepLogMapper;
import com.fit.service.SleepLogService;
import org.springframework.stereotype.Service;

@Service
public class SleepLogServiceImpl extends ServiceImpl<SleepLogMapper, SleepLog> implements SleepLogService {
}