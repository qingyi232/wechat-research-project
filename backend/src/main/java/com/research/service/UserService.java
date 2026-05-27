package com.research.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.research.entity.User;
import com.research.mapper.UserMapper;
import com.research.util.JwtUtil;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Result<?> register(User user) {
        User existing = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(0);
        save(user);
        return Result.success("注册成功，请等待管理员审核");
    }

    public Result<?> login(String username, String password) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error("账号待审核，请联系管理员");
        }
        if (user.getStatus() == 2) {
            return Result.error("账号已被禁用");
        }
        User detail = baseMapper.selectUserWithCollege(user.getId());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        detail.setToken(token);
        return Result.success("登录成功", detail);
    }

    public IPage<User> pageList(int page, int size, String keyword, String role, Long collegeId, Integer status) {
        return baseMapper.selectUserPage(new Page<>(page, size), keyword, role, collegeId, status);
    }

    public Result<?> audit(Long id, Integer status) {
        User user = getById(id);
        if (user == null) return Result.error("用户不存在");
        user.setStatus(status);
        updateById(user);
        return Result.success(status == 1 ? "审核通过" : "已禁用");
    }

    public Result<?> resetPassword(Long id) {
        User user = getById(id);
        if (user == null) return Result.error("用户不存在");
        user.setPassword(passwordEncoder.encode("123456"));
        updateById(user);
        return Result.success("密码已重置为 123456");
    }

    public Result<?> changePassword(Long userId, String oldPwd, String newPwd) {
        User user = getById(userId);
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            return Result.error("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPwd));
        updateById(user);
        return Result.success("密码修改成功");
    }
}
