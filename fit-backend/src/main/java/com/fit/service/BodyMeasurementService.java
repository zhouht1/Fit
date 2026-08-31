package com.fit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fit.dto.BodyMeasurementRequest;
import com.fit.entity.BodyMeasurement;
import com.fit.vo.BodyMeasurementVO;
import com.fit.vo.ProgressVO;

import java.util.List;

public interface BodyMeasurementService extends IService<BodyMeasurement> {
    List<BodyMeasurementVO> getMeasurements(Long userId, String period);
    BodyMeasurementVO addMeasurement(Long userId, BodyMeasurementRequest request);
    List<ProgressVO> getProgress(Long userId, String period);
}