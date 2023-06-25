import {ProCard, ProDescriptions} from '@ant-design/pro-components';
import {message, Space, Tag} from 'antd';
import React, {useEffect, useState} from 'react';
import {viewApiFormat} from "@/services/api-facade/apiFormatController";

/**
 * API 视图卡属性
 */
export type ApiViewCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void;
    apiDigestVO?: API.ApiDigestVO;
};

/**
 * API 视图卡
 */
const ApiViewCard: React.FC<ApiViewCardProps> = (props: ApiViewCardProps) => {

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
     * 使用方法映射
     */
    const UsageTypeMap: Record<string, { value: string, color: string }> = {
        ['QUANTITY']: {
            value: '计数用法',
            color: 'green'
        },
    };

    const [apiFormatVO, setApiFormatVO]
        = useState<API.ApiFormatVO>();

    // 全局初始状态
    // const {initialState} = useModel('@@initialState');

    // 登陆用户信息
    // const {currentUser} = initialState || {};

    const loadData = async () => {
        message.loading("加载中");
        try {
            const result = await viewApiFormat(
                {
                    digestId: apiDigestVO?.digestId || ""
                }
            )
            setApiFormatVO(result.data);
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
                <ProDescriptions bordered title={"接口信息"}
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
                    <ProDescriptions.Item label="用法类型">
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
                    <ProDescriptions.Item label="接口格式创建时间">
                        {apiFormatVO?.createTime}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口格式更新时间">
                        {apiFormatVO?.updateTime}
                    </ProDescriptions.Item>

                    <ProDescriptions.Item label="请求参数" valueType={"jsonCode"}>
                        {apiFormatVO?.requestParam}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="请求头" valueType={"jsonCode"}>
                        {apiFormatVO?.requestHeader}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="请求体" valueType={"jsonCode"}>
                        {apiFormatVO?.requestBody}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="响应头" valueType={"jsonCode"}>
                        {apiFormatVO?.requestHeader}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="响应体" valueType={"jsonCode"}>
                        {apiFormatVO?.requestBody}
                    </ProDescriptions.Item>
                </ProDescriptions>
            </ProCard>
        </div>
    );
};

export default ApiViewCard;