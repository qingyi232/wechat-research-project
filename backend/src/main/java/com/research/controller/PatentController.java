package com.research.controller;

import com.research.entity.ResearchPatent;
import com.research.service.PatentService;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/patent")
public class PatentController {

    @Autowired
    private PatentService patentService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) Long inventorId) {
        return Result.success(patentService.pageList(page, size, keyword, status, inventorId));
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(patentService.getById(id));
    }

    @PostMapping("/submit")
    public Result<?> submit(@RequestBody ResearchPatent patent) {
        return patentService.submit(patent);
    }

    @PostMapping("/review")
    public Result<?> review(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long id = Long.valueOf(params.get("id").toString());
        String action = (String) params.get("action");
        String comment = (String) params.get("comment");
        return patentService.review(id, userId, action, comment);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        patentService.removeById(id);
        return Result.success("删除成功");
    }
}
