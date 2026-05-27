-- 高校科研项目管理系统 数据库初始化脚本
CREATE DATABASE IF NOT EXISTS research_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE research_db;

-- 1. 学院信息表
DROP TABLE IF EXISTS sys_college;
CREATE TABLE sys_college (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    college_name VARCHAR(100) NOT NULL COMMENT '学院名称',
    college_code VARCHAR(50) COMMENT '学院编码',
    description VARCHAR(500) COMMENT '描述',
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '学院信息表';

-- 2. 用户信息表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role VARCHAR(30) NOT NULL COMMENT '角色: TEACHER/COLLEGE_ADMIN/SCHOOL_ADMIN/FINANCE_ADMIN/SYSTEM_ADMIN',
    college_id BIGINT COMMENT '所属学院ID',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    title VARCHAR(50) COMMENT '职称',
    avatar VARCHAR(500) COMMENT '头像',
    status INT DEFAULT 0 COMMENT '0-待审核 1-正常 2-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    KEY idx_role (role),
    KEY idx_college (college_id)
) COMMENT '核心用户信息表';

-- 3. 科研项目信息表
DROP TABLE IF EXISTS research_project;
CREATE TABLE research_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_name VARCHAR(200) NOT NULL COMMENT '项目名称',
    project_category VARCHAR(30) NOT NULL COMMENT 'HORIZONTAL-横向/VERTICAL-纵向',
    project_type VARCHAR(30) COMMENT '细分类型',
    project_level VARCHAR(30) COMMENT '项目级别',
    project_source VARCHAR(200) COMMENT '项目来源',
    leader_id BIGINT NOT NULL COMMENT '负责人ID',
    college_id BIGINT COMMENT '所属学院ID',
    funding_amount DECIMAL(14,2) DEFAULT 0 COMMENT '经费金额(万元)',
    apply_date DATE COMMENT '申报日期',
    approval_date DATE COMMENT '立项日期',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    completion_date DATE COMMENT '结题日期',
    status VARCHAR(30) DEFAULT 'DRAFT' COMMENT '项目状态',
    description TEXT COMMENT '项目简介',
    attachment_url VARCHAR(500) COMMENT '附件',
    reject_reason VARCHAR(500) COMMENT '驳回原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    KEY idx_leader (leader_id),
    KEY idx_category (project_category),
    KEY idx_status (status)
) COMMENT '科研项目信息表';

-- 4. 项目成员表
DROP TABLE IF EXISTS project_member;
CREATE TABLE project_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    member_role VARCHAR(20) DEFAULT 'MEMBER' COMMENT 'LEADER/MEMBER',
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_project (project_id),
    KEY idx_user (user_id)
) COMMENT '项目成员关联表';

-- 5. 项目任务书表
DROP TABLE IF EXISTS project_task_book;
CREATE TABLE project_task_book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    objectives TEXT COMMENT '研究目标',
    research_content TEXT COMMENT '研究内容',
    expected_results TEXT COMMENT '预期成果',
    schedule TEXT COMMENT '研究计划',
    attachment_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'DRAFT',
    reviewer_id BIGINT,
    review_comment VARCHAR(500),
    review_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_project (project_id)
) COMMENT '项目任务书表';

-- 6. 项目经费预算表
DROP TABLE IF EXISTS project_budget;
CREATE TABLE project_budget (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    total_amount DECIMAL(14,2) DEFAULT 0,
    equipment_fee DECIMAL(14,2) DEFAULT 0 COMMENT '设备费',
    material_fee DECIMAL(14,2) DEFAULT 0 COMMENT '材料费',
    travel_fee DECIMAL(14,2) DEFAULT 0 COMMENT '差旅费',
    meeting_fee DECIMAL(14,2) DEFAULT 0 COMMENT '会议费',
    labor_fee DECIMAL(14,2) DEFAULT 0 COMMENT '劳务费',
    consult_fee DECIMAL(14,2) DEFAULT 0 COMMENT '咨询费',
    other_fee DECIMAL(14,2) DEFAULT 0 COMMENT '其他费用',
    remark VARCHAR(500),
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT 'DRAFT/PENDING/APPROVED/SEALED/REJECTED',
    reviewer_id BIGINT,
    review_comment VARCHAR(500),
    review_time DATETIME,
    sealer_id BIGINT,
    seal_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_project (project_id)
) COMMENT '项目经费预算表';

-- 7. 项目经费结算表
DROP TABLE IF EXISTS project_settlement;
CREATE TABLE project_settlement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    budget_id BIGINT,
    total_amount DECIMAL(14,2) DEFAULT 0,
    equipment_fee DECIMAL(14,2) DEFAULT 0,
    material_fee DECIMAL(14,2) DEFAULT 0,
    travel_fee DECIMAL(14,2) DEFAULT 0,
    meeting_fee DECIMAL(14,2) DEFAULT 0,
    labor_fee DECIMAL(14,2) DEFAULT 0,
    consult_fee DECIMAL(14,2) DEFAULT 0,
    other_fee DECIMAL(14,2) DEFAULT 0,
    remark VARCHAR(500),
    status VARCHAR(20) DEFAULT 'DRAFT',
    reviewer_id BIGINT,
    review_comment VARCHAR(500),
    review_time DATETIME,
    sealer_id BIGINT,
    seal_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_project (project_id)
) COMMENT '项目经费结算表';

-- 8. 论文专著表
DROP TABLE IF EXISTS research_paper;
CREATE TABLE research_paper (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(500) NOT NULL COMMENT '标题',
    type VARCHAR(20) NOT NULL COMMENT 'PAPER-论文/MONOGRAPH-专著',
    author_id BIGINT NOT NULL COMMENT '第一作者ID',
    co_authors VARCHAR(500) COMMENT '合作作者',
    project_id BIGINT COMMENT '关联项目ID',
    journal_name VARCHAR(200) COMMENT '期刊/出版社名称',
    publish_level VARCHAR(50) COMMENT '发表级别(SCI/EI/CSSCI/核心/普刊)',
    doi VARCHAR(100),
    issn VARCHAR(50),
    publish_date DATE COMMENT '发表日期',
    volume VARCHAR(50) COMMENT '卷期号',
    pages VARCHAR(50) COMMENT '页码',
    abstract_text TEXT COMMENT '摘要',
    keywords VARCHAR(200) COMMENT '关键词',
    attachment_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'PENDING',
    reviewer_id BIGINT,
    review_comment VARCHAR(500),
    review_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    KEY idx_author (author_id),
    KEY idx_project (project_id)
) COMMENT '论文专著信息表';

-- 9. 专利信息表
DROP TABLE IF EXISTS research_patent;
CREATE TABLE research_patent (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patent_name VARCHAR(300) NOT NULL COMMENT '专利名称',
    patent_type VARCHAR(30) COMMENT 'INVENTION/UTILITY/DESIGN',
    patent_no VARCHAR(50) COMMENT '专利号',
    application_no VARCHAR(50) COMMENT '申请号',
    inventor_id BIGINT NOT NULL COMMENT '发明人ID',
    co_inventors VARCHAR(500) COMMENT '合作发明人',
    project_id BIGINT COMMENT '关联项目',
    apply_date DATE COMMENT '申请日期',
    authorize_date DATE COMMENT '授权日期',
    status VARCHAR(30) DEFAULT 'DRAFT',
    attachment_url VARCHAR(500),
    reviewer_id BIGINT,
    review_comment VARCHAR(500),
    review_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    KEY idx_inventor (inventor_id)
) COMMENT '专利信息表';

-- 10. 审批记录表
DROP TABLE IF EXISTS approval_record;
CREATE TABLE approval_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    biz_type VARCHAR(30) NOT NULL COMMENT 'PROJECT/BUDGET/SETTLEMENT/PAPER/PATENT',
    biz_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'APPROVE/REJECT/SEAL',
    operator_id BIGINT NOT NULL,
    comment VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_biz (biz_type, biz_id)
) COMMENT '审批记录表';

-- 11. 系统通知表
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    type VARCHAR(30) COMMENT 'SYSTEM/PROJECT/BUDGET/ACHIEVEMENT',
    sender_id BIGINT,
    receiver_id BIGINT,
    is_read INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_receiver (receiver_id)
) COMMENT '系统通知表';

-- 12. 操作日志表
DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '操作用户名',
    module VARCHAR(50) COMMENT '操作模块',
    operation VARCHAR(100) COMMENT '操作类型',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT '请求IP',
    status INT COMMENT '1-成功 0-失败',
    error_msg VARCHAR(500) COMMENT '错误信息',
    execution_time BIGINT COMMENT '执行时长(ms)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user (user_id),
    KEY idx_time (create_time)
) COMMENT '操作日志表';

-- ==================== 示例数据 ====================

-- 学院数据
INSERT INTO sys_college (college_name, college_code, sort_order) VALUES
('计算机科学与技术学院', 'CS', 1),
('电子信息工程学院', 'EE', 2),
('机械工程学院', 'ME', 3),
('材料科学与工程学院', 'MSE', 4),
('经济管理学院', 'EM', 5),
('外国语学院', 'FL', 6),
('数学与统计学院', 'MATH', 7),
('物理与光电工程学院', 'PHY', 8);

-- 用户数据 (密码均为 123456, BCrypt 加密)
INSERT INTO sys_user (username, password, real_name, role, college_id, phone, email, title, status) VALUES
('admin', '$2a$10$KaUb9nOhuJOYgiUacS/XpuTCWvrbd14nHJIVLsDfRK4gdX7rZB332', '系统管理员', 'SYSTEM_ADMIN', NULL, '13800000001', 'admin@university.edu.cn', '高级工程师', 1),
('teacher1', '$2a$10$KaUb9nOhuJOYgiUacS/XpuTCWvrbd14nHJIVLsDfRK4gdX7rZB332', '张明远', 'TEACHER', 1, '13800000002', 'zhangmy@university.edu.cn', '教授', 1),
('teacher2', '$2a$10$KaUb9nOhuJOYgiUacS/XpuTCWvrbd14nHJIVLsDfRK4gdX7rZB332', '李思涵', 'TEACHER', 1, '13800000003', 'lish@university.edu.cn', '副教授', 1),
('teacher3', '$2a$10$KaUb9nOhuJOYgiUacS/XpuTCWvrbd14nHJIVLsDfRK4gdX7rZB332', '王建国', 'TEACHER', 2, '13800000004', 'wangjg@university.edu.cn', '教授', 1),
('teacher4', '$2a$10$KaUb9nOhuJOYgiUacS/XpuTCWvrbd14nHJIVLsDfRK4gdX7rZB332', '陈雅文', 'TEACHER', 3, '13800000005', 'chenyw@university.edu.cn', '讲师', 1),
('teacher5', '$2a$10$KaUb9nOhuJOYgiUacS/XpuTCWvrbd14nHJIVLsDfRK4gdX7rZB332', '赵志强', 'TEACHER', 2, '13800000006', 'zhaozq@university.edu.cn', '副教授', 1),
('college_admin1', '$2a$10$KaUb9nOhuJOYgiUacS/XpuTCWvrbd14nHJIVLsDfRK4gdX7rZB332', '刘学敏', 'COLLEGE_ADMIN', 1, '13800000010', 'liuxm@university.edu.cn', '教授', 1),
('college_admin2', '$2a$10$KaUb9nOhuJOYgiUacS/XpuTCWvrbd14nHJIVLsDfRK4gdX7rZB332', '孙维民', 'COLLEGE_ADMIN', 2, '13800000011', 'sunwm@university.edu.cn', '教授', 1),
('school_admin', '$2a$10$KaUb9nOhuJOYgiUacS/XpuTCWvrbd14nHJIVLsDfRK4gdX7rZB332', '周科研', 'SCHOOL_ADMIN', NULL, '13800000020', 'zhouky@university.edu.cn', '研究员', 1),
('finance_admin', '$2a$10$KaUb9nOhuJOYgiUacS/XpuTCWvrbd14nHJIVLsDfRK4gdX7rZB332', '吴财务', 'FINANCE_ADMIN', NULL, '13800000030', 'wucw@university.edu.cn', '高级会计师', 1);

-- 科研项目示例数据
INSERT INTO research_project (project_name, project_category, project_type, project_level, project_source, leader_id, college_id, funding_amount, apply_date, approval_date, start_date, end_date, status, description) VALUES
('基于深度学习的医学影像智能诊断系统研究', 'VERTICAL', 'NATIONAL', '国家级', '国家自然科学基金', 2, 1, 85.00, '2025-03-15', '2025-06-01', '2025-07-01', '2028-06-30', 'APPROVED', '本项目旨在研发基于深度学习的医学影像智能诊断系统，提升疾病早期检测的准确率和效率。'),
('新能源汽车电池管理系统关键技术研发', 'HORIZONTAL', 'COMMISSION', NULL, '比亚迪股份有限公司', 4, 2, 120.00, '2025-05-10', '2025-06-20', '2025-07-01', '2027-06-30', 'APPROVED', '与比亚迪合作开发新一代电池管理系统，提升电池安全性和续航能力。'),
('高性能复合材料在航空航天领域的应用研究', 'VERTICAL', 'PROVINCIAL', '省级', '省科技厅重点项目', 5, 3, 45.00, '2025-09-01', '2025-11-15', '2025-12-01', '2027-11-30', 'APPROVED', '研究高性能复合材料在极端环境下的力学性能及其在航空航天领域的应用。'),
('面向智慧城市的物联网安全协议设计与实现', 'VERTICAL', 'SCHOOL', '校级', '校级科研基金', 3, 1, 8.00, '2025-04-01', '2025-05-15', '2025-06-01', '2026-12-31', 'APPROVED', '设计面向智慧城市场景的轻量级物联网安全通信协议。'),
('区块链技术在供应链金融中的应用研究', 'HORIZONTAL', 'BIDDING', NULL, '中国银行科技创新招标', 6, 2, 65.00, '2026-01-10', '2026-03-01', '2026-04-01', '2028-03-31', 'PENDING_APPROVAL', '研究区块链技术在供应链金融风控中的应用方案与系统实现。'),
('人工智能辅助教学系统的设计与实践', 'VERTICAL', 'COLLEGE', '院级', '学院教改基金', 3, 1, 3.00, '2026-02-20', NULL, NULL, NULL, 'PENDING_REVIEW', '设计基于AI的智能教学辅助系统，提升教学质量与学生学习效率。'),
('5G通信基站节能优化算法研究', 'VERTICAL', 'NATIONAL', '国家级', '国家重点研发计划', 4, 2, 150.00, '2025-01-20', '2025-04-15', '2025-05-01', '2028-04-30', 'APPROVED', '针对5G基站高能耗问题，研发智能节能调度算法，降低运营商能耗成本。'),
('工业机器人精密控制系统研发', 'HORIZONTAL', 'COMMISSION', NULL, '富士康科技集团', 5, 3, 200.00, '2025-08-15', '2025-10-01', '2025-11-01', '2027-10-31', 'COMPLETED', '为富士康定制开发高精度工业机器人控制系统，提升生产线自动化水平。');

-- 项目成员数据
INSERT INTO project_member (project_id, user_id, member_role, sort_order) VALUES
(1, 2, 'LEADER', 0), (1, 3, 'MEMBER', 1),
(2, 4, 'LEADER', 0), (2, 6, 'MEMBER', 1),
(3, 5, 'LEADER', 0),
(4, 3, 'LEADER', 0), (4, 2, 'MEMBER', 1),
(5, 6, 'LEADER', 0), (5, 4, 'MEMBER', 1),
(6, 3, 'LEADER', 0),
(7, 4, 'LEADER', 0), (7, 6, 'MEMBER', 1),
(8, 5, 'LEADER', 0);

-- 经费预算数据
INSERT INTO project_budget (project_id, total_amount, equipment_fee, material_fee, travel_fee, meeting_fee, labor_fee, consult_fee, other_fee, status, remark) VALUES
(1, 85.00, 25.00, 10.00, 12.00, 5.00, 20.00, 8.00, 5.00, 'SEALED', '已通过财务审核并盖章'),
(2, 120.00, 40.00, 20.00, 15.00, 8.00, 25.00, 7.00, 5.00, 'SEALED', '企业合作项目预算'),
(3, 45.00, 15.00, 8.00, 6.00, 3.00, 8.00, 3.00, 2.00, 'APPROVED', '省级项目预算已审核通过'),
(4, 8.00, 2.00, 1.50, 1.00, 0.50, 2.00, 0.50, 0.50, 'SEALED', '校级项目预算'),
(7, 150.00, 50.00, 25.00, 20.00, 10.00, 30.00, 10.00, 5.00, 'SEALED', '国家重点研发计划预算');

-- 经费结算数据
INSERT INTO project_settlement (project_id, budget_id, total_amount, equipment_fee, material_fee, travel_fee, meeting_fee, labor_fee, consult_fee, other_fee, status, remark) VALUES
(8, NULL, 195.50, 38.00, 22.00, 14.50, 7.50, 24.00, 6.50, 5.00, 'SEALED', '项目已结题，经费结算完成');

-- 论文专著数据
INSERT INTO research_paper (title, type, author_id, co_authors, project_id, journal_name, publish_level, doi, publish_date, volume, pages, abstract_text, keywords, status) VALUES
('基于Transformer的医学图像分割方法研究', 'PAPER', 2, '李思涵,王建国', 1, 'IEEE Transactions on Medical Imaging', 'SCI', '10.1109/TMI.2025.12345', '2025-11-20', 'Vol.44 No.11', '3245-3258', '本文提出了一种基于Transformer架构的医学图像分割方法，在多个公开数据集上取得了领先的分割精度。', '深度学习;医学影像;Transformer;图像分割', 'APPROVED'),
('物联网安全协议的轻量化设计', 'PAPER', 3, '张明远', 4, '计算机学报', 'CSSCI', '10.11897/SP.J.1016.2026.0234', '2026-02-15', '49(2)', '234-248', '针对资源受限的物联网设备，设计了一种轻量级安全通信协议，显著降低了计算开销。', '物联网;安全协议;轻量化;智慧城市', 'APPROVED'),
('高性能碳纤维复合材料力学性能研究', 'PAPER', 5, NULL, 3, 'Composites Science and Technology', 'SCI', '10.1016/j.compscitech.2026.110234', '2026-03-10', 'Vol.245', '110234', '系统研究了碳纤维复合材料在高温高压环境下的力学性能变化规律。', '复合材料;碳纤维;力学性能;航空航天', 'APPROVED'),
('区块链驱动的供应链金融风控模型构建', 'PAPER', 6, '王建国', 5, '管理科学学报', '核心', NULL, '2026-04-01', '29(4)', '56-72', '提出了一种基于区块链的供应链金融风险控制模型。', '区块链;供应链金融;风险控制', 'PENDING'),
('人工智能技术在高等教育中的应用与展望', 'MONOGRAPH', 3, NULL, 6, '清华大学出版社', '专著', NULL, '2026-01-15', NULL, '共280页', '全面论述了AI技术在高等教育领域的应用现状、挑战与未来发展趋势。', 'AI;高等教育;智能教学', 'APPROVED');

-- 专利数据
INSERT INTO research_patent (patent_name, patent_type, patent_no, application_no, inventor_id, co_inventors, project_id, apply_date, authorize_date, status) VALUES
('一种基于深度学习的医学影像快速诊断方法', 'INVENTION', 'ZL202510123456.7', 'CN202510123456.7', 2, '李思涵', 1, '2025-08-20', '2026-03-15', 'AUTHORIZED'),
('新能源电池智能管理系统及控制方法', 'INVENTION', NULL, 'CN202510234567.8', 4, '赵志强', 2, '2025-12-10', NULL, 'UNDER_REVIEW'),
('工业机器人高精度运动控制装置', 'UTILITY', 'ZL202520345678.9', 'CN202520345678.9', 5, NULL, 8, '2025-09-25', '2026-01-20', 'AUTHORIZED'),
('物联网设备安全认证芯片', 'INVENTION', NULL, 'CN202610456789.0', 3, '张明远', 4, '2026-03-05', NULL, 'PENDING_SEAL');

-- 任务书数据
INSERT INTO project_task_book (project_id, objectives, research_content, expected_results, schedule, status) VALUES
(1, '构建高效的医学影像智能诊断模型，实现多种疾病的自动检测', '1.医学影像数据增强与预处理\n2.Transformer架构优化\n3.多模态融合诊断', '1.发表SCI论文3篇\n2.申请发明专利2项\n3.开发原型系统1套', '第一年：数据收集与模型设计\n第二年：实验验证与系统开发\n第三年：成果总结与推广应用', 'APPROVED'),
(7, '研发5G基站智能节能算法，实现能耗降低30%以上', '1.5G基站能耗建模\n2.智能调度算法设计\n3.大规模仿真验证', '1.发表高水平论文4篇\n2.申请专利3项\n3.与运营商合作试点', '第一年：理论建模\n第二年：算法实现\n第三年：实际部署测试', 'APPROVED');

-- 审批记录
INSERT INTO approval_record (biz_type, biz_id, action, operator_id, comment) VALUES
('PROJECT', 1, 'APPROVE', 9, '项目选题具有重要学术价值，同意立项'),
('PROJECT', 2, 'APPROVE', 9, '企业合作项目，经费充足，同意'),
('PROJECT', 3, 'APPROVE', 9, '省级项目，同意推荐'),
('PROJECT', 4, 'APPROVE', 9, '校级项目，同意立项'),
('PROJECT', 7, 'APPROVE', 9, '国家重点项目，同意'),
('PROJECT', 8, 'APPROVE', 9, '横向合作项目，同意'),
('BUDGET', 1, 'APPROVE', 9, '预算合理，同意'),
('BUDGET', 1, 'SEAL', 10, '预算盖章完成'),
('BUDGET', 2, 'APPROVE', 9, '预算合理'),
('BUDGET', 2, 'SEAL', 10, '盖章完成'),
('PAPER', 1, 'APPROVE', 9, 'SCI论文，审核通过'),
('PAPER', 2, 'APPROVE', 9, '核心期刊论文，审核通过'),
('PAPER', 3, 'APPROVE', 9, 'SCI论文，审核通过'),
('PATENT', 1, 'SEAL', 9, '专利盖章审核通过'),
('PATENT', 3, 'SEAL', 9, '实用新型专利审核通过');

-- 通知数据
INSERT INTO sys_notice (title, content, type, sender_id, receiver_id, is_read) VALUES
('项目立项通知', '您申报的"基于深度学习的医学影像智能诊断系统研究"项目已通过审核，正式立项。', 'PROJECT', 9, 2, 1),
('项目立项通知', '您申报的"新能源汽车电池管理系统关键技术研发"项目已通过审核。', 'PROJECT', 9, 4, 1),
('预算审核通知', '您的项目预算已通过审核并完成盖章。', 'BUDGET', 10, 2, 1),
('论文审核通知', '您的论文"基于Transformer的医学图像分割方法研究"已通过审核。', 'ACHIEVEMENT', 9, 2, 1),
('专利授权通知', '您的专利"一种基于深度学习的医学影像快速诊断方法"已获得授权。', 'ACHIEVEMENT', 9, 2, 0),
('新项目申报提醒', '2026年度国家自然科学基金项目申报工作已启动，请及时准备申报材料。', 'SYSTEM', 1, 2, 0),
('新项目申报提醒', '2026年度国家自然科学基金项目申报工作已启动，请及时准备申报材料。', 'SYSTEM', 1, 3, 0),
('结题提醒', '您负责的"面向智慧城市的物联网安全协议设计与实现"项目即将到期，请及时提交结题报告。', 'PROJECT', 9, 3, 0);
