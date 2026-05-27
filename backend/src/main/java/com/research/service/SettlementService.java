package com.research.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.research.entity.Approval;
import com.research.entity.ProjectSettlement;
import com.research.mapper.ApprovalMapper;
import com.research.mapper.SettlementMapper;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SettlementService extends ServiceImpl<SettlementMapper, ProjectSettlement> {

    @Autowired
    private ApprovalMapper approvalMapper;

    public IPage<ProjectSettlement> pageList(int page, int size, String keyword, String status, Long leaderId) {
        return baseMapper.selectSettlementPage(new Page<>(page, size), keyword, status, leaderId);
    }

    public Result<?> submit(ProjectSettlement settlement, String role) {
        if (!"TEACHER".equals(role)) return Result.error("结算编制仅限普通教师操作");
        settlement.setStatus("PENDING");
        save(settlement);
        return Result.success("结算表已提交");
    }

    public Result<?> review(Long id, Long reviewerId, String role, String action, String comment) {
        ProjectSettlement settlement = getById(id);
        if (settlement == null) return Result.error("结算不存在");
        if (!"SCHOOL_ADMIN".equals(role) && !"COLLEGE_ADMIN".equals(role)) return Result.error("结算审核需由科研主管操作");
        if (!"PENDING".equals(settlement.getStatus())) return Result.error("当前状态不可审核");

        Approval record = new Approval();
        record.setBizType("SETTLEMENT");
        record.setBizId(id);
        record.setAction(action);
        record.setOperatorId(reviewerId);
        record.setComment(comment);
        approvalMapper.insert(record);

        if ("APPROVE".equals(action)) {
            settlement.setStatus("APPROVED");
        } else if ("REJECT".equals(action)) {
            settlement.setStatus("REJECTED");
        }
        settlement.setReviewerId(reviewerId);
        settlement.setReviewComment(comment);
        settlement.setReviewTime(LocalDateTime.now());
        updateById(settlement);
        return Result.success("操作成功");
    }

    public Result<?> seal(Long id, Long sealerId, String role) {
        ProjectSettlement settlement = getById(id);
        if (settlement == null) return Result.error("结算不存在");
        if (!"FINANCE_ADMIN".equals(role)) return Result.error("结算盖章需由财务主管操作");
        if (!"APPROVED".equals(settlement.getStatus())) return Result.error("结算尚未审核通过，无法盖章");
        settlement.setStatus("SEALED");
        settlement.setSealerId(sealerId);
        settlement.setSealTime(LocalDateTime.now());
        updateById(settlement);

        Approval record = new Approval();
        record.setBizType("SETTLEMENT");
        record.setBizId(id);
        record.setAction("SEAL");
        record.setOperatorId(sealerId);
        record.setComment("结算盖章完成");
        approvalMapper.insert(record);

        return Result.success("盖章完成");
    }
}
