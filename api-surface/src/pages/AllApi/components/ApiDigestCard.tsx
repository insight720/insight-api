import {ModalForm, ProCard, ProColumns, ProDescriptions, ProFormDigit, ProTable} from '@ant-design/pro-components';
import {message, Space, Tabs, Tag, Typography} from 'antd';
import React, {useState} from 'react';
import {viewApiDigestPage} from "@/services/api-facade/apiDigestController";
import {viewApiStockInfo} from "@/services/api-facade/apiQuantityUsageController";
import {SlidersFilled} from "@ant-design/icons";
import {FormattedMessage} from "@@/exports";
import {placeQuantityUsageOrder} from "@/services/api-security/userOrderController";

/**
 * 接口摘要卡片属性
 */
export type ApiDigestCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void
    apiDigestVO?: API.ApiDigestVO;
    setApiDigestVO?: (newValue: API.ApiDigestVO | undefined) => void;
    add?: (tabType: string) => void;
};

/**
 * 接口摘要卡片
 */
const ApiDigestCard: React.FC<ApiDigestCardProps> = (props: ApiDigestCardProps) => {

    // 当前登录用户
    const {currentUser} = props;

    // 当前查看的 API 摘要
    const {apiDigestVO} = props;

    // 设置当前查看的 API 摘要
    const {setApiDigestVO} = props;

    // 设置当前查看的 API 摘要
    const {add} = props;

    /**
     * 操作模态框的类型
     */
    enum OptionModalTypeEnum {
        // 查看用法
        VIEW_USAGE = 0,
        // 下订单
        PLACE_ORDER = 1,
        // 查看创建者
        VIEW_CREATOR = 2
    }

    // 操作模态框开关
    const [optionModalOpen, setOptionModalOpen]
        = useState<boolean>(false);

    // 操作模态框调用的函数
    const [optionModalOnFinishType, setOptionModalOnFinishType]
        = useState<OptionModalTypeEnum>();

    // 操作模态框 Tab activeKey
    const [optionModalTabActiveKey, setOptionModalTabActiveKey]
        = useState<string>("QUANTITY");

    // 操作模态框 Tab 选项
    const [optionModalTabItems, setOptionModalTabItems]
        = useState<{ key: string; label: string; }[]>();

    // 操作模态框提示消息
    const [optionModalTipMessage, setOptionModalTipMessage]
        = useState<React.ReactNode>();

    // 操作模态框标题
    const [optionModalTitle, setOptionModalTitle]
        = useState<string>();

    // 当前查看的接口库存信息 VO
    const [apiStockInfoVO, setApiStockInfoVO]
        = useState<API.ApiStockInfoVO>();

    // 接口用法类型标签页 Tab 选项
    const apiUsageTypeTabItems = [
        {
            key: "QUANTITY",
            label: "计数用法",
        },
    ];

    // 查看计数用法的操作模态框提示消息
    const quantityUsageTipMessage: React.ReactNode = (
        <Typography.Text>
            <Typography.Text strong>计数用法指的是记录接口调用次数的接口用法，
                也是平台目前仅支持的接口用法。</Typography.Text>
            在下单后，将从<Typography.Text strong>调用次数存量</Typography.Text>
            中扣除<Typography.Text strong>订单锁定的调用次数</Typography.Text>，此后接口调用将计数
            <Typography.Text strong>总调用次数</Typography.Text>和<Typography.Text strong>失败调用次数</Typography.Text>。
            通过总调用次数和失败调用次数，系统可以计算出<Typography.Text strong>成功调用次数</Typography.Text>。
            <br/>
            <Typography.Text>
                如果您想要查看当前接口计数用法的数据信息，请点击确定。
            </Typography.Text>
        </Typography.Text>
    );

    // 下计数用法订单的操作模态框提示消息
    const placeQuantityUsageOrderTipMessage: React.ReactNode = (
        <Typography.Text>
            <Typography.Text strong>
                计数用法是一种记录接口调用次数的方法，也是平台目前唯一支持的接口用法。
            </Typography.Text>
            在下单后，会从<Typography.Text strong> 调用次数存量 </Typography.Text>中扣除订单锁定的调用次数，
            然后开始对接口调用进行计数。接着，会从<Typography.Text strong> 锁定的调用次数存量 </Typography.Text>
            中扣除总调用次数。如果订单取消，一部分锁定的调用次数可能会被重新加入到<Typography.Text
            strong> 调用次数存量 </Typography.Text>中。
        </Typography.Text>
    );

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
            dataIndex: 'methodSet',
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
                <a
                    key="format"
                    type={"link"}
                    onClick={() => {
                        setApiDigestVO?.(record);
                        add?.("format");
                    }}
                >
                    查看
                </a>
                ,
                <a
                    key="quantityUsage"
                    type={"link"}
                    onClick={() => {
                        setApiDigestVO?.(record);
                        setOptionModalOnFinishType(OptionModalTypeEnum.VIEW_USAGE);
                        setOptionModalTabItems(apiUsageTypeTabItems);
                        setOptionModalTipMessage(quantityUsageTipMessage);
                        setOptionModalTitle("接口用法类型");
                        setOptionModalTabActiveKey("QUANTITY");
                        setOptionModalOpen(true);
                    }}
                >
                    用法
                </a>,
                <a
                    key="order"
                    onClick={() => {
                        if (!currentUser?.secretId) {
                            message.warning("您还没有创建 API 密钥，请先前往 用户页-账户设置 创建密钥")
                            return false;
                        }
                        setApiDigestVO?.(record);
                        setOptionModalOnFinishType(OptionModalTypeEnum.PLACE_ORDER);
                        // 如果有别的接口用法，需要动态改变 TabItems
                        setOptionModalTabItems(apiUsageTypeTabItems);
                        setOptionModalTipMessage(placeQuantityUsageOrderTipMessage);
                        setOptionModalTitle("下单");
                        setOptionModalTabActiveKey("QUANTITY");
                        setOptionModalOpen(true);
                    }}
                >
                    下单
                </a>,
                <a
                    key="creator"
                    onClick={() => {
                        setApiDigestVO?.(record);
                        add?.("creator");
                    }}
                >
                    创建者
                </a>,
            ],
        },
    ];

    /**
     * 修改用户名提交函数
     */
    const viewUsageOnFinish = async () => {
        // 检查用户是否绑定信息
        switch (optionModalTabActiveKey) {
            case "QUANTITY":
                add?.("quantityUsage");
        }
    };

    /**
     * 查看库存开启函数
     */
    const viewApiStockInfoOnOpen = async (open: boolean) => {
        if (!open) {
            setOptionModalOpen(false);
            return false;
        }
        message.loading("加载中");
        try {
            const result = await viewApiStockInfo(
                {
                    digestId: apiDigestVO?.digestId || "",
                }
            );
            setApiStockInfoVO(result.data);
            message.destroy();
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || "加载失败，请稍后再试");
            return false;
        }
    };

    /**
     * 西单提交函数
     */
    const placeOrderOnFinish = async (formData: any) => {
        message.loading("下单中");
        try {
            await placeQuantityUsageOrder(
                {
                    accountId: currentUser?.accountId,
                    digestId: apiDigestVO?.digestId,
                    methodSet: apiDigestVO?.methodSet,
                    usageTypeSet: apiDigestVO?.usageTypeSet,
                    orderQuantity: formData?.quantity,
                    apiName: apiDigestVO?.apiName || "",
                    description: apiDigestVO?.description || "",
                    url: apiDigestVO?.url || ""
                }
            );
            message.destroy();
            message.success('下单成功');
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || "下单失败，请稍后再试");
            return false;
        }
    };

    return (
        <div
            style={{background: '#F5F7FA'}}
        >
            <ModalForm<{
                name: string;
                company: string;
            }>
                // 操作模态框
                onFinish={async (formData) => {
                    // 根据点击按钮的类型选择对应的函数
                    switch (optionModalOnFinishType) {
                        case OptionModalTypeEnum.VIEW_USAGE:
                            return viewUsageOnFinish();
                        case OptionModalTypeEnum.PLACE_ORDER:
                            return placeOrderOnFinish(formData);
                    }
                }}
                width={500}
                open={optionModalOpen}
                onOpenChange={async (open: boolean) => {
                    // 根据点击按钮的类型选择对应的函数
                    switch (optionModalOnFinishType) {
                        case OptionModalTypeEnum.PLACE_ORDER:
                            return viewApiStockInfoOnOpen(open);
                        default:
                            setOptionModalOpen(open);
                    }
                }}
                title={optionModalTitle}
                // form={form}
                autoFocusFirstInput
                modalProps={{
                    destroyOnClose: true,
                }}
                submitTimeout={2000}
            >
                <Tabs
                    activeKey={optionModalTabActiveKey}
                    onChange={setOptionModalTabActiveKey}
                    centered
                    items={optionModalTabItems}/>
                {optionModalTipMessage}
                <br/>
                <br/>
                {optionModalOnFinishType === OptionModalTypeEnum.PLACE_ORDER &&
                    optionModalTabActiveKey === "QUANTITY" &&
                    (
                        <>
                            <ProDescriptions column={2}>
                                <ProDescriptions.Item span={2}>
                                    <Typography.Text italic>API 调用次数存量信息</Typography.Text>
                                </ProDescriptions.Item>
                                <ProDescriptions.Item label="调用次数存量">
                                    {apiStockInfoVO?.stock}
                                </ProDescriptions.Item>
                                <ProDescriptions.Item label="锁定的调用次数存量">
                                    {apiStockInfoVO?.lockedStock}
                                </ProDescriptions.Item>
                                <ProDescriptions.Item label="用法状态">
                                    {apiStockInfoVO?.usageStatus}
                                </ProDescriptions.Item>
                                <ProDescriptions.Item label="更新时间">
                                    {apiStockInfoVO?.updateTime}
                                </ProDescriptions.Item>
                            </ProDescriptions>
                            <br/>
                            <ProFormDigit
                                name="quantity"
                                label="订单锁定的调用次数"
                                fieldProps={{
                                    size: 'large',
                                    prefix: <SlidersFilled/>,
                                }}
                                rules={[
                                    {
                                        required: true,
                                        message: (
                                            <FormattedMessage
                                                id="Account name is required"
                                                defaultMessage="订单锁定的调用次数是必填项"
                                            />
                                        ),
                                    },
                                    {
                                        validator: async (rule, value) => {
                                            if (value <= 0) {
                                                throw new Error("订单锁定的调用次数必须大于 0");
                                            } else if (value > (apiStockInfoVO?.stock || 0)) {
                                                throw new Error("订单锁定的调用次数不能大于调用次数存量");
                                            }
                                        },
                                    },
                                ]}
                            />
                        </>
                    )}
            </ModalForm>

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
                            size: params.pageSize,
                            current: params.current,
                            ...params
                        } as API.ApiDigestPageQuery);
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