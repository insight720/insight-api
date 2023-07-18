# Insight API

![Java](https://img.shields.io/badge/Java-17-red) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-blue) ![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2022-brightgreen) ![Spring Cloud Alibaba](https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2022-blueviolet) ![Ant Design Pro](https://img.shields.io/badge/Ant%20Design%20Pro-6-blue) ![License](https://img.shields.io/badge/License-Apache%202-9cf)

> 注意：
>
> - 此项目主题来自 [程序员鱼皮]([liyupi (程序员鱼皮) (github.com)](https://github.com/liyupi)) 的 API 开放平台项目，但改动和增加的内容占绝大部分。
> - 按原本的项目设计，项目并没有完成，之后如果有更新都会遵循语义化版本规范。
> - 以展示为目的，项目目前已经部署上线，如需学习该项目可以联系我。

## 项目简介

Insight API 是一个分布式接口开放平台。开发者可以注册并登录平台，然后在平台下单获取接口资源，并通过申请密钥来使用平台提供的客户端 SDK，从而在自己的代码中轻松使用平台提供的接口资源。平台管理员可以接入并发布接口，统计分析各接口调用情况，并对平台用户进行管理。（用户部分基本开发完成，可供部署上线展示，管理员部分尚待开发）

## 主要技术选型

### 后端

- JDK 17
- Spring Boot 3
- MySQL 8 和 Mybatis-Plus 3.5
- Redis 7 和 Redisson 3.22
- RocketMQ 5
- Spring Security、Spring Session
- Spring Cloud 2022 (OpenFeign、Gateway)
- Spring Cloud Alibaba 2022 (Nacos)

### 前端

这主要是一个后端项目，前端以展示响应结果为主要目的。

- React 18
- Ant Design Pro 6 脚手架

## 项目亮点

项目涉及非常多细节，这里仅列出部分内容：

- api-client 包封装了 JDK 的 HTTP 客户端，它是一个可引入的客户端 SDK 依赖，同时也是一个 Spring Boot 的 Stater。
- api-common 包封装了事务工具类，提供在 Spring 事务上下文中的一些简便方法，此外还设计了部分自定义参数校验注解。
- api-security 包仿照源码实现了 Spring Security 的验证码登录，能够结合框架本身的许多安全功能。
- api-gateway 包自定义网关 Filter 实现了全局请求日志，并结合客户端 SDK 对 api-provider 包的请求设计了较为完善的验签流程。
- api-security 包和 api-facade 包使用 RocketMQ 消息队列的功能特性，设计了订单消息系统，支持分布式事务的最终一致性。
- Redis 使用方面，将其数据回滚考虑到事务回滚中，还使用脚本确保作用于幂等性的令牌验证操作是原子的，此外使用 Redisson 信号量统计和限制接口调用次数。

## 项目展示

- 项目地址：https://insightapi.cn

  暂不提供测试账号，可以通过密码、手机或邮箱注册并登录。

  虽然项目是分布式的，但部署在同一台服务器上，内存紧张，可能会有卡顿。

- 项目结构图

<img src="https://insight-api-1316431501.cos.ap-shanghai.myqcloud.com/typora/jiegou.jpg" alt="jiegou" style="zoom:50%;" />

- 登录页面

  <img src="https://insight-api-1316431501.cos.ap-shanghai.myqcloud.com/typora/login.png"  />

- 测试调用页面

  ![](https://insight-api-1316431501.cos.ap-shanghai.myqcloud.com/typora/test-1689709195062-85.png)

- 订单列表页面

  ![](https://insight-api-1316431501.cos.ap-shanghai.myqcloud.com/typora/order-1689709248539-87.png)

- 订单消息系统流程图

  ![](https://insight-api-1316431501.cos.ap-shanghai.myqcloud.com/typora/%E8%AE%A1%E6%95%B0%E7%94%A8%E6%B3%95%E8%AE%A2%E5%8D%95%E6%B6%88%E6%81%AF%E7%B3%BB%E7%BB%9F.jpg)

- 网关验签和客户端 SDK 流程图

  ![](https://insight-api-1316431501.cos.ap-shanghai.myqcloud.com/typora/%E5%AE%A2%E6%88%B7%E7%AB%AF%20SDK%C2%A0%20%E8%B0%83%E7%94%A8%E6%B5%81%E7%A8%8B-1689709583577-93.jpg)