import {ProCard, ProDescriptions} from '@ant-design/pro-components';
import {Avatar, message, Space, Tag, Typography} from 'antd';
import React, {useEffect, useState} from 'react';
import {useModel} from "@umijs/max";
import {viewApiCreator} from "@/services/api-security/securityController";
import {UserOutlined} from "@ant-design/icons";

/**
 * API 创建者视图卡属性
 */
export type ApiCreatorViewCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void;
    apiDigestVO?: API.ApiDigestVO;
};

/**
 * API 创建者视图卡
 */
const ApiCreatorViewCard: React.FC<ApiCreatorViewCardProps> = (props: ApiCreatorViewCardProps) => {

    // API 摘要信息
    const {apiDigestVO} = props;

    /**
     * 账户状态的映射
     */
    const AccountStatusMap: Record<number, { value: number, name: string, color: string }> = {
        [0]: {
            value: 0,
            name: '正常',
            color: 'green'
        },
        [1]: {
            value: 1,
            name: '错误',
            color: 'red'
        },
    };

    /**
     * 当前查看的 API 创建者视图
     */
    const [apiCreatorVO, setApiCreatorVO]
        = useState<API.ApiCreatorVO>();

    // 全局初始状态
    const {initialState} = useModel('@@initialState');

    // 登陆用户信息
    const {currentUser} = initialState || {};

    /**
     * 加载 API 创建者数据
     */
    const loadData = async () => {
        message.loading("加载中");
        try {
            const result = await viewApiCreator(
                {
                    accountId: currentUser?.accountId || "",
                }
            )
            setApiCreatorVO(result.data);
            message.destroy();
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || '加载失败，请重试！');
            return false;
        }
    };

    useEffect(() => {
        loadData();
    }, []);

    return (
        <div
            style={{background: '#F5F7FA'}}
        >
            <ProCard>
                <ProDescriptions title="接口创建者" bordered
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
                    <ProDescriptions.Item label="账户名" span={1}>
                        {apiCreatorVO?.username}
                    </ProDescriptions.Item>
                    {/*                    <ProDescriptions.Item label="请求方法">
                        {apiDigestVO?.methodSet?.map((value) => (
                            <Space key={value}>
                                <Tag color={HttpMethodMap[value || "GET"].color}>
                                    {HttpMethodMap[value || "GET"].value}
                                </Tag>
                            </Space>
                        ))}
                    </ProDescriptions.Item>*/}
                    <ProDescriptions.Item label="邮箱">
                        {apiCreatorVO?.emailAddress || <Typography.Text italic>暂未绑定邮箱</Typography.Text>}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="账号状态">
                        <Space>
                            <Tag color={AccountStatusMap[apiDigestVO?.apiStatus || 0].color}>
                                {AccountStatusMap[apiDigestVO?.apiStatus || 0].name}
                            </Tag>
                        </Space>
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

                    <ProDescriptions.Item label="IP 属地">
                        {currentUser?.ipLocation || <Typography.Text italic>未获取到 IP 属地信息</Typography.Text>}
                    </ProDescriptions.Item>

                    <ProDescriptions.Item label="上次登录时间">
                        {currentUser?.lastLoginTime || <Typography.Text italic>暂未登录过系统</Typography.Text>}
                    </ProDescriptions.Item>
                </ProDescriptions>
            </ProCard>
        </div>
    );
};

export default ApiCreatorViewCard;