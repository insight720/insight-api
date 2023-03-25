import {ModalForm, ProFormText,} from '@ant-design/pro-components';
import React from "react";
import {DrawerProps, Form, message} from "antd";
import {FormattedMessage, useIntl} from "@@/exports";
import {LockOutlined, UserOutlined} from "@ant-design/icons";
import {registerUser} from "@/services/api-security/userAccountController";


export type RegistryModalProps = {
    // 开启标志
    open?: DrawerProps['open'];
    // 开启函数
    onOpenChange?: (visible: boolean) => void;
};

/**
 * 注册模态框
 */
const RegistryModal: React.FC<RegistryModalProps> = (props: RegistryModalProps) => {
    // 国际化
    const locale = useIntl();
    // 表单数据
    const [form] = Form.useForm<{ name: string; company: string }>();

    /**
     * 注册表单提交
     */
    const onFinish = async (fields: any) => {
        const hide = message.loading('注册中');
        try {
            await registerUser({
                username: fields.username,
                password: fields.password,
                confirmedPassword: fields.confirmedPassword
            });
            hide();
            message.success('注册成功');
            return true;
        } catch (error: any) {
            hide();
            message.error(error.message || '注册失败，请重试！');
            return false;
        }
    };

    return (
        <ModalForm<{
            name: string;
            company: string;
        }>
            open={props.open}
            onOpenChange={props.onOpenChange}
            title={locale.formatMessage({
                id: 'Sign up for an account',
                defaultMessage: '注册账户',
            })}
            form={form}
            autoFocusFirstInput
            modalProps={{
                destroyOnClose: true,
                onCancel: () => console.log('run'),
            }}
            submitTimeout={2000}
            onFinish={onFinish}
        >
            <ProFormText
                name="username"
                label={locale.formatMessage({
                    id: 'Account name',
                    defaultMessage: '账户名',
                })}
                fieldProps={{
                    size: 'large',
                    prefix: <UserOutlined/>,
                }}
                rules={[
                    {
                        required: true,
                        message: (
                            <FormattedMessage
                                id="Account name is required"
                                defaultMessage="账户名是必填项！"
                            />
                        ),
                    },
                ]}
            />

            <ProFormText.Password
                name="password"
                label={locale.formatMessage({
                    id: 'password',
                    defaultMessage: '密码',
                })}
                fieldProps={{
                    size: 'large',
                    prefix: <LockOutlined/>,
                }}
                rules={[
                    {
                        required: true,
                        message: (
                            <FormattedMessage
                                id="Password is required"
                                defaultMessage="密码是必填项！"
                            />
                        ),
                    },
                ]}
            />

            <ProFormText.Password
                name="confirmedPassword"
                label={locale.formatMessage({
                    id: 'Confirm the password',
                    defaultMessage: '确认密码',
                })}
                fieldProps={{
                    size: 'large',
                    prefix: <LockOutlined/>,
                }}
                rules={[
                    {
                        required: true,
                        message: (
                            <FormattedMessage
                                id="Please confirm your password"
                                defaultMessage="请确认你的密码！"
                            />
                        ),
                    },
                    ({getFieldValue}) => ({
                        validator(_, value) {
                            if (!value || getFieldValue('password') === value) {
                                return Promise.resolve();
                            }
                            const errorMessage = locale.formatMessage(({
                                id: 'The two passwords you entered don\'t match',
                                defaultMessage: '你输入的两个密码不匹配！',
                            }));
                            return Promise.reject(new Error(errorMessage));
                        },
                    }),
                ]}
            />
        </ModalForm>
    );

}

export default RegistryModal;
