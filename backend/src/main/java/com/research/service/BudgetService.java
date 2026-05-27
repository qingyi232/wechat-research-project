package com.research.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.research.entity.Approval;
import com.research.entity.ProjectBudget;
import com.research.mapper.ApprovalMapper;
import com.research.mapper.BudgetMapper;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BudgetService extends ServiceImpl<BudgetMapper, ProjectBudget> {

    @Autowired
    private ApprovalMapper approvalMapper;

    public IPage<ProjectBudget> pageList(int page, int size, String keyword, String status, Long leaderId) {
        return baseMapper.selectBudgetPage(new Page<>(page, size), keyword, status, leaderId);
    }

    public Result<?> submit(ProjectBudget budget) {
        budget.setStatus("PENDING");
        save(budget);
        return Result.success("预算编制已提交");
    }

    public Result<?> review(Long id, Long reviewerId, String role, String action, String comment) {
        ProjectBudget budget = getById(id);
        if (budget == null) return Result.error("预算不存在");
        if (!"SCHOOL_ADMIN".equals(role) && !"COLLEGE_ADMIN".equals(role)) return Result.error("预算审核需由科研主管操作");
        if (!"PENDING".equals(budget.getStatus())) return Result.error("当前状态不可审核");

        Approval record = new Approval();
        record.setBizType("BUDGET");
        record.setBizId(id);
        record.setAction(action);
        record.setOperatorId(reviewerId);
        record.setComment(comment);
        approvalMapper.insert(record);

        if ("APPROVE".equals(action)) {
            budget.setStatus("APPROVED");
        } else if ("REJECT".equals(action)) {
            budget.setStatus("REJECTED");
        }
        budget.setReviewerId(reviewerId);
        budget.setReviewComment(comment);
        budget.setReviewTime(LocalDateTime.now());
        updateById(budget);
        return Result.success("操作成功");
    }

    public Result<?> seal(Long id, Long sealerId, String role) {
        ProjectBudget budget = getById(id);
        if (budget == null) return Result.error("预算不存在");
        if (!"FINANCE_ADMIN".equals(role)) return Result.error("预算盖章需由财务主管操作");
        if (!"APPROVED".equals(budget.getStatus())) return Result.error("预算尚未审核通过，无法盖章");
        budget.setStatus("SEALED");
        budget.setSealerId(sealerId);
        budget.setSealTime(LocalDateTime.now());
        updateById(budget);

        Approval record = new Approval();
        record.setBizType("BUDGET");
        record.setBizId(id);
        record.setAction("SEAL");
        record.setOperatorId(sealerId);
        record.setComment("预算盖章完成");
        approvalMapper.insert(record);

        return Result.success("盖章完成");
    }

    public Result<?> finalApprove(Long id, Long approverId, String role) {
        ProjectBudget budget = getById(id);
        if (budget == null) return Result.error("预算不存在");
        if (!"SCHOOL_ADMIN".equals(role) && !"COLLEGE_ADMIN".equals(role)) return Result.error("预算最终审批需由科研主管操作");
        if (!"SEALED".equals(budget.getStatus())) return Result.error("预算尚未盖章，无法审批");
        budget.setStatus("FINAL_APPROVED");
        updateById(budget);

        Approval record = new Approval();
        record.setBizType("BUDGET");
        record.setBizId(id);
        record.setAction("FINAL_APPROVE");
        record.setOperatorId(approverId);
        record.setComment("预算审批通过，已生成审批单");
        approvalMapper.insert(record);

        return Result.success("预算审批完成");
    }
}
