package com.research.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.research.entity.Approval;
import com.research.entity.ResearchPatent;
import com.research.mapper.ApprovalMapper;
import com.research.mapper.PatentMapper;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PatentService extends ServiceImpl<PatentMapper, ResearchPatent> {

    @Autowired
    private ApprovalMapper approvalMapper;

    public IPage<ResearchPatent> pageList(int page, int size, String keyword, String status, Long inventorId) {
        return baseMapper.selectPatentPage(new Page<>(page, size), keyword, status, inventorId);
    }

    public Result<?> submit(ResearchPatent patent) {
        patent.setStatus("PENDING_SEAL");
        save(patent);
        return Result.success("专利盖章申请已提交");
    }

    public Result<?> review(Long id, Long reviewerId, String action, String comment) {
        ResearchPatent patent = getById(id);
        if (patent == null) return Result.error("专利不存在");

        Approval record = new Approval();
        record.setBizType("PATENT");
        record.setBizId(id);
        record.setAction(action);
        record.setOperatorId(reviewerId);
        record.setComment(comment);
        approvalMapper.insert(record);

        switch (action) {
            case "SEAL":
                patent.setStatus("SEALED");
                break;
            case "UNDER_REVIEW":
                patent.setStatus("UNDER_REVIEW");
                break;
            case "AUTHORIZE":
                patent.setStatus("AUTHORIZED");
                break;
            case "REJECT":
                patent.setStatus("REJECTED");
                break;
            default:
                break;
        }
        patent.setReviewerId(reviewerId);
        patent.setReviewComment(comment);
        patent.setReviewTime(LocalDateTime.now());
        updateById(patent);
        return Result.success("操作成功");
    }
}
