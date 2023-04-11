###########################################################################################
CREATE SCHEMA IF NOT EXISTS `api-facade`;
USE `api-facade`;

CREATE TABLE IF NOT EXISTS `api-facade`.`api_digest`
(
    `id`          BIGINT UNSIGNED        NOT NULL COMMENT '主键' PRIMARY KEY,
    `account_id`  BIGINT UNSIGNED        NOT NULL COMMENT '创建账户主键',
    `api_name`    VARCHAR(256)           NOT NULL COMMENT '接口名称',
    `description` VARCHAR(1024)          NULL COMMENT '接口描述',
    `method`      TINYINT                NOT NULL COMMENT '请求方法',
    `url`         VARCHAR(512)           NOT NULL COMMENT '接口地址',
    `api_status`  TINYINT  DEFAULT 0     NOT NULL COMMENT '接口状态',
    `is_deleted`  TINYINT  DEFAULT 0     NOT NULL COMMENT '是否删除（1 表示删除，0 表示未删除）',
    `create_time` DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间'
) COMMENT '接口摘要';

CREATE TABLE IF NOT EXISTS `api-facade`.`api_format`
(
    `id`              BIGINT UNSIGNED        NOT NULL COMMENT '主键' PRIMARY KEY,
    `digest_id`       BIGINT UNSIGNED        NOT NULL COMMENT '接口摘要主键',
    `request_param`   TEXT                   NULL COMMENT '请求参数',
    `request_header`  TEXT                   NULL COMMENT '请求头',
    `request_body`    TEXT                   NULL COMMENT '请求体',
    `response_header` TEXT                   NULL COMMENT '响应头',
    `response_body`   TEXT                   NULL COMMENT '响应体',
    `is_deleted`      TINYINT  DEFAULT 0     NOT NULL COMMENT '是否删除（1 表示删除，0 表示未删除）',
    `create_time`     DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间'
) COMMENT '接口格式';

# 接口文档，可单独用一个表存
/*CREATE TABLE IF NOT EXISTS `api-facade`.`api_document`
(
    `id`          BIGINT UNSIGNED        NOT NULL COMMENT '主键' PRIMARY KEY,
    `digest_id`   BIGINT UNSIGNED        NOT NULL COMMENT '接口摘要主键',
    `content`     TEXT                   NULL COMMENT '文档内容',
    `is_deleted`  TINYINT  DEFAULT 0     NOT NULL COMMENT '是否删除（1 表示删除，0 表示未删除）',
    `create_time` DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间'
) COMMENT '接口文档';*/

CREATE TABLE IF NOT EXISTS `api-facade`.`api_quantity_usage`
(
    `id`           BIGINT UNSIGNED               NOT NULL COMMENT '主键' PRIMARY KEY,
    `digest_id`    BIGINT UNSIGNED               NOT NULL COMMENT '接口摘要主键',
    `total`        BIGINT UNSIGNED DEFAULT 0     NOT NULL COMMENT '总调用次数',
    `failure`      BIGINT UNSIGNED DEFAULT 0     NOT NULL COMMENT '失败调用次数',
    `stock`        BIGINT UNSIGNED DEFAULT 0     NOT NULL COMMENT '调用次数存量',
    `locked_stock` BIGINT UNSIGNED DEFAULT 0     NOT NULL COMMENT '锁定的调用次数存量',
    `usage_status` TINYINT         DEFAULT 0     NOT NULL COMMENT '用法状态',
    `is_deleted`   TINYINT         DEFAULT 0     NOT NULL COMMENT '是否删除（1 表示删除，0 表示未删除）',
    `create_time`  DATETIME        DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time`  DATETIME        DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间'
) COMMENT '接口计数用法';

CREATE TABLE IF NOT EXISTS `api-facade`.`user_quantity_usage`
(
    `id`           BIGINT UNSIGNED               NOT NULL COMMENT '主键' PRIMARY KEY,
    `account_id`   BIGINT UNSIGNED               NOT NULL COMMENT '账户主键',
    `digest_id`    BIGINT UNSIGNED               NOT NULL COMMENT '接口摘要主键',
    `total`        BIGINT UNSIGNED DEFAULT 0     NOT NULL COMMENT '总调用次数',
    `failure`      BIGINT UNSIGNED DEFAULT 0     NOT NULL COMMENT '失败调用次数',
    `stock`        BIGINT UNSIGNED DEFAULT 0     NOT NULL COMMENT '调用次数存量',
    `usage_status` TINYINT         DEFAULT 0     NOT NULL COMMENT '用法状态',
    `is_deleted`   TINYINT         DEFAULT 0     NOT NULL COMMENT '是否删除（1 表示删除，0 表示未删除）',
    `create_time`  DATETIME        DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time`  DATETIME        DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间'
) COMMENT '用户接口计数用法';

###########################################################################################
CREATE SCHEMA IF NOT EXISTS `api-security`;
USE `api-security`;

DROP TABLE IF EXISTS `api-security`.`user_account`;
CREATE TABLE IF NOT EXISTS `api-security`.`user_account`
(
    `id`             BIGINT UNSIGNED        NOT NULL COMMENT '主键' PRIMARY KEY,
    `username`       VARCHAR(256)           NOT NULL COMMENT '账户名',
    `password`       VARCHAR(512)           NOT NULL COMMENT '密码',
    `email`          VARCHAR(256)           NULL COMMENT '邮箱',
    `phone_number`   VARCHAR(256)           NULL COMMENT '手机号',
    `authority`      VARCHAR(256)           NOT NULL COMMENT '权限',
    `account_key`    VARCHAR(512)           NULL COMMENT '帐户密钥',
    `access_key`     VARCHAR(512)           NULL COMMENT '访问密钥',
    `account_status` TINYINT  DEFAULT 0     NOT NULL COMMENT '账号状态',
    `is_deleted`     TINYINT  DEFAULT 0     NOT NULL COMMENT '是否删除（1 表示删除，0 表示未删除）',
    `create_time`    DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time`    DATETIME DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间'
) COMMENT '用户帐户';

DROP TABLE `api-security`.`user_profile`;
CREATE TABLE IF NOT EXISTS `api-security`.`user_profile`
(
    `id`          BIGINT UNSIGNED        NOT NULL COMMENT '主键' PRIMARY KEY,
    `account_id`  BIGINT UNSIGNED        NOT NULL COMMENT '账户主键',
    `avatar`      VARCHAR(512)           NULL COMMENT '头像',
    `nickname`    VARCHAR(256)           NULL COMMENT '昵称',
    `website`     VARCHAR(256)           NULL COMMENT '个人网站',
    `github`      VARCHAR(256)           NULL COMMENT 'GitHub',
    `gitee`       VARCHAR(256)           NULL COMMENT 'Gitee',
    `biography`   VARCHAR(1024)          NULL COMMENT '个人简介',
    `ip_address`  VARCHAR(256)           NULL COMMENT 'IP 地址',
    `ip_origin`   VARCHAR(256)           NULL COMMENT 'IP 属地',
    `last_login_time`  VARCHAR(256)           NULL COMMENT '上次登陆时间',
    `is_deleted`  TINYINT  DEFAULT 0     NOT NULL COMMENT '是否删除（1 表示删除，0 表示未删除）',
    `create_time` DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间'
) COMMENT '用户资料';

CREATE TABLE IF NOT EXISTS `api-security`.`user_order`
(
    `id`           BIGINT UNSIGNED        NOT NULL COMMENT '主键' PRIMARY KEY,
    `order_sn`     VARCHAR(512)           NOT NULL COMMENT '订单编号',
    `account_id`   BIGINT UNSIGNED        NOT NULL COMMENT '账户主键',
    `digest_id`    BIGINT UNSIGNED        NOT NULL COMMENT '接口摘要主键',
    `usage_id`     BIGINT UNSIGNED        NOT NULL COMMENT '用户接口用法主键',
    `usage_type`   TINYINT                NOT NULL COMMENT '接口用法类型',
    `order_status` TINYINT  DEFAULT 0     NOT NULL COMMENT '订单状态',
    `is_deleted`   TINYINT  DEFAULT 0     NOT NULL COMMENT '是否删除（1 表示删除，0 表示未删除）',
    `create_time`  DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time`  DATETIME DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间'
) COMMENT '用户接口订单';

###########################################################################################

