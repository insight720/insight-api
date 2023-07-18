import {ProCard, ProColumns, ProTable} from '@ant-design/pro-components';
import {Button, Space, Tag} from 'antd';
import React from 'react';
import {viewUserApiDigestPage} from "@/services/api-security/securityController";

/**
 * 接口摘要卡片属性
 */
export type ApiDigestCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void
    setUserApiDigestVO?: (newValue: API.UserApiDigestVO | undefined) => void;
    add?: (tabType: string) => void;
};

/**
 * 接口摘要卡片
 */
const ApiDigestCard: React.FC<ApiDigestCardProps> = (props: ApiDigestCardProps) => {


    // 设置当前查看的 API 摘要
    const {setUserApiDigestVO} = props;

    // 设置当前查看的 API 摘要
    const {add} = props;

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
     * ProTable 的列
     */
    /**
     * ProTable 的列
     */
    const columns: ProColumns<API.UserApiDigestVO>[] = [
        {
            dataIndex: 'index',
            valueType: 'indexBorder',
            width: 48,
        },
        {
            title: '接口名称',
            dataIndex: 'apiName',
            valueType: "textarea",
            copyable: true,
        },
        {
            title: '接口描述',
            dataIndex: 'description',
            valueType: "textarea",
        },
        {
            title: '请求方法',
            dataIndex: 'methodSet',
            valueType: 'treeSelect',
            fieldProps: {
                multiple: true
            },
            valueEnum: {
                'GET': {
                    text: 'GET',
                },
                'HEAD': {
                    text: 'HEAD',
                },
                'POST': {
                    text: 'POST',
                },
                'PUT': {
                    text: 'PUT',
                },
                'DELETE': {
                    text: 'DELETE',
                },
                'OPTIONS': {
                    text: 'OPTIONS',
                },
                'TRACE': {
                    text: 'TRACE',
                },
                'PATCH': {
                    text: 'PATCH',
                }
            },
            renderFormItem: (_, {defaultRender}) => {
                return defaultRender(_);
            },
            render: (text, record) => {
                return (
                    <>
                        {record.methodSet?.map((value) => (
                            <Space key={value}>
                                <Tag color={HttpMethodMap[value || "GET"].color}>
                                    {HttpMethodMap[value || "GET"].value}
                                </Tag>
                            </Space>
                        ))}
                    </>
                );
            },
        },
        {
            title: 'URL',
            dataIndex: 'url',
            valueType: 'textarea',
            copyable: true
        },
        {
            title: '接口状态',
            dataIndex: 'apiStatus',
            hideInSearch: true,
            valueEnum: {
                0: {text: '正常', status: "success"},
                1: {text: '错误', status: "error"},
            },
        },
        {
            title: '接口状态',
            dataIndex: 'apiStatusSet',
            valueType: 'treeSelect',
            fieldProps: {
                multiple: true
            },
            hideInTable: true,
            valueEnum: {
                0: {text: '正常', status: "success"},
                1: {text: '错误', status: "error"},
            },
        },
        {
            title: '用法类型',
            dataIndex: 'usageTypeSet',
            valueType: 'treeSelect',
            fieldProps: {
                multiple: true
            },
            valueEnum: {
                "QUANTITY": {text: '计数用法', status: "success"},
            },
            renderFormItem: (_, {defaultRender}) => {
                return defaultRender(_);
            },
            render: (text, record) => {
                return (
                    <>
                        {record.usageTypeSet?.map((type) => (
                            <Space key={type}>
                                <Tag color={UsageTypeMap[type || "QUANTITY"].color}>
                                    {UsageTypeMap[type || "QUANTITY"].value}
                                </Tag>
                            </Space>
                        ))}
                    </>
                );
            },
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            valueType: 'dateTime',
            hideInSearch: true
            // 如果需要自定义时间格式，可以使用 formatString 参数
            // 例如：formatString: 'YYYY-MM-DD HH:mm:ss'
        },
        {
            title: '创建时间范围',
            key: 'createTimeRange',
            hideInTable: true,
            valueType: 'dateTimeRange',
            fieldProps: {
                // placeholder: []
            },
            renderFormItem: (_, {type, defaultRender}) => {
                if (type === 'form') {
                    return null;
                }
                return defaultRender(_);
            },
        },
        {
            title: '更新时间',
            dataIndex: 'updateTime',
            valueType: 'dateTime',
            hideInSearch: true
            // 如果需要自定义时间格式，可以使用 formatString 参数
            // 例如：formatString: 'YYYY-MM-DD HH:mm:ss'
        },
        {
            title: '更新时间范围',
            key: 'updateTimeRange',
            hideInTable: true,
            valueType: 'dateTimeRange',
            fieldProps: {
                // placeholder: []
            },
            renderFormItem: (_, {type, defaultRender}) => {
                if (type === 'form') {
                    return null;
                }
                return defaultRender(_);
            },
        },
        {
            title: '操作',
            valueType: 'option',
            key: 'option',
            render: (text, record) => [
                <Button
                    key="view"
                    type={"link"}
                    onClick={() => {
                        setUserApiDigestVO?.(record);
                        add?.("format");
                    }}
                >
                    查看
                </Button>
            ],
        },
    ];

    // 当前登录的用户
    const {currentUser} = props

    return (
        <div
            style={{background: '#F5F7FA'}}
        >
            <ProCard>
                {

                }
                <ProTable<API.UserApiDigestVO>
                    columns={columns}
                    cardBordered
                    request={async (
                        // 第一个参数 params 查询表单和 params 参数的结合
                        // 第一个参数中一定会有 pageSize 和  current ，这两个参数是 antd 的规范
                        params,
                        sort,
                        filter,
                    ) => {
                        console.log("params: ", params);
                        console.log("sort: ", sort);
                        console.log("filter: ", filter);
                        // 这里需要返回一个 Promise,在返回之前你可以进行数据转化
                        // 如果需要转化参数可以在这里进行修改
                        const result = await viewUserApiDigestPage({
                            accountId: currentUser?.accountId,
                            size: params.pageSize,
                            current: params.current,
                            ...params
                        } as API.UserApiDigestPageQuery);
                        return {
                            data: result?.data?.digestVOList,
                            // success 请返回 true，
                            // 不然 table 会停止解析数据，即使有数据
                            success: true,
                            // 不传会使用 data 的长度，如果是分页一定要传
                            total: result?.data?.total,
                        };
                    }}
                    editable={{
                        type: 'multiple',
                    }}
                    columnsState={{
                        persistenceKey: 'pro-table-singe-demos',
                        persistenceType: 'localStorage',
                        onChange(value) {
                            console.log('value: ', value);
                        },
                    }}
                    rowKey="id"
                    search={{
                        span: 12,
                        labelWidth: 'auto',
                    }}
                    options={{
                        setting: {
                            listsHeight: 400,
                        },
                    }}
                    pagination={{
                        pageSize: 5,
                        onChange: (page) => console.log(page),
                    }}
                    dateFormatter="string"
                    headerTitle="摘要列表"
                />
            </ProCard>
        </div>

    );
};

export default ApiDigestCard;