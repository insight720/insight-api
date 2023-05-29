import {PlusOutlined} from '@ant-design/icons';
import {ActionType, ProCard, ProColumns, ProTable, TableDropdown} from '@ant-design/pro-components';
import {Button, Space, Tag} from 'antd';
import React, {useRef} from 'react';
import {viewApiAdminPage} from "@/services/api-security/securityController";

/**
 * 接口管理卡片属性
 */
export type ApiAdminCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void
    setApiAdminVO?: (newValue: API.UserApiDigestVO | undefined) => void;
    add?: (tabType: string) => void;
};

/**
 * 接口管理卡片
 */
const ApiAdminCard: React.FC<ApiAdminCardProps> = (props: ApiAdminCardProps) => {

    const actionRef = useRef<ActionType>();

    // 当前登录用户
    // const {currentUser} = props;

    // 设置当前查看的 API 摘要
    const {setApiAdminVO} = props;

    // 设置当前查看的 API 摘要
    const {add} = props;

    /**
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
     * ProTable 的列
     */
    const columns: ProColumns<API.ApiAdminVO>[] = [
        {
            dataIndex: 'index',
            valueType: 'indexBorder',
            width: 48,
        },
        {
            title: '接口名称',
            dataIndex: 'apiName',
            valueType: "text",
            copyable: true
        },
        {
            title: '接口描述',
            dataIndex: 'description',
            valueType: "textarea"
        },
        {
            title: '请求方法',
            dataIndex: 'method',
            valueType: 'treeSelect',
            fieldProps: {
                multiple: true
            },
            valueEnum: {
                0: {
                    text: 'GET',
                },
                1: {
                    text: 'HEAD',
                },
                2: {
                    text: 'POST',
                },
                3: {
                    text: 'PUT',
                },
                4: {
                    text: 'DELETE',
                },
                5: {
                    text: 'OPTIONS',
                },
                6: {
                    text: 'TRACE',
                },
                7: {
                    text: 'PATCH',
                }
            },
            renderFormItem: (_, {defaultRender}) => {
                return defaultRender(_);
            },
            render: (_, record) => (
                <Space>
                    <Tag color={HttpMethodMap[record?.method || 0].color}>
                        {HttpMethodMap[record?.method || 0].name}
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
            title: '接口用法类型',
            dataIndex: 'usageType',
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
            title: '接口摘要更新时间',
            dataIndex: 'digestUpdateTime',
            valueType: 'dateTime',
            hideInSearch: true
            // 如果需要自定义时间格式，可以使用 formatString 参数
            // 例如：formatString: 'YYYY-MM-DD HH:mm:ss'
        },
        {
            title: '接口摘要更新时间范围',
            key: 'digestUpdateTimeRange',
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
            title: '请求参数',
            dataIndex: 'requestParam',
            valueType: 'jsonCode',
        },
        {
            title: '请求头',
            dataIndex: 'requestHeader',
            valueType: 'code',
        },
        {
            title: '请求体',
            dataIndex: 'requestBody',
            valueType: 'jsonCode',
        },
        {
            title: '响应头',
            dataIndex: 'responseHeader',
            valueType: 'jsonCode',
        },
        {
            title: '响应体',
            dataIndex: 'responseHeader',
            valueType: 'jsonCode',
        },
        {
            title: '逻辑删除',
            dataIndex: 'isDeleted',
            valueType: 'treeSelect',
            fieldProps: {
                multiple: true
            },
            valueEnum: {
                0: {text: '正常', status: "success"},
                1: {text: '已删除', status: "error"},
            },
        },
        {
            title: '接口格式更新时间',
            dataIndex: 'formatUpdateTime',
            valueType: 'dateTime',
            hideInSearch: true
            // 如果需要自定义时间格式，可以使用 formatString 参数
            // 例如：formatString: 'YYYY-MM-DD HH:mm:ss'
        },
        {
            title: '接口格式更新时间范围',
            key: 'formatUpdateTimeRange',
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
            title: '操作',
            valueType: 'option',
            key: 'option',
            render: (text, record, _, action) => [
                <Button
                    key="view"
                    type={"link"}
                    onClick={() => {
                        setApiAdminVO?.(record);
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
                    编辑
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
                    scroll={{x: 3500}}
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
                        const result = await viewApiAdminPage({
                            ...params,
                            current: params.current,
                            size: params.pageSize,
                        });
                        return {
                            data: result?.data?.apiAdminVOList,
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
                    headerTitle="管理的所有接口"
                    toolBarRender={() => [
                        <Button
                            key="button"
                            icon={<PlusOutlined/>}
                            onClick={() => {
                                actionRef.current?.reload();
                            }}
                            type="primary"
                        >
                            新建
                        </Button>,
                    ]}
                />
            </ProCard>
        </div>

    );
};

export default ApiAdminCard;