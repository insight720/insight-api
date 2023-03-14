-- 数据库
CREATE SCHEMA IF NOT EXISTS `api`;
USE `api`;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`            BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    `user_name`     VARCHAR(256)                NULL COMMENT '用户昵称',
    `user_account`  VARCHAR(256)                NOT NULL COMMENT '账号',
    `user_avatar`   VARCHAR(1024)               NULL COMMENT '用户头像',
    `gender`        TINYINT                     NULL COMMENT '性别',
    `user_role`     VARCHAR(256) DEFAULT 'user' NOT NULL COMMENT '用户角色(user/admin)',
    `user_password` VARCHAR(512)                NOT NULL COMMENT '密码',
    `access_key`    VARCHAR(512)                NOT NULL COMMENT 'accessKey',
    `secret_key`    VARCHAR(512)                NOT NULL COMMENT 'secretKey',
    `create_time`   DATETIME     DEFAULT NOW()  NOT NULL COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT NOW()  NOT NULL ON UPDATE NOW() COMMENT '更新时间',
    `is_delete`     TINYINT      DEFAULT 0      NOT NULL COMMENT '是否删除(0-未删, 1-已删)',
    CONSTRAINT `uni_user_account` UNIQUE (`user_account`)
) COMMENT '用户';

-- 帖子表
CREATE TABLE IF NOT EXISTS `post`
(
    `id`             BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    `age`            INT COMMENT '年龄',
    `gender`         TINYINT  DEFAULT 0     NOT NULL COMMENT '性别（0-男, 1-女）',
    `education`      VARCHAR(512)           NULL COMMENT '学历',
    `place`          VARCHAR(512)           NULL COMMENT '地点',
    `job`            VARCHAR(512)           NULL COMMENT '职业',
    `contact`        VARCHAR(512)           NULL COMMENT '联系方式',
    `love_exp`       VARCHAR(512)           NULL COMMENT '感情经历',
    `content`        TEXT                   NULL COMMENT '内容（个人介绍）',
    `photo`          VARCHAR(1024)          NULL COMMENT '照片地址',
    `review_status`  INT      DEFAULT 0     NOT NULL COMMENT '状态（0-待审核, 1-通过, 2-拒绝）',
    `review_message` VARCHAR(512)           NULL COMMENT '审核信息',
    `view_num`       INT                    NOT NULL DEFAULT 0 COMMENT '浏览数',
    `thumb_num`      INT                    NOT NULL DEFAULT 0 COMMENT '点赞数',
    `user_id`        BIGINT                 NOT NULL COMMENT '创建用户 id',
    `create_time`    DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time`    DATETIME DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间',
    `is_delete`      TINYINT  DEFAULT 0     NOT NULL COMMENT '是否删除'
) COMMENT '帖子';

-- 接口信息
CREATE TABLE IF NOT EXISTS `api`.`api_info`
(
    `id`              BIGINT                 NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    `name`            VARCHAR(256)           NOT NULL COMMENT '名称',
    `description`     VARCHAR(256)           NULL COMMENT '描述',
    `url`             VARCHAR(512)           NOT NULL COMMENT '接口地址',
    `request_params`  TEXT                   NULL COMMENT '请求参数',
    `request_header`  TEXT                   NULL COMMENT '请求头',
    `response_header` TEXT                   NULL COMMENT '响应头',
    `status`          INT      DEFAULT 0     NOT NULL COMMENT '接口状态(0-关闭，1-开启)',
    `method`          VARCHAR(256)           NOT NULL COMMENT '请求类型',
    `user_id`         BIGINT                 NOT NULL COMMENT '创建人',
    `create_time`     DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间',
    `is_delete`       TINYINT  DEFAULT 0     NOT NULL COMMENT '是否删除(0-未删, 1-已删)'
) COMMENT '接口信息';

INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('许擎宇', '薛聪健', 'www.cary-king.net', '潘博涛', '谭聪健', 0, '石炫明', 9500534531);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('陆弘文', '白志强', 'www.leslee-kuhn.net', '潘懿轩', '马鸿涛', 0, '陈峻熙', 3982575846);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('毛建辉', '罗文', 'www.rosaria-kilback.io', '冯子默', '彭哲瀚', 0, '赵远航', 121776355);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`,
 `user_id`)
VALUES ('彭雨泽', '蔡煜祺', 'www.norris-bergstrom.biz', '董思源', '田晓博', 0, '潘擎宇', 740);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`,
 `user_id`)
VALUES ('傅志强', '陈梓晨', 'www.jordan-reinger.com', '金志强', '熊锦程', 0, '邓睿渊', 35542559);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`,
 `user_id`)
VALUES ('吕黎昕', '孔越彬', 'www.fe-okon.info', '万伟宸', '林昊然', 0, '孟荣轩', 1445);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`,
 `user_id`)
VALUES ('夏雪松', '许子骞', 'www.lashawna-legros.co', '蔡昊然', '胡鹏涛', 0, '钟立辉', 34075514);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`,
 `user_id`)
VALUES ('严钰轩', '阎志泽', 'www.kay-funk.biz', '莫皓轩', '郭黎昕', 0, '龚天宇', 70956);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`,
 `user_id`)
VALUES ('萧嘉懿', '曹熠彤', 'www.margarette-lindgren.biz', '田泽洋', '邓睿渊', 0, '梁志强', 98);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('杜驰', '冯思源', 'www.vashti-auer.org', '黎健柏', '武博文', 0, '李伟宸', 9);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('史金鑫', '蔡鹏涛', 'www.diann-keebler.org', '徐烨霖', '阎建辉', 0, '李烨伟', 125);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('林炫明', '贾旭尧', 'www.dotty-kuvalis.io', '梁雨泽', '龙伟泽', 0, '许智渊', 79998);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('何钰轩', '赖智宸', 'www.andy-adams.net', '崔思淼', '白鸿煊', 0, '邵振家', 7167482751);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('魏志强', '于立诚', 'www.ione-aufderhar.biz', '朱懿轩', '万智渊', 0, '唐昊强', 741098);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('严君浩', '金胤祥', 'www.duane-boyle.org', '雷昊焱', '侯思聪', 0, '郝思', 580514);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('姚皓轩', '金鹏', 'www.lyda-klein.biz', '杜昊强', '邵志泽', 0, '冯鸿涛', 6546);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('廖驰', '沈泽洋', 'www.consuelo-sipes.info', '彭昊然', '邓耀杰', 0, '周彬', 7761037);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('赖智渊', '邓志泽', 'www.emerson-mann.co', '熊明哲', '贺哲瀚', 0, '田鹏', 381422);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('许涛', '陆致远', 'www.vella-ankunding.name', '贾哲瀚', '莫昊焱', 0, '袁越彬', 4218096);
INSERT INTO `api`.`api_info`
(`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`)
VALUES ('吕峻熙', '沈鹏飞', 'www.shari-reichel.org', '郭鸿煊', '覃烨霖', 0, '熊黎昕', 493);

-- 用户调用接口关系表
CREATE TABLE IF NOT EXISTS `api`.`user_api_info`
(
    `id`          BIGINT                 NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    `user_id`     BIGINT                 NOT NULL COMMENT '调用用户 id',
    `api_info_id` BIGINT                 NOT NULL COMMENT '接口 id',
    `total_num`   INT      DEFAULT 0     NOT NULL COMMENT '总调用次数',
    `left_num`    INT      DEFAULT 0     NOT NULL COMMENT '剩余调用次数',
    `status`      INT      DEFAULT 0     NOT NULL COMMENT '状态(0-正常，1-禁用)',
    `create_time` DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NOW() NOT NULL ON UPDATE NOW() COMMENT '更新时间',
    `is_delete`   TINYINT  DEFAULT 0     NOT NULL COMMENT '是否删除(0-未删, 1-已删)'
) COMMENT '用户调用接口关系';