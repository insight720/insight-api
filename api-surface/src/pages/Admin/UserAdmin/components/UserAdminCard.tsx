import {PlusOutlined} from '@ant-design/icons';
import {ActionType, ProCard, ProColumns, ProTable, TableDropdown} from '@ant-design/pro-components';
import {Button} from 'antd';
import React, {useRef} from 'react';
import {viewUserAdminPage} from "@/services/api-security/securityController";

/**
 * 用户管理卡片属性
 */
export type UserAdminCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void
    setUserAdminVO?: (newValue: API.UserAdminVO | undefined) => void;
    add?: (tabType: string) => void;
};

/**
 * 用户管理卡片
 */
const UserAdminCard: React.FC<UserAdminCardProps> = (props: UserAdminCardProps) => {

    const actionRef = useRef<ActionType>();

    // 当前登录用户
    // const {currentUser} = props;

    // 设置当前查看的 API 摘要
    const {setUserAdminVO} = props;

    // 设置当前查看的 API 摘要
    // const {add} = props;

    /**
     * ProTable 的列
     */
    const columns: ProColumns<API.UserAdminVO>[] = [
        {
            dataIndex: 'index',
            valueType: 'indexBorder',
            width: 48,
        },
        {
            title: '头像',
            dataIndex: 'avatar',
            valueType: "avatar",
            copyable: true
        },
        {
            title: '昵称',
            dataIndex: 'nickname',
            valueType: "text",
            copyable: true
        },
        {
            title: '个人网站',
            dataIndex: 'description',
            valueType: 'text'
        },
        {
            title: 'GitHub',
            dataIndex: 'github',
            valueType: 'text'
        },
        {
            title: 'Gitee',
            dataIndex: 'gitee',
            valueType: 'text'
        },
        {
            title: '个人简介',
            dataIndex: 'biography',
            valueType: 'text'
        },
        {
            title: 'IP 地址',
            dataIndex: 'ipAddress',
            valueType: 'text'
        },
        {
            title: 'IP 属地',
            dataIndex: 'ipLocation',
            valueType: 'text'
        },
        {
            title: '上次登录时间',
            dataIndex: 'lastLoginTime',
            valueType: 'dateTime',
            hideInSearch: true
            // 如果需要自定义时间格式，可以使用 formatString 参数
            // 例如：formatString: 'YYYY-MM-DD HH:mm:ss'
        },
        {
            title: '上次登录时间范围',
            key: 'lastLoginTimeRange',
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
            title: '逻辑删除',
            dataIndex: 'isDeleted',
            valueType: 'treeSelect',
            fieldProps: {
                multiple: true
            },
            valueEnum: {
                0: {text: '未删除', status: "success"},
                1: {text: '删除', status: "error"},
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
            title: '资料更新时间',
            dataIndex: 'profileUpdateTime',
            valueType: 'dateTime',
            hideInSearch: true
            // 如果需要自定义时间格式，可以使用 formatString 参数
            // 例如：formatString: 'YYYY-MM-DD HH:mm:ss'
        },
        {
            title: '资料更新时间范围',
            key: 'profileUpdateTimeRange',
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
            title: '账户名',
            dataIndex: 'username',
            valueType: 'text',
            hideInSearch: true
        },
        {
            title: '邮箱',
            dataIndex: 'emailAddress',
            valueType: 'text',
            hideInSearch: true
        },
        {
            title: '手机号',
            dataIndex: 'phoneNumber',
            valueType: 'text',
        },
        {
            title: '权限',
            dataIndex: 'authority',
            valueType: 'text',
        },
        {
            title: '密钥 ID',
            dataIndex: 'secretId',
            valueType: 'text',
        },
        {
            title: '密钥值',
            dataIndex: 'secretKey',
            valueType: 'text',
        },
        {
            title: '账号状态',
            dataIndex: 'accountStatus',
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
            title: '账户更新时间',
            dataIndex: 'accountUpdateTime',
            valueType: 'dateTime',
            hideInSearch: true
            // 如果需要自定义时间格式，可以使用 formatString 参数
            // 例如：formatString: 'YYYY-MM-DD HH:mm:ss'
        },
        {
            title: '账户更新时间范围',
            key: 'accountUpdateTime',
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
                        setUserAdminVO?.(record);
                        // add?.("format");
                    }}
                >
                    查看
                </Button>
                ,
                <a
                    key="editable"
                    onClick={() => {
                        // action?.startEditable?.(record.id);
                        // action?.startEditable?.(record.digestId || "");
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
                <ProTable<API.UserAdminVO>
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
                        const result = await viewUserAdminPage({
                            authoritySet: params.authority,
                            accountStatusSet: params.accountStatus,
                            size: params.pageSize,
                            ...params
                        });
                        return {
                            data: result?.data?.userAdminVOList,
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
                    headerTitle="所有用户"
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

export default UserAdminCard;