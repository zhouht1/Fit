package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.BodyMeasurement;
import com.fit.mapper.BodyMeasurementMapper;
import com.fit.service.BodyMeasurementService;
import org.springframework.stereotype.Service;

@Service
public class BodyMeasurementServiceImpl extends ServiceImpl<BodyMeasurementMapper, BodyMeasurement> implements BodyMeasurementService {
}