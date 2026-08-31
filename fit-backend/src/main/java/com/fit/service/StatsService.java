package com.fit.service;

import com.fit.vo.PersonalRecordVO;
import com.fit.vo.ProgressiveOverloadVO;
import com.fit.vo.StreakVO;

import java.util.List;

public interface StatsService {
    List<PersonalRecordVO> getPersonalRecords(Long userId);
    List<ProgressiveOverloadVO> getProgressiveOverload(Long userId);
    StreakVO getStreak(Long userId);
}