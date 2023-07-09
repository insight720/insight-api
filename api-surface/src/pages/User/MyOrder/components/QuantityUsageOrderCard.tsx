import {ModalForm, ProCard, ProColumns, ProFormCaptcha, ProTable} from '@ant-design/pro-components';
import {message, Space, Tabs, Tag, Typography} from 'antd';
import React, {useState} from 'react';
import {
    cancelQuantityUsageOrder,
    confirmQuantityUsageOrder,
    viewQuantityUsageOrderPage
} from "@/services/api-security/quantityUsageOrderController";
import {LockOutlined} from "@ant-design/icons";
import {FormattedMessage, useIntl} from "@@/exports";
import {getVerificationCode} from "@/services/api-security/securityController";


/**
 * 接口计数用法订单卡片属性
 */
export type QuantityUsageOrderCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void
    add?: (tabType: string) => void;
    setOrderResultStatus?: (status: string) => void;
    setOrderResultType?: (type: string) => void;
};

/**
 * 接口计数用法订单卡片
 */
const QuantityUsageOrderCard: React.FC<QuantityUsageOrderCardProps> = (props: QuantityUsageOrderCardProps) => {

    // 当前登录用户
    const {currentUser} = props;

    // 设置当前查看的 API 摘要
    // const {setUserApiDigestVO} = props;

    // 设置订单操作结果状态
    const {setOrderResultStatus} = props;

    // 设置订单操作结果类型
    const {setOrderResultType} = props;

    // 设置当前查看的 API 摘要
    const {add} = props;

    // 接口计数用法订单 VO
    const [quantityUsageOrderVO, setQuantityUsageOrderVO]
        = useState<API.QuantityUsageOrderVO>();

    // 国际化
    const locale = useIntl();

    /**
     * 操作模态框的类型
     */
    enum OptionModalTypeEnum {
        // 确认
        CONFIRM = 0,
        // 取消
        CANCEL = 1,
    }

    // 操作模态框调用的函数
    const [optionModalOnFinishType, setOptionModalOnFinishType]
        = useState<OptionModalTypeEnum>();

    // 身份验证策略
    const [authStrategy, setAuthStrategy]
        = useState<string>("PHONE");

    // 操作模态框开关
    const [optionModalOpen, setOptionModalOpen]
        = useState<boolean>(false);

    // 操作模态框标题
    const [optionModalTitle, setOptionModalTitle]
        = useState<string>();

    // 操作模态框提示消息
    const [optionModalTipMessage, setOptionModalTipMessage]
        = useState<React.ReactNode>();

    // 身份验证类型 Tab 选项
    const verificationTabItems = [
        {
            key: "PHONE",
            label: "手机号验证"
        },
        {
            key: "EMAIL",
            label: "邮箱号验证"
        },
    ];

    // 确认订单的操作模态框提示消息
    const orderConfirmationTipMessage: React.ReactNode = (
        <Typography.Text>
            <Typography.Text strong>
                确认订单是指在核对订单信息正确后，使下单接口可用的操作，类似于下单后支付的操作
            </Typography.Text>。
            <Typography.Text>
                如果您想要确认当前的接口计数用法订单，请获取并输入验证码，然后点击确定。
            </Typography.Text>
        </Typography.Text>
    );

    // 取消订单的操作模态框提示消息
    const orderCancellationTipMessage: React.ReactNode = (
        <Typography.Text>
            <Typography.Text strong>
                取消订单是指在订单超时自动取消之前，用户自行取消订单。订单被用户主动取消后，订单占用的接口资源将自动释放
            </Typography.Text>。
            <Typography.Text>
                如果您想要取消当前的接口计数用法订单，请获取并输入验证码，再点击确定。
            </Typography.Text>
        </Typography.Text>
    );

    /**
     * 确定身份验证策略
     *
     * 如果用户没有绑定对应的信息，则进行错误提示，并返回 null。
     */
    function determineAuthStrategy() {
        const phoneNumber = currentUser?.phoneNumber;
        const emailAddress = currentUser?.emailAddress;
        const isUsingPhone = (authStrategy === "PHONE");
        if (isUsingPhone && !phoneNumber) {
            message.error("你没有绑定手机号")
            return null;
        }
        if (!isUsingPhone && !emailAddress) {
            message.error("你没有绑定邮箱地址")
            return null;
        }
        return isUsingPhone;
    }

    /**
     * 确认订单提交函数
     */
    const confirmOrderOnFinish = async (formData: any) => {
        const isUsingPhone = determineAuthStrategy();
        if (isUsingPhone === null) {
            return false;
        }
        message.loading("确认订单中");
        try {
            await confirmQuantityUsageOrder(
                {
                    codeCheckDTO: {
                        phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                        emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                        strategy: authStrategy || "",
                        verificationCode: formData.verificationCode
                    },
                    orderSn: quantityUsageOrderVO?.orderSn || "",
                    usageId: quantityUsageOrderVO?.usageId,
                    quantity: quantityUsageOrderVO?.quantity || "",
                }
            );
            message.destroy();
            // 打开成功的订单结果页面
            await add?.("result");
            setOrderResultType?.("CONFIRMATION")
            setOrderResultStatus?.("success");
            return true;
        } catch (error: any) {
            message.destroy();
            // 打开失败的订单结果页面
            await add?.("result");
            setOrderResultType?.("CONFIRMATION")
            setOrderResultStatus?.("error");
            return false;
        }
    };

    /**
     * 取消订单提交函数
     */
    const cancelOrderOnFinish = async (formData: any) => {
        const isUsingPhone = determineAuthStrategy();
        if (isUsingPhone === null) {
            return false;
        }
        message.loading("取消订单中");
        try {
            // 能下单说明用户已经绑定相关信息
            const isUsingPhone = (authStrategy === "PHONE");
            await cancelQuantityUsageOrder(
                {
                    codeCheckDTO: {
                        phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                        emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                        strategy: authStrategy || "",
                        verificationCode: formData.verificationCode
                    },
                    orderSn: quantityUsageOrderVO?.orderSn || "",
                    accountId: currentUser?.accountId,
                    digestId: quantityUsageOrderVO?.digestId
                }
            );
            message.destroy();
            // 打开成功的订单结果页面
            await add?.("result");
            setOrderResultType?.("CANCELLATION")
            setOrderResultStatus?.("success");
            return true;
        } catch (error: any) {
            message.destroy();
            // 打开失败的订单结果页面
            await add?.("result");
            setOrderResultType?.("CANCELLATION")
            setOrderResultStatus?.("error");
            return false;
        }
    };

    /**
     * 接口计数用法订单状态枚举
     */
    const QuantityUsageOrderStatusMap: Record<number, { description: string, color: string }> = {
        0: {
            description: '订单新建',
            color: 'cyan'
        },
        1: {
            description: '下单成功',
            color: 'blue'
        },
        2: {
            description: '存量不足',
            color: 'red'
        },
        3: {
            description: '超时取消',
            color: 'orange'
        },
        4: {
            description: '用户取消',
            color: 'purple'
        },
        5: {
            description: '订单确认',
            color: 'green'
        },
    };

    /**
     * ProTable 的列
     */
    const columns: ProColumns<API.QuantityUsageOrderVO>[] = [
        {
            dataIndex: 'index',
            valueType: 'indexBorder',
            width: 48,
        },
        {
            title: '订单编号',
            dataIndex: 'orderSn',
            valueType: "text",
            copyable: true
        },
        {
            title: '订单描述',
            dataIndex: 'description',
            valueType: "text",
            copyable: true
        },
        {
            title: '锁定的调用次数',
            dataIndex: 'quantity',
            valueType: "digit",
        },
        {
            title: '订单状态',
            dataIndex: 'orderStatus',
            valueType: 'treeSelect',
            fieldProps: {
                multiple: true
            },
            // 对应后端 QuantityUsageOrderStatusEnum#storedValue()
            valueEnum: {
                0: {text: '订单新建', color: 'cyan'},
                1: {text: '下单成功', color: 'blue'},
                2: {text: '存量不足', color: 'red'},
                3: {text: '超时取消', color: 'orange'},
                4: {text: '用户取消', color: 'purple'},
                5: {text: '订单确认', color: 'green'},
            },
            render: (text, record) => {
                return (
                    <>
                        <Space>
                            <Tag color={QuantityUsageOrderStatusMap[record.orderStatus || 0].color}>
                                {QuantityUsageOrderStatusMap[record.orderStatus || 0].description}
                            </Tag>
                        </Space>
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
                    key="CONFIRMATION"
                    onClick={() => {
                        setQuantityUsageOrderVO(record);
                        setOptionModalOnFinishType(OptionModalTypeEnum.CONFIRM);
                        setOptionModalTipMessage(orderConfirmationTipMessage);
                        setOptionModalTitle("确认订单");
                        setOptionModalOpen(true);
                    }}
                >
                    确认
                </a>
                ,
                <a
                    key={"CANCELLATION"}
                    type={"link"}
                    onClick={() => {
                        setQuantityUsageOrderVO(record);
                        setOptionModalOnFinishType(OptionModalTypeEnum.CANCEL);
                        setOptionModalTipMessage(orderCancellationTipMessage);
                        setOptionModalTitle("取消订单");
                        setOptionModalOpen(true);
                    }}
                >
                    取消
                </a>,
            ],
        },
    ];

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
                        case OptionModalTypeEnum.CONFIRM:
                            return confirmOrderOnFinish(formData);
                        case OptionModalTypeEnum.CANCEL:
                            return cancelOrderOnFinish(formData);
                    }
                }}
                width={500}
                open={optionModalOpen}
                onOpenChange={async (open: boolean) => {
                    // 根据点击按钮的类型选择对应的函数
                    switch (optionModalOnFinishType) {
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
                    activeKey={authStrategy}
                    onChange={setAuthStrategy}
                    centered
                    items={verificationTabItems}/>
                {optionModalTipMessage}
                <br/>
                <br/>
                <ProFormCaptcha
                    placeholder={locale.formatMessage({
                        id: 'Please enter',
                        defaultMessage: '请输入验证码',
                    })}
                    fieldProps={{
                        size: 'large',
                        prefix: <LockOutlined/>,
                    }}
                    captchaProps={{
                        size: 'large',
                    }}
                    captchaTextRender={(timing, count) => {
                        if (timing) {
                            return `${count} ${locale.formatMessage({
                                id: 'pages.getCaptchaSecondText',
                                defaultMessage: '获取验证码',
                            })}`;
                        }
                        return locale.formatMessage({
                            id: 'pages.login.phoneLogin.getVerificationCode',
                            defaultMessage: '获取验证码',
                        });
                    }}
                    name="verificationCode"
                    rules={[
                        {
                            required: true,
                            message: (
                                <FormattedMessage
                                    id="Please enter the verification code"
                                    defaultMessage="验证码是必填项"
                                />
                            ),
                        },
                        {
                            pattern: /^\d{6}$/,
                            message: (
                                <FormattedMessage
                                    id="is not a valid verification code"
                                    defaultMessage="不是有效的验证码"
                                />
                            )
                        }
                    ]}
                    onGetCaptcha={async () => {
                        // 能下单说明用户已经绑定相关信息
                        const isUsingPhone = (authStrategy === "PHONE");
                        try {
                            await getVerificationCode({
                                phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                                emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                                strategy: authStrategy || ""
                            });
                            message.success("获取验证码成功");
                        } catch (error: any) {
                            message.error(error.message || "获取验证码失败");
                            // 让等待状态结束
                            throw error;
                        }
                    }}
                />
            </ModalForm>

            <ProCard>
                <ProTable<API.QuantityUsageOrderVO>
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
                        const result = await viewQuantityUsageOrderPage({
                            size: params.pageSize,
                            current: params.current,
                            orderSn: params.orderSn,
                            accountId: currentUser?.accountId,
                            quantity: params.quantity,
                            description: params.description,
                            orderStatusSet: params.orderStatus,
                            createTimeRange: params.createTimeRange,
                            updateTimeRange: params.updateTimeRange,
                        });
                        return {
                            data: result?.data?.quantityUsageOrderVOList,
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
                    headerTitle="订单列表（计数用法）"
                    // 这里可以添加按钮
                    toolBarRender={() => []}
                />
            </ProCard>
        </div>

    );
};

export default QuantityUsageOrderCard;