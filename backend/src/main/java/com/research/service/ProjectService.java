package com.research.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.research.entity.Approval;
import com.research.entity.Project;
import com.research.entity.ProjectMember;
import com.research.mapper.ApprovalMapper;
import com.research.mapper.ProjectMapper;
import com.research.mapper.ProjectMemberMapper;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService extends ServiceImpl<ProjectMapper, Project> {

    @Autowired
    private ProjectMemberMapper memberMapper;
    @Autowired
    private ApprovalMapper approvalMapper;

    public IPage<Project> pageList(int page, int size, String keyword, String category, String type, String status, Long leaderId, Long collegeId) {
        return baseMapper.selectProjectPage(new Page<>(page, size), keyword, category, type, status, leaderId, collegeId);
    }

    public Project getDetail(Long id) {
        return baseMapper.selectProjectDetail(id);
    }

    @Transactional
    public Result<?> apply(Project project, List<Long> memberIds) {
        String initialStatus = resolveInitialStatus(project.getProjectCategory(), project.getProjectType());
        project.setStatus(initialStatus);
        project.setApplyDate(LocalDate.now());
        save(project);
        if (memberIds != null) {
            for (int i = 0; i < memberIds.size(); i++) {
                ProjectMember pm = new ProjectMember();
                pm.setProjectId(project.getId());
                pm.setUserId(memberIds.get(i));
                pm.setMemberRole("MEMBER");
                pm.setSortOrder(i + 1);
                memberMapper.insert(pm);
            }
        }
        ProjectMember leader = new ProjectMember();
        leader.setProjectId(project.getId());
        leader.setUserId(project.getLeaderId());
        leader.setMemberRole("LEADER");
        leader.setSortOrder(0);
        memberMapper.insert(leader);
        return Result.success("项目申报成功");
    }

    public Result<?> approve(Long projectId, Long operatorId, String operatorRole, String action, String comment) {
        Project project = getById(projectId);
        if (project == null) return Result.error("项目不存在");

        String currentStatus = project.getStatus();
        String roleError = validateApproveRole(currentStatus, operatorRole, project.getProjectCategory(), project.getProjectType());
        if (roleError != null) return Result.error(roleError);

        Approval record = new Approval();
        record.setBizType("PROJECT");
        record.setBizId(projectId);
        record.setAction(action);
        record.setOperatorId(operatorId);
        record.setComment(comment);
        approvalMapper.insert(record);

        if ("APPROVE".equals(action)) {
            String nextStatus = resolveNextStatus(currentStatus, project.getProjectCategory(), project.getProjectType());
            project.setStatus(nextStatus);
            if ("APPROVED".equals(nextStatus)) {
                project.setApprovalDate(LocalDate.now());
            } else if ("COMPLETED".equals(nextStatus)) {
                project.setCompletionDate(LocalDate.now());
            }
        } else if ("REJECT".equals(action)) {
            project.setStatus("REJECTED");
            project.setRejectReason(comment);
        }
        updateById(project);
        return Result.success("操作成功");
    }

    private String resolveInitialStatus(String category, String type) {
        if ("HORIZONTAL".equals(category) && "BIDDING".equals(type)) {
            return "PENDING_SEAL";
        }
        if ("VERTICAL".equals(category) && "COLLEGE".equals(type)) {
            return "PENDING_REVIEW";
        }
        return "PENDING_APPROVAL";
    }

    private String resolveNextStatus(String currentStatus, String category, String type) {
        switch (currentStatus) {
            case "PENDING_SEAL":
                return "SEALED";
            case "SEALED":
                return "PENDING_REVIEW";
            case "PENDING_REVIEW":
                if ("VERTICAL".equals(category) && "COLLEGE".equals(type)) {
                    return "PENDING_APPROVAL";
                }
                if ("HORIZONTAL".equals(category) && "BIDDING".equals(type)) {
                    return "PENDING_APPROVAL";
                }
                if ("VERTICAL".equals(category) && ("PROVINCIAL".equals(type) || "NATIONAL".equals(type))) {
                    return "PENDING_APPROVAL";
                }
                return "REVIEWED";
            case "REVIEWED":
                return "PENDING_APPROVAL";
            case "PENDING_APPROVAL":
                return "APPROVED";
            case "PENDING_COMPLETION":
                return "COMPLETED";
            default:
                return "APPROVED";
        }
    }

    private String validateApproveRole(String status, String role, String category, String type) {
        if (role == null) return "无法获取操作人角色";
        switch (status) {
            case "PENDING_SEAL":
                if (!"SCHOOL_ADMIN".equals(role)) return "投标/项目盖章需由学校科研主管操作";
                break;
            case "PENDING_REVIEW":
                if ("VERTICAL".equals(category) && "COLLEGE".equals(type)) {
                    if (!"COLLEGE_ADMIN".equals(role)) return "院级项目评审需由学院科研主管操作";
                } else {
                    if (!"SCHOOL_ADMIN".equals(role)) return "项目评审需由学校科研主管操作";
                }
                break;
            case "PENDING_APPROVAL":
                if (!"SCHOOL_ADMIN".equals(role)) return "项目审批需由学校科研主管操作";
                break;
            case "PENDING_COMPLETION":
                if (!"SCHOOL_ADMIN".equals(role)) return "结题审核需由学校科研主管操作";
                break;
            case "SEALED":
                if (!"SCHOOL_ADMIN".equals(role)) return "盖章后审核需由学校科研主管操作";
                break;
        }
        return null;
    }

    public Result<?> submitCompletion(Long projectId) {
        Project project = getById(projectId);
        if (project == null) return Result.error("项目不存在");
        project.setStatus("PENDING_COMPLETION");
        updateById(project);
        return Result.success("结题报告已提交");
    }

    public Map<String, Object> getStatistics(Long collegeId) {
        Map<String, Object> stats = new HashMap<>();
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (collegeId != null) wrapper.eq(Project::getCollegeId, collegeId);

        stats.put("total", count(wrapper));
        stats.put("horizontal", count(new LambdaQueryWrapper<Project>().eq(Project::getProjectCategory, "HORIZONTAL").eq(collegeId != null, Project::getCollegeId, collegeId)));
        stats.put("vertical", count(new LambdaQueryWrapper<Project>().eq(Project::getProjectCategory, "VERTICAL").eq(collegeId != null, Project::getCollegeId, collegeId)));
        stats.put("completed", count(new LambdaQueryWrapper<Project>().eq(Project::getStatus, "COMPLETED").eq(collegeId != null, Project::getCollegeId, collegeId)));
        stats.put("inProgress", count(new LambdaQueryWrapper<Project>().eq(Project::getStatus, "APPROVED").eq(collegeId != null, Project::getCollegeId, collegeId)));
        return stats;
    }

    public List<ProjectMember> getMembers(Long projectId) {
        return memberMapper.selectByProjectId(projectId);
    }

    public List<Approval> getApprovals(Long projectId) {
        return approvalMapper.selectByBiz("PROJECT", projectId);
    }
}
