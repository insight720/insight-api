import {ProCard} from '@ant-design/pro-components';
import {Button, Result, Typography} from 'antd';
import React from 'react';

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
     * 订单确认成功结果子标题
     */
    const successfulOrderConfirmationResultSubtitle: React.ReactNode =
        <Typography.Text>
            订单确认成功，您可点击返回键查看。
        </Typography.Text>;

    /**
     * 订单确认失败结果子标题
     */
    const failedOrderConfirmationResultSubtitle: React.ReactNode =
        <Typography.Text>
            订单确认失败，请点击返回键查看，并稍后尝试重新确认。
        </Typography.Text>;

    /**
     * 取消订单成功结果子标题
     */
    const successfulOrderCancellationResultSubtitle: React.ReactNode =
        <Typography.Text>
            订单取消成功，您可点击返回键查看。
        </Typography.Text>;

    /**
     * 取消订单失败结果子标题
     */
    const failedOrderCancellationResultSubtitle: React.ReactNode =
        <Typography.Text>
            订单取消失败，请点击返回键查看，并稍后尝试重新取消。
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
                            successfulOrderConfirmationResultSubtitle : failedOrderConfirmationResultSubtitle}
                        extra={[
                            <Button type="primary" key="back" onClick={handleBack}>
                                返回
                            </Button>,
                        ]}
                    />
                }
                {orderResultType === "CANCELLATION" &&
                    <Result
                        // @ts-ignore
                        status={orderResultStatus || "info"}
                        title={orderResultStatus === "success" ? "取消成功" : "取消失败"}
                        subTitle={orderResultStatus === "success" ?
                            successfulOrderCancellationResultSubtitle : failedOrderCancellationResultSubtitle}
                        extra={[
                            <Button type="primary" key="back" onClick={handleBack}>
                                返回
                            </Button>,
                        ]}
                    />
                }
            </ProCard>
        </div>

    );
};

export default ApiOrderResultCard;