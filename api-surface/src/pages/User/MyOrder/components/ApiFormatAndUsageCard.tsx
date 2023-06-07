import {ProCard, ProDescriptions} from '@ant-design/pro-components';
import {Badge, Tag} from 'antd';
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
     *
     * HTTP 方法的映射
     */
    const HttpMethodMap: Record<number, { value: number, name: string, color: string }> = {
        [0]: {
            value: 0,
            name: 'GET',
            color: 'green'
        },
        [1]: {
            value: 1,
            name: 'HEAD',
            color: 'blue'
        },
        [2]: {
            value: 2,
            name: 'POST',
            color: 'magenta'
        },
        [3]: {
            value: 3,
            name: 'PUT',
            color: 'geekblue'
        },
        [4]: {
            value: 4,
            name: 'DELETE',
            color: 'red'
        },
        [5]: {
            value: 5,
            name: 'OPTIONS',
            color: 'cyan'
        },
        [6]: {
            value: 6,
            name: 'TRACE',
            color: 'purple'
        },
        [7]: {
            value: 7,
            name: 'PATCH',
            color: 'orange'
        }
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
        const result = await viewUserApiFormatAndQuantityUsage(
            {
                accountId: currentUser?.accountId,
                digestId: record?.digestId
            }
        )
        setFormatAndQuantityUsageVO(result.data);
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
                        <Tag color={HttpMethodMap[record?.method || 0].color}>
                            {HttpMethodMap[record?.method || 0].name}
                        </Tag>
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
                        {formatAndQuantityUsageVO?.requestBody}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="用法名">
                        按调用次数统计
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