import {ProCard} from '@ant-design/pro-components';
import {Button, Result, Typography} from 'antd';
import React from 'react';
import {Link} from "umi";

/**
 * 接口订单结果卡片属性
 */
export type ApiDigestCardProps = {
    orderResultStatus?: string,
    orderResultType?: string,
    remove?: (tabType: string) => void;
};

/**
 * 接口订单结果卡片
 */
const ApiOrderResultCard: React.FC<ApiDigestCardProps> = (props: ApiDigestCardProps) => {

    // 删除标签页
    const {remove} = props;

    // 订单结果状态
    const {orderResultStatus} = props;

    // 订单结果类型
    const {orderResultType} = props;

    // 处理返回按钮点击事件
    const handleBack = async () => {
        await remove?.("result");
    };

    /**
     * 下单成功结果子标题
     */
    const successfulOrderResultSubtitle: React.ReactNode =
        <Typography.Text>
            请尽快前往
            <Typography.Text strong> 我的订单 </Typography.Text>
            进行订单确认，
            <Typography.Text strong>
                若 30 分钟未确认，订单将自动取消
            </Typography.Text>。
        </Typography.Text>;

    /**
     * 下单失败结果子标题
     */
    const failedOrderResultSubtitle: React.ReactNode =
        <Typography.Text>
            请稍后尝试重新下单或联系管理员。
        </Typography.Text>;

    return (
        <div
            style={{background: '#F5F7FA'}}
        >
            <ProCard>
                {orderResultType === "CONFIRMATION" &&
                    <Result
                        // @ts-ignore
                        status={orderResultStatus || "info"}
                        title={orderResultStatus === "success" ? "确认成功" : "确认失败"}
                        subTitle={orderResultStatus === "success" ?
                            successfulOrderResultSubtitle : failedOrderResultSubtitle}
                        extra={[
                            <Button type="primary" key="back" onClick={handleBack}>
                                返回
                            </Button>,
                            orderResultStatus === "success" && (
                                <Link key="view" to="/user/order">
                                    <Button key="view">我的订单</Button>
                                </Link>
                            ),
                        ]}
                    />
                }
                {orderResultType === "CANCELLATION" &&
                    <Result
                        // @ts-ignore
                        status={orderResultStatus || "info"}
                        title={orderResultStatus === "success" ? "取消成功" : "取消失败"}
                        subTitle={orderResultStatus === "success" ?
                            successfulOrderResultSubtitle : failedOrderResultSubtitle}
                        extra={[
                            <Button type="primary" key="back" onClick={handleBack}>
                                返回
                            </Button>,
                            orderResultStatus === "success" && (
                                <Link key="view" to="/user/order">
                                    <Button key="view">我的订单</Button>
                                </Link>
                            ),
                        ]}
                    />
                }
            </ProCard>
        </div>

    );
};

export default ApiOrderResultCard;