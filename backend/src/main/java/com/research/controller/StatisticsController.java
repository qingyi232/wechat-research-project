package com.research.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.research.entity.*;
import com.research.service.*;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PaperService paperService;
    @Autowired
    private PatentService patentService;
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    @Cacheable(value = "statistics", key = "'dashboard:' + #collegeId", unless = "#result == null")
    public Result<?> dashboard(@RequestParam(required = false) Long collegeId) {
        Map<String, Object> data = new HashMap<>();
        data.put("project", projectService.getStatistics(collegeId));

        long paperCount = paperService.count(new LambdaQueryWrapper<ResearchPaper>().eq(ResearchPaper::getStatus, "APPROVED"));
        long patentCount = patentService.count(new LambdaQueryWrapper<ResearchPatent>().eq(ResearchPatent::getStatus, "AUTHORIZED"));
        Map<String, Object> achievement = new HashMap<>();
        achievement.put("paperCount", paperCount);
        achievement.put("patentCount", patentCount);
        achievement.put("total", paperCount + patentCount);
        data.put("achievement", achievement);

        long teacherCount = userService.count(new LambdaQueryWrapper<User>().eq(User::getRole, "TEACHER").eq(User::getStatus, 1));
        data.put("teacherCount", teacherCount);

        return Result.success(data);
    }
}
