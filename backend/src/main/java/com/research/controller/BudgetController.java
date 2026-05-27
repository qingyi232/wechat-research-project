package com.research.controller;

import com.research.entity.ProjectBudget;
import com.research.service.BudgetService;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) Long leaderId) {
        return Result.success(budgetService.pageList(page, size, keyword, status, leaderId));
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(budgetService.getById(id));
    }

    @PostMapping("/submit")
    public Result<?> submit(@RequestBody ProjectBudget budget) {
        return budgetService.submit(budget);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody ProjectBudget budget) {
        budgetService.updateById(budget);
        return Result.success("修改成功");
    }

    @PostMapping("/review")
    public Result<?> review(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        Long id = Long.valueOf(params.get("id").toString());
        String action = (String) params.get("action");
        String comment = (String) params.get("comment");
        return budgetService.review(id, userId, role, action, comment);
    }

    @PostMapping("/seal/{id}")
    public Result<?> seal(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        return budgetService.seal(id, userId, role);
    }

    @PostMapping("/finalApprove/{id}")
    public Result<?> finalApprove(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        return budgetService.finalApprove(id, userId, role);
    }
}
