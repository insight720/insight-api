import {ProCard, ProDescriptions,} from '@ant-design/pro-components';
import React from "react";
import {Avatar, Typography} from "antd";
import {UserOutlined} from "@ant-design/icons";

/**
 * 用户资料描述卡属性
 */
export type ProfileDescriptionCardProps = {
    currentUser?: API.LoginUserDTO;
};

/**
 * 用户资料描述卡
 */
const ProfileDescriptionCard: React.FC<ProfileDescriptionCardProps> = (props: ProfileDescriptionCardProps) => {
    // 当前登录用户
    const {currentUser} = props;

    return (
        <ProCard>

            <ProDescriptions title="资料内容" bordered
                             column={{xxl: 4, xl: 3, lg: 3, md: 3, sm: 2, xs: 1}}>

                <ProDescriptions.Item label="用户头像">
                    <Avatar
                        alt="头像无法显示"
                        size={{xs: 24, sm: 32, md: 40, lg: 64, xl: 80, xxl: 100}}
                        icon={<UserOutlined/>}
                        shape="square"
                        src={currentUser?.avatar}
                    />
                </ProDescriptions.Item>

                <ProDescriptions.Item label="用户昵称" span={2}>
                    {currentUser?.nickname || <Typography.Text italic>暂无昵称</Typography.Text>}
                </ProDescriptions.Item>

                <ProDescriptions.Item label="个人网站">
                    {currentUser?.website || <Typography.Text italic>未填写个人网站</Typography.Text>}
                </ProDescriptions.Item>

                <ProDescriptions.Item label="Github 地址">
                    {currentUser?.github || <Typography.Text italic>未填写 Github 地址</Typography.Text>}
                </ProDescriptions.Item>

                <ProDescriptions.Item label="Gitee 地址">
                    {currentUser?.gitee || <Typography.Text italic>未填写 Gitee 地址</Typography.Text>}
                </ProDescriptions.Item>

                <ProDescriptions.Item label="个人简介" span={3}>
                    {currentUser?.biography || <Typography.Text italic>暂无个人简介</Typography.Text>}
                </ProDescriptions.Item>

                <ProDescriptions.Item label="IP 地址">
                    {currentUser?.ipAddress || <Typography.Text italic>未获取到 IP 地址</Typography.Text>}
                </ProDescriptions.Item>

                <ProDescriptions.Item label="IP 属地">
                    {currentUser?.ipLocation || <Typography.Text italic>未获取到 IP 属地信息</Typography.Text>}
                </ProDescriptions.Item>

                <ProDescriptions.Item label="上次登录时间">
                    {currentUser?.lastLoginTime || <Typography.Text italic>暂未登录过系统</Typography.Text>}
                </ProDescriptions.Item>

            </ProDescriptions>

        </ProCard>
    );
};

export default ProfileDescriptionCard;
