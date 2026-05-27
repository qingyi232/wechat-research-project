package com.research.controller;

import com.research.entity.ProjectSettlement;
import com.research.service.SettlementService;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/settlement")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) Long leaderId) {
        return Result.success(settlementService.pageList(page, size, keyword, status, leaderId));
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(settlementService.getById(id));
    }

    @PostMapping("/submit")
    public Result<?> submit(@RequestBody ProjectSettlement settlement, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        return settlementService.submit(settlement, role);
    }

    @PostMapping("/review")
    public Result<?> review(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        Long id = Long.valueOf(params.get("id").toString());
        String action = (String) params.get("action");
        String comment = (String) params.get("comment");
        return settlementService.review(id, userId, role, action, comment);
    }

    @PostMapping("/seal/{id}")
    public Result<?> seal(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        return settlementService.seal(id, userId, role);
    }
}
