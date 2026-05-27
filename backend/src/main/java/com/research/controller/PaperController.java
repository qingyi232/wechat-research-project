package com.research.controller;

import com.research.entity.ResearchPaper;
import com.research.service.PaperService;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/paper")
public class PaperController {

    @Autowired
    private PaperService paperService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) Long authorId) {
        return Result.success(paperService.pageList(page, size, keyword, type, status, authorId));
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(paperService.getById(id));
    }

    @PostMapping("/submit")
    public Result<?> submit(@RequestBody ResearchPaper paper) {
        return paperService.submit(paper);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody ResearchPaper paper) {
        paperService.updateById(paper);
        return Result.success("修改成功");
    }

    @PostMapping("/review")
    public Result<?> review(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long id = Long.valueOf(params.get("id").toString());
        String action = (String) params.get("action");
        String comment = (String) params.get("comment");
        return paperService.review(id, userId, action, comment);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        paperService.removeById(id);
        return Result.success("删除成功");
    }
}
