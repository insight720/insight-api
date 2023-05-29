import {ProCard, ProColumns, ProTable, TableDropdown} from '@ant-design/pro-components';
import {Button, Space, Tag} from 'antd';
import React from 'react';
import {viewApiDigestPage} from "@/services/api-facade/facadeController";

/**
 * 接口摘要卡片属性
 */
export type ApiDigestCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void
    setApiDigestVO?: (newValue: API.ApiDigestVO | undefined) => void;
    add?: (tabType: string) => void;
};

/**
 * 接口摘要卡片
 */
const ApiDigestCard: React.FC<ApiDigestCardProps> = (props: ApiDigestCardProps) => {

    // 当前登录用户
    // const {currentUser} = props;

    // 设置当前查看的 API 摘要
    const {setApiDigestVO} = props;

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
    const columns: ProColumns<API.ApiDigestVO>[] = [
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
            dataIndex: 'method',
            valueType: 'treeSelect',
            fieldProps: {
                multiple: true
            },
            valueEnum: {
                'GET': {
                    text: HttpMethodMap['GET'].value,
                },
                'HEAD': {
                    text: HttpMethodMap['HEAD'].value,
                },
                'POST': {
                    text: HttpMethodMap['POST'].value,
                },
                'PUT': {
                    text: HttpMethodMap['PUT'].value,
                },
                'DELETE': {
                    text: HttpMethodMap['DELETE'].value,
                },
                'OPTIONS': {
                    text: HttpMethodMap['OPTIONS'].value,
                },
                'TRACE': {
                    text: HttpMethodMap['TRACE'].value,
                },
                'PATCH': {
                    text: HttpMethodMap['PATCH'].value,
                }
            },
            renderFormItem: (_, {defaultRender}) => {
                return defaultRender(_);
            },
            render: (_, record) => (
                <Space>
                    <Tag color={HttpMethodMap[record?.method || "GET"].color}>
                        {HttpMethodMap[record?.method || "GET"].value}
                    </Tag>
                </Space>
            ),
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
            valueType: 'treeSelect',
            fieldProps: {
                multiple: true
            },
            valueEnum: {
                0: {text: '正常', status: "success"},
                1: {text: '错误', status: "error"},
            },
        },
        {
            title: '用法类型',
            dataIndex: 'usageTypeSet',
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
            render: (text, record, _, action) => [
                <Button
                    key="view"
                    type={"link"}
                    onClick={() => {
                        setApiDigestVO?.(record);
                        add?.("format");
                    }}
                >
                    查看
                </Button>
                ,
                <a
                    key="editable"
                    onClick={() => {
                        // action?.startEditable?.(record.id);
                        action?.startEditable?.(record.digestId || "");
                    }}
                >
                    用法
                </a>,
                <TableDropdown
                    key="actionGroup"
                    onSelect={() => action?.reload()}
                    menus={[
                        {key: 'copy', name: '复制'},
                        {key: 'delete', name: '删除'},
                    ]}
                />,
            ],
        },
    ];

    return (
        <div
            style={{background: '#F5F7FA'}}
        >
            <ProCard>
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
                        const result = await viewApiDigestPage({
                            size: params.pageSize || 5,
                            current: params.current || 1,
                            description: params.description,
                            methodSet: params.method,
                            url: params.url,
                            usageTypeSet: params.usageTypeSet,
                            apiStatusSet: params.apiStatus,
                            createTimeRange: params.createTimeRange,
                            updateTimeRange: params.updateTimeRange
                        });
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
                    headerTitle="接口摘要"
                />
            </ProCard>
        </div>

    );
};

export default ApiDigestCard;