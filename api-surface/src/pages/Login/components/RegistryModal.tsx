import {ModalForm, ProFormCaptcha, ProFormText,} from '@ant-design/pro-components';
import React, {useState} from "react";
import {DrawerProps, Form, message, Select, Tabs} from "antd";
import {FormattedMessage, useIntl} from "@@/exports";
import {LockOutlined, MailOutlined, MobileOutlined, UserOutlined} from "@ant-design/icons";
import {register} from "@/services/api-security/userAccountController";
import {getVerificationCode} from "@/services/api-security/securityController";

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

    // 注册类型（邮箱或手机号）
    const [registryType, setRegistryType] = useState<string>("mobile");

    // 手机号前缀选项（如 +86）
    const [phoneOption, setPhoneOption] = useState<string>("+86")

    /**
     * 注册表单提交
     */
    const onFinish = async (fields: any) => {
        message.loading('注册中');
        try {
            const isUsingEmail = (registryType === "email");
            await register({
                username: fields.username,
                password: fields.password,
                email: isUsingEmail ? fields.email : undefined,
                phoneNumber: !isUsingEmail ? phoneOption + fields.mobile : undefined,
                strategy: isUsingEmail ? "EMAIL" : "PHONE",
                confirmedPassword: fields.confirmedPassword,
                verificationCode: fields.captcha
            });
            message.destroy();
            message.success('注册成功');
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || '注册失败，请重试！');
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
                defaultMessage: '注册账户',
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
                activeKey={registryType}
                onChange={setRegistryType}
                centered
                items={[
                    {
                        key: 'mobile',
                        label: locale.formatMessage({
                            id: 'Mobile phone number registration',
                            defaultMessage: '手机号注册',
                        }),
                    },
                    {
                        key: 'email',
                        label: locale.formatMessage({
                            id: 'Email number registration',
                            defaultMessage: '邮箱号注册',
                        }),
                    },
                ]}
            />

            {registryType === 'mobile' && (
                <>
                    <ProFormText
                        width={374}
                        addonBefore={
                            <Select style={{width: 70}}
                                    defaultValue={"+86"}
                                    onChange={value => {
                                        setPhoneOption(value)
                                    }}>
                                <option value="+86">+86</option>
                            </Select>
                        }
                        fieldProps={{
                            size: 'large',
                            prefix: <MobileOutlined/>,
                        }}
                        name="mobile"
                        label={locale.formatMessage({
                            id: 'Mobile phone number',
                            defaultMessage: '手机号',
                        })}
                        rules={[
                            {
                                required: true,
                                message: (
                                    <FormattedMessage
                                        id="Mobile phone number is required"
                                        defaultMessage="手机号是必填项！"
                                    />
                                ),
                            },
                            {
                                pattern: /^1[3-9]\d{9}$/,
                                message: (
                                    <FormattedMessage
                                        id="is not a valid phone number"
                                        defaultMessage="不是有效的手机号！"
                                    />
                                ),
                            }
                        ]}
                    />
                    <ProFormCaptcha
                        fieldProps={{
                            size: 'large',
                            prefix: <LockOutlined/>,
                        }}
                        captchaProps={{
                            size: 'large',
                        }}
                        placeholder={locale.formatMessage({
                            id: 'Please enter',
                            defaultMessage: '请输入',
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
                        name="captcha"
                        rules={[
                            {
                                required: true,
                                message: (
                                    <FormattedMessage
                                        id="Please enter the verification code"
                                        defaultMessage="验证码是必填项！"
                                    />
                                ),
                            },
                            {
                                pattern: /^\d{6}$/,
                                message: (
                                    <FormattedMessage
                                        id="is not a valid verification code"
                                        defaultMessage="不是有效的验证码！"
                                    />
                                )
                            }
                        ]}
                        onGetCaptcha={async () => {
                            // 如果需要失败会 throw 一个错误出来，onGetCaptcha 会自动停止
                            // throw new Error("获取验证码错误")
                            const phoneNumber = phoneOption
                                + form.getFieldValue('mobile');
                            await getVerificationCode({
                                phoneNumber: phoneNumber,
                                email: undefined,
                                strategy: "PHONE"
                            });
                            message.success("获取验证码成功！");
                        }}
                    />
                </>
            )}

            {registryType === 'email' && (
                <>
                    <ProFormText
                        fieldProps={{
                            size: 'large',
                            prefix: <MailOutlined/>,
                        }}
                        name="email"
                        label={locale.formatMessage({
                            id: 'pages.login.emailAddress',
                            defaultMessage: '邮箱号',
                        })}
                        rules={[
                            {
                                required: true,
                                message: (
                                    <FormattedMessage
                                        id="pages.login.emailAddress.required"
                                        defaultMessage="请输入邮箱号！"
                                    />
                                ),
                            },
                            {
                                type: 'email',
                                message: (
                                    <FormattedMessage
                                        id="pages.login.emailAddress.invalidFormat"
                                        defaultMessage="邮箱号格式不正确！"
                                    />
                                ),
                            },
                        ]}
                    />
                    <ProFormCaptcha
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
                        name="captcha"
                        rules={[
                            {
                                required: true,
                                message: (
                                    <FormattedMessage
                                        id="Please enter the verification code"
                                        defaultMessage="验证码是必填项！"
                                    />
                                ),
                            },
                            {
                                pattern: /^\d{6}$/,
                                message: (
                                    <FormattedMessage
                                        id="is not a valid verification code"
                                        defaultMessage="不是有效的验证码！"
                                    />
                                )
                            }
                        ]}
                        onGetCaptcha={async () => {
                            // 如果需要失败会 throw 一个错误出来，onGetCaptcha 会自动停止
                            // throw new Error("获取验证码错误")
                            await getVerificationCode({
                                phoneNumber: undefined,
                                email: form.getFieldValue('email'),
                                strategy: "EMAIL"
                            });
                            message.success('获取验证码成功！');
                        }}
                    />
                </>
            )}

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
                    {
                        min: 3,
                        max: 25,
                        message: (
                            <FormattedMessage
                                id="Account name length should be between 3 to 25"
                                defaultMessage="账户名长度应为 3 至 25 个字符！"
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
                    {
                        pattern: /^(?=.*\d)(?=.*\D).{8,25}$/,
                        message: (
                            <FormattedMessage
                                id="Passwords should be between 8 and 25 characters long and cannot be numeric only"
                                defaultMessage="密码长度应为 8 至 25 个字符，且不能为纯数字！"
                            />
                        ),
                    }
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
