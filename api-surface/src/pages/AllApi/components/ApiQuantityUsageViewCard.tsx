import {ProCard, ProDescriptions} from '@ant-design/pro-components';
import {message, Space, Tag} from 'antd';
import React, {useEffect, useState} from 'react';
import {viewApiQuantityUsage} from "@/services/api-facade/apiQuantityUsageController";

/**
 * API 计数用法视图卡属性
 */
export type ApiQuantityUsageViewCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void;
    apiDigestVO?: API.ApiDigestVO;
};

/**
 * API 计数用法视图卡
 */
const ApiQuantityUsageViewCard: React.FC<ApiQuantityUsageViewCardProps> = (props: ApiQuantityUsageViewCardProps) => {

    // Api 摘要信息
    const {apiDigestVO} = props;

    /**
     * HTTP 方法的映射
     */
    const HttpMethodMap: Record<string, { value: string, color: string }> = {
        ['GET']: {
            value: 'GET',
            color: 'green'
        },
        ['HEAD']: {
            value: 'HEAD',
            color: 'blue'
        },
        ['POST']: {
            value: 'POST',
            color: 'magenta'
        },
        ['PUT']: {
            value: 'PUT',
            color: 'geekblue'
        },
        ['DELETE']: {
            value: 'DELETE',
            color: 'red'
        },
        ['OPTIONS']: {
            value: 'OPTIONS',
            color: 'cyan'
        },
        ['TRACE']: {
            value: 'TRACE',
            color: 'purple'
        },
        ['PATCH']: {
            value: 'PATCH',
            color: 'orange'
        }
    };

    /**
     * API 状态的映射
     */
    const ApiStatusMap: Record<number, { value: number, name: string, color: string }> = {
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
     * API 计数用法状态的映射
     */
    const ApiQuantityUsageStatusMap: Record<number, { value: number, name: string, color: string }> = {
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
     * 使用方法映射
     */
    const UsageTypeMap: Record<string, { value: string, color: string }> = {
        ['QUANTITY']: {
            value: '计数用法',
            color: 'green'
        },
    };

    // 当前接口计数用法 VO
    const [apiQuantityUsageVO, setApiQuantityUsageVO]
        = useState<API.ApiQuantityUsageVO>();

    // 全局初始状态
    // const {initialState} = useModel('@@initialState');

    // 登陆用户信息
    // const {currentUser} = initialState || {};

    const loadData = async () => {
        message.loading("加载中");
        try {
            const result = await viewApiQuantityUsage(
                {
                    digestId: apiDigestVO?.digestId || ""
                }
            )
            setApiQuantityUsageVO(result.data);
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
                <ProDescriptions bordered
                                 column={{xxl: 4, xl: 3, lg: 3, md: 3, sm: 2, xs: 1}}>
                    <ProDescriptions.Item label="接口名称" span={1}>
                        {apiDigestVO?.apiName}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="请求方法">
                        {apiDigestVO?.methodSet?.map((value) => (
                            <Space key={value}>
                                <Tag color={HttpMethodMap[value || "GET"].color}>
                                    {HttpMethodMap[value || "GET"].value}
                                </Tag>
                            </Space>
                        ))}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口地址">
                        {apiDigestVO?.url}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口描述" span={3}>
                        {apiDigestVO?.description}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口状态">
                        <Space>
                            <Tag color={ApiStatusMap[apiDigestVO?.apiStatus || 0].color}>
                                {ApiStatusMap[apiDigestVO?.apiStatus || 0].name}
                            </Tag>
                        </Space>
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="所有用法类型">
                        {apiDigestVO?.usageTypeSet?.map((type) => (
                            <Space key={type}>
                                <Tag color={UsageTypeMap[type || "QUANTITY"].color}>
                                    {UsageTypeMap[type || "QUANTITY"].value}
                                </Tag>
                            </Space>
                        ))}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口摘要创建时间">
                        {apiDigestVO?.createTime}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口摘要更新时间">
                        {apiDigestVO?.updateTime}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="计数用法状态">
                        <Space>
                            <Tag color={ApiQuantityUsageStatusMap[apiQuantityUsageVO?.usageStatus || 0].color}>
                                {ApiQuantityUsageStatusMap[apiQuantityUsageVO?.usageStatus || 0].name}
                            </Tag>
                        </Space>
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口计数用法创建时间">
                        {apiDigestVO?.createTime}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口计数用法更新时间">
                        {apiDigestVO?.updateTime}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="总调用次数">
                        {apiQuantityUsageVO?.total}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="失败调用次数">
                        {apiQuantityUsageVO?.failure}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="成功调用次数">
                        {apiQuantityUsageVO?.total || 0 - (apiQuantityUsageVO?.failure || 0)}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="调用次数存量">
                        {apiQuantityUsageVO?.stock}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="锁定的调用次数存量">
                        {apiQuantityUsageVO?.stock}
                    </ProDescriptions.Item>
                </ProDescriptions>
            </ProCard>
        </div>
    );
};

export default ApiQuantityUsageViewCard;