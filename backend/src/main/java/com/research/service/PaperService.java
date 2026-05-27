package com.research.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.research.entity.Approval;
import com.research.entity.ResearchPaper;
import com.research.mapper.ApprovalMapper;
import com.research.mapper.PaperMapper;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaperService extends ServiceImpl<PaperMapper, ResearchPaper> {

    @Autowired
    private ApprovalMapper approvalMapper;

    public IPage<ResearchPaper> pageList(int page, int size, String keyword, String type, String status, Long authorId) {
        return baseMapper.selectPaperPage(new Page<>(page, size), keyword, type, status, authorId);
    }

    public Result<?> submit(ResearchPaper paper) {
        paper.setStatus("PENDING");
        save(paper);
        return Result.success("论文/专著录入已提交");
    }

    public Result<?> review(Long id, Long reviewerId, String action, String comment) {
        ResearchPaper paper = getById(id);
        if (paper == null) return Result.error("记录不存在");

        Approval record = new Approval();
        record.setBizType("PAPER");
        record.setBizId(id);
        record.setAction(action);
        record.setOperatorId(reviewerId);
        record.setComment(comment);
        approvalMapper.insert(record);

        if ("APPROVE".equals(action)) {
            paper.setStatus("APPROVED");
        } else {
            paper.setStatus("REJECTED");
        }
        paper.setReviewerId(reviewerId);
        paper.setReviewComment(comment);
        paper.setReviewTime(LocalDateTime.now());
        updateById(paper);
        return Result.success("操作成功");
    }
}
