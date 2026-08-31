package com.fit.controller;

import com.fit.common.Result;
import com.fit.entity.User;
import com.fit.service.TodayService;
import com.fit.vo.TodayVO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/today")
public class TodayController {

    private final TodayService todayService;

    public TodayController(TodayService todayService) {
        this.todayService = todayService;
    }

    @GetMapping
    public Result<TodayVO> getToday(@AuthenticationPrincipal User user) {
        return Result.success(todayService.getToday(user.getId()));
    }
}