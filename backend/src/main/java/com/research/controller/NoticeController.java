package com.research.controller;

import com.research.service.NoticeService;
import com.research.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(noticeService.pageList(page, size, userId));
    }

    @GetMapping("/unread")
    public Result<?> unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(noticeService.unreadCount(userId));
    }

    @PutMapping("/read/{id}")
    public Result<?> markRead(@PathVariable Long id) {
        noticeService.markRead(id);
        return Result.success("已读");
    }

    @PostMapping("/send")
    public Result<?> send(@RequestBody java.util.Map<String, Object> params, HttpServletRequest request) {
        Long senderId = (Long) request.getAttribute("userId");
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        String type = (String) params.get("type");
        Object receiverIdObj = params.get("receiverId");
        Long receiverId = receiverIdObj != null ? Long.valueOf(receiverIdObj.toString()) : null;
        noticeService.sendNotice(title, content, type != null ? type : "SYSTEM", senderId, receiverId);
        return Result.success("通知发送成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success("删除成功");
    }
}
