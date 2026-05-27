package com.research.controller;

import com.research.entity.TaskBook;
import com.research.service.TaskBookService;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/taskbook")
public class TaskBookController {

    @Autowired
    private TaskBookService taskBookService;

    @GetMapping("/byProject/{projectId}")
    public Result<?> getByProject(@PathVariable Long projectId) {
        return Result.success(taskBookService.getByProjectId(projectId));
    }

    @PostMapping("/submit")
    public Result<?> submit(@RequestBody TaskBook taskBook) {
        return taskBookService.submit(taskBook);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody TaskBook taskBook) {
        taskBookService.updateById(taskBook);
        return Result.success("修改成功");
    }

    @PostMapping("/review/{id}")
    public Result<?> review(@PathVariable Long id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return taskBookService.review(id, userId, params.get("action"), params.get("comment"));
    }
}
