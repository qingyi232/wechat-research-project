package com.research.controller;

import com.research.entity.Project;
import com.research.service.ProjectService;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) Long leaderId,
                          @RequestParam(required = false) Long collegeId) {
        return Result.success(projectService.pageList(page, size, keyword, category, type, status, leaderId, collegeId));
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(projectService.getDetail(id));
    }

    @GetMapping("/members/{id}")
    public Result<?> members(@PathVariable Long id) {
        return Result.success(projectService.getMembers(id));
    }

    @GetMapping("/approvals/{id}")
    public Result<?> approvals(@PathVariable Long id) {
        return Result.success(projectService.getApprovals(id));
    }

    @PostMapping("/apply")
    public Result<?> apply(@RequestBody Map<String, Object> params) {
        Project project = new Project();
        project.setProjectName((String) params.get("projectName"));
        project.setProjectCategory((String) params.get("projectCategory"));
        project.setProjectType((String) params.get("projectType"));
        project.setProjectLevel((String) params.get("projectLevel"));
        project.setProjectSource((String) params.get("projectSource"));
        project.setLeaderId(Long.valueOf(params.get("leaderId").toString()));
        project.setCollegeId(Long.valueOf(params.get("collegeId").toString()));
        project.setDescription((String) params.get("description"));
        if (params.get("fundingAmount") != null) {
            project.setFundingAmount(new java.math.BigDecimal(params.get("fundingAmount").toString()));
        }
        List<Integer> memberIds = (List<Integer>) params.get("memberIds");
        List<Long> members = memberIds != null ? memberIds.stream().map(Long::valueOf).collect(java.util.stream.Collectors.toList()) : null;
        return projectService.apply(project, members);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Project project) {
        projectService.updateById(project);
        return Result.success("修改成功");
    }

    @PostMapping("/approve")
    public Result<?> approve(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        Long projectId = Long.valueOf(params.get("projectId").toString());
        String action = (String) params.get("action");
        String comment = (String) params.get("comment");
        return projectService.approve(projectId, userId, role, action, comment);
    }

    @PostMapping("/submitCompletion/{id}")
    public Result<?> submitCompletion(@PathVariable Long id) {
        return projectService.submitCompletion(id);
    }

    @GetMapping("/statistics")
    public Result<?> statistics(@RequestParam(required = false) Long collegeId) {
        return Result.success(projectService.getStatistics(collegeId));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        projectService.removeById(id);
        return Result.success("删除成功");
    }
}
