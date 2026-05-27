package com.research.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.research.entity.Notice;
import com.research.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {

    public IPage<Notice> pageList(int page, int size, Long receiverId) {
        return page(new Page<>(page, size),
                new LambdaQueryWrapper<Notice>()
                        .eq(receiverId != null, Notice::getReceiverId, receiverId)
                        .orderByDesc(Notice::getCreateTime));
    }

    public long unreadCount(Long receiverId) {
        return count(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getReceiverId, receiverId)
                .eq(Notice::getIsRead, 0));
    }

    public void markRead(Long id) {
        Notice notice = getById(id);
        if (notice != null) {
            notice.setIsRead(1);
            updateById(notice);
        }
    }

    public void sendNotice(String title, String content, String type, Long senderId, Long receiverId) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setType(type);
        notice.setSenderId(senderId);
        notice.setReceiverId(receiverId);
        notice.setIsRead(0);
        save(notice);
    }
}
