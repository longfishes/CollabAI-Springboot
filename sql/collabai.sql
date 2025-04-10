CREATE DATABASE IF NOT EXISTS collabai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE collabai;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户id',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(15) DEFAULT NULL COMMENT '手机号',
    `password` CHAR(32) DEFAULT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(1024) DEFAULT NULL COMMENT '头像url',
    `info` VARCHAR(80) DEFAULT NULL COMMENT '个人信息',
    `gender` TINYINT(1) DEFAULT 3 COMMENT '性别：1-男 2-女 3-未知',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `location` VARCHAR(50) DEFAULT NULL COMMENT '位置',
    `is_banned` TINYINT(1) DEFAULT 0 COMMENT '是否被封禁：0-否 1-是',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'ip地址',
    `ip_source` VARCHAR(50) DEFAULT NULL COMMENT 'ip来源',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户个人信息表';

-- 会议表
CREATE TABLE IF NOT EXISTS `meeting` (
    `id` CHAR(32) NOT NULL COMMENT '会议号',
    `title` VARCHAR(128) DEFAULT NULL COMMENT '标题',
    `holder_id` BIGINT DEFAULT NULL COMMENT '创建者id',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `speech_text` MEDIUMTEXT DEFAULT NULL COMMENT '语音转写内容',
    `md_content` MEDIUMTEXT DEFAULT NULL COMMENT '用户上传文档',
    `ai_summary` MEDIUMTEXT DEFAULT NULL COMMENT 'ai总结',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `cover_img` VARCHAR(1024) DEFAULT NULL COMMENT '封面图片url',
    PRIMARY KEY (`id`),
    KEY `idx_holder_id` (`holder_id`),
    CONSTRAINT `fk_meeting_holder` FOREIGN KEY (`holder_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议信息表';

-- 会议权限表
CREATE TABLE IF NOT EXISTS `meeting_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `meeting_id` CHAR(32) NOT NULL COMMENT '会议号',
    `user_id` BIGINT NOT NULL COMMENT '用户id',
    `auth_type` TINYINT(1) DEFAULT NULL COMMENT '权限类型：1-创建者holder 2-操作者operator 3-参与者participants',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_meeting_user` (`meeting_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_meeting_user_meeting` FOREIGN KEY (`meeting_id`) REFERENCES `meeting` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_meeting_user_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议权限表';

-- 用户好友表
CREATE TABLE IF NOT EXISTS `user_friend` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `user_id` BIGINT NOT NULL COMMENT '用户id',
    `friend_id` BIGINT NOT NULL COMMENT '关联的id',
    `type` TINYINT(1) DEFAULT NULL COMMENT '类型：1-申请request 2-好友friend 3-黑名单blacklist',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`),
    KEY `idx_friend_id` (`friend_id`),
    CONSTRAINT `fk_user_friend_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_friend_friend` FOREIGN KEY (`friend_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户好友表';
