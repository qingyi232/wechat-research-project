package com.research.controller;

import com.research.entity.User;
import com.research.mapper.CollegeMapper;
import com.research.service.UserService;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CollegeMapper collegeMapper;

    @GetMapping("/info")
    public Result<?> getInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getById(userId));
    }

    @GetMapping("/detail/{id}")
    public Result<?> getDetail(@PathVariable Long id) {
        return Result.success(userService.getBaseMapper().selectUserWithCollege(id));
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String role,
                          @RequestParam(required = false) Long collegeId,
                          @RequestParam(required = false) Integer status) {
        return Result.success(userService.pageList(page, size, keyword, role, collegeId, status));
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody User user) {
        userService.updateById(user);
        return Result.success("修改成功");
    }

    @PutMapping("/audit/{id}")
    public Result<?> audit(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        return userService.audit(id, params.get("status"));
    }

    @PutMapping("/resetPwd/{id}")
    public Result<?> resetPassword(@PathVariable Long id) {
        return userService.resetPassword(id);
    }

    @PutMapping("/changePwd")
    public Result<?> changePassword(HttpServletRequest request, @RequestBody Map<String, String> params) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.changePassword(userId, params.get("oldPassword"), params.get("newPassword"));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/colleges")
    public Result<?> colleges() {
        return Result.success(collegeMapper.selectList(null));
    }

    @GetMapping("/teachers")
    public Result<?> teachers(@RequestParam(required = false) Long collegeId) {
        return Result.success(userService.lambdaQuery()
                .eq(User::getRole, "TEACHER")
                .eq(User::getStatus, 1)
                .eq(collegeId != null, User::getCollegeId, collegeId)
                .list());
    }
}
