package com.research.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.research.entity.TaskBook;
import com.research.mapper.TaskBookMapper;
import com.research.util.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskBookService extends ServiceImpl<TaskBookMapper, TaskBook> {

    public TaskBook getByProjectId(Long projectId) {
        return getOne(new LambdaQueryWrapper<TaskBook>().eq(TaskBook::getProjectId, projectId));
    }

    public Result<?> submit(TaskBook taskBook) {
        taskBook.setStatus("PENDING");
        save(taskBook);
        return Result.success("任务书已提交");
    }

    public Result<?> review(Long id, Long reviewerId, String action, String comment) {
        TaskBook tb = getById(id);
        if (tb == null) return Result.error("任务书不存在");
        tb.setReviewerId(reviewerId);
        tb.setReviewComment(comment);
        tb.setReviewTime(LocalDateTime.now());
        tb.setStatus("APPROVE".equals(action) ? "APPROVED" : "REJECTED");
        updateById(tb);
        return Result.success("操作成功");
    }
}
