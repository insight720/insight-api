import {ProCard, ProDescriptions} from '@ant-design/pro-components';
import {Badge, message, Space, Tag} from 'antd';
import React, {useEffect, useState} from 'react';
import {viewUserApiFormatAndQuantityUsage} from "@/services/api-security/securityController";
import {useModel} from "@@/exports";

/**
 * 接口格式和用法卡片属性
 */
export type ApiFormatAndUsageCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void;
    userApiDigestVO?: API.UserApiDigestVO;
};

/**
 * 接口格式和用法卡片属性
 */
const ApiFormatAndUsageCard: React.FC<ApiFormatAndUsageCardProps> = (props: ApiFormatAndUsageCardProps) => {

    const {userApiDigestVO} = props;

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
     * 使用方法映射
     */
    const UsageTypeMap: Record<string, { value: string, color: string }> = {
        ['QUANTITY']: {
            value: '计数用法',
            color: 'green'
        },
    };

    /**
     * API 状态的映射
     */
    const ApiStatusMap: Record<number, { value: number, name: string, status: string }> = {
        [0]: {
            value: 0,
            name: '正常',
            status: 'success'
        },
        [1]: {
            value: 1,
            name: '错误',
            status: 'error'
        },
    };

    const record = userApiDigestVO;

    const [formatAndQuantityUsageVO, setFormatAndQuantityUsageVO]
        = useState<API.UserApiFormatAndQuantityUsageVO>();

    // 全局初始状态
    const {initialState} = useModel('@@initialState');

    // 登陆用户信息
    const {currentUser} = initialState || {};

    const loadData = async () => {
        message.loading("加载中");
        try {
            const result = await viewUserApiFormatAndQuantityUsage(
                {
                    accountId: currentUser?.accountId,
                    digestId: record?.digestId
                }
            )
            setFormatAndQuantityUsageVO(result.data);
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
                <ProDescriptions bordered>
                    <ProDescriptions.Item label="接口名称">{record?.apiName}</ProDescriptions.Item>
                    <ProDescriptions.Item label="接口描述">{record?.description}</ProDescriptions.Item>
                    <ProDescriptions.Item label="请求方法">{
                        <>
                            {record?.methodSet?.map((value) => (
                                <Space key={value}>
                                    <Tag color={HttpMethodMap[value || "GET"].color}>
                                        {HttpMethodMap[value || "GET"].value}
                                    </Tag>
                                </Space>
                            ))}
                        </>
                    }</ProDescriptions.Item>
                    <ProDescriptions.Item label="接口地址">
                        {record?.url}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口状态" span={3}>
                        {/*// @ts-ignore*/}
                        <Badge status={ApiStatusMap[record?.apiStatus || 0].status}
                               text={ApiStatusMap[record?.apiStatus || 0].name}/>
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="创建时间" span={2}>
                        {record?.createTime}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="更新时间" span={2}>
                        {record?.updateTime}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="请求参数" valueType={"jsonCode"}>
                        {formatAndQuantityUsageVO?.requestParam}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="请求头" valueType={"jsonCode"}>
                        {formatAndQuantityUsageVO?.requestHeader}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="请求体" valueType={"jsonCode"}>
                        {formatAndQuantityUsageVO?.requestBody}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="响应头" valueType={"jsonCode"}>
                        {formatAndQuantityUsageVO?.requestHeader}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="响应体" valueType={"jsonCode"}>
                        {formatAndQuantityUsageVO?.responseBody}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="用法类型">
                        <>
                            {record?.usageTypeSet?.map((type) => (
                                <Space key={type}>
                                    <Tag color={UsageTypeMap[type || "QUANTITY"].color}>
                                        {UsageTypeMap[type || "QUANTITY"].value}
                                    </Tag>
                                </Space>
                            ))}
                        </>
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="用法状态">
                        {formatAndQuantityUsageVO?.usageStatus}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="总调用次数">
                        {formatAndQuantityUsageVO?.total}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="失败调用次数">
                        {formatAndQuantityUsageVO?.failure}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="调用次数存量">
                        {formatAndQuantityUsageVO?.stock}
                    </ProDescriptions.Item>
                </ProDescriptions>
            </ProCard>
        </div>
    );
};

export default ApiFormatAndUsageCard;