package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.Recovery;
import com.fit.mapper.RecoveryMapper;
import com.fit.service.RecoveryService;
import org.springframework.stereotype.Service;

@Service
public class RecoveryServiceImpl extends ServiceImpl<RecoveryMapper, Recovery> implements RecoveryService {
}