import {ModalForm, ProFormCaptcha,} from '@ant-design/pro-components';
import React, {useState} from "react";
import {DrawerProps, Form, message, Tabs} from "antd";
import {FormattedMessage, useIntl} from "@@/exports";
import {LockOutlined} from "@ant-design/icons";

/**
 * 验证码模态框属性
 */
export type CaptchaModalProps = {
    // 开启标志
    open?: DrawerProps['open'];

    // 开启函数
    onOpenChange?: (visible: boolean) => void;

    // 设置密钥
    setSecretKey?: (secretKey: string) => void;

    // 提示消息
    tipMessage?: React.ReactNode;
}

/**
 * 验证码模态框
 */
const CaptchaModal: React.FC<CaptchaModalProps> = (props: CaptchaModalProps) => {
    // 国际化
    const locale = useIntl();

    // 表单数据
    const [form] = Form.useForm<{ name: string; company: string }>();

    // 身份验证类型
    const [authType, setAuthType] = useState<string>('PHONE');

    // 提示消息
    const {tipMessage} = props;

    // 设置密钥
    const {setSecretKey} = props;

    // 是否应该显示密钥
    const [shouldShowKey, setShouldKey] = useState<boolean>(false);

    /**
     * 验证码表单提交
     */
    const onFinish = async (values: any) => {
        message.loading("验证中");
        try {
            const isUsingPhone = (authType === "PHONE");
            values.verificaionCode
            if (shouldShowKey) {
                setSecretKey?.("");
            }
            message.destroy();
            message.success('验证成功');
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || '验证失败，请重试');
            return false;
        }
    };

    return (
        <ModalForm<{
            name: string;
            company: string;
        }>
            width={500}
            open={props.open}
            onOpenChange={props.onOpenChange}
            title={locale.formatMessage({
                id: 'Sign up for an account',
                defaultMessage: '身份验证',
            })}
            form={form}
            autoFocusFirstInput
            modalProps={{
                destroyOnClose: true,
            }}
            submitTimeout={2000}
            onFinish={onFinish}
        >

            <Tabs
                activeKey={authType}
                onChange={setAuthType}
                centered
                items={[
                    {
                        key: "PHONE",
                        label: locale.formatMessage({
                            id: 'Mobile phone number verification',
                            defaultMessage: '手机号验证',
                        }),
                    },
                    {
                        key: "EMAIL",
                        label: locale.formatMessage({
                            id: 'Email address verification',
                            defaultMessage: '邮箱号验证',
                        }),
                    },
                ]}
            />

            {tipMessage}

            <ProFormCaptcha
                name="verificationCode"
                fieldProps={{
                    size: 'large',
                    prefix: <LockOutlined/>,
                }}
                captchaProps={{
                    size: 'large',
                }}
                placeholder={locale.formatMessage({
                    id: 'pages.login.captcha.placeholder',
                    defaultMessage: '请输入验证码',
                })}
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
                rules={[
                    {
                        required: true,
                        message: (
                            <FormattedMessage
                                id="pages.login.captcha.required"
                                defaultMessage="请输入验证码！"
                            />
                        ),
                    },
                ]}
                onGetCaptcha={async (phone) => {
                    const result = "";
                    if (!result) {
                        return;
                    }
                    message.success('获取验证码成功！验证码为：1234');
                }}
            />


        </ModalForm>
    );

}

export default CaptchaModal;
