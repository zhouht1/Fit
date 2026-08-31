package com.fit.controller;

import com.fit.common.Result;
import com.fit.entity.User;
import com.fit.service.StatsService;
import com.fit.vo.PersonalRecordVO;
import com.fit.vo.ProgressiveOverloadVO;
import com.fit.vo.StreakVO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/personal-records")
    public Result<List<PersonalRecordVO>> getPersonalRecords(@AuthenticationPrincipal User user) {
        return Result.success(statsService.getPersonalRecords(user.getId()));
    }

    @GetMapping("/progressive-overload")
    public Result<List<ProgressiveOverloadVO>> getProgressiveOverload(@AuthenticationPrincipal User user) {
        return Result.success(statsService.getProgressiveOverload(user.getId()));
    }

    @GetMapping("/streak")
    public Result<StreakVO> getStreak(@AuthenticationPrincipal User user) {
        return Result.success(statsService.getStreak(user.getId()));
    }
}