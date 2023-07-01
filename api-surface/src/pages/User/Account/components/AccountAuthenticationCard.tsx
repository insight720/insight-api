import {ModalForm, ProCard, ProForm, ProFormCaptcha, ProFormText,} from '@ant-design/pro-components';
import React, {useState} from "react";
import {Button, Form, message, Select, Space, Tabs, Typography} from "antd";
import {LockOutlined, MailOutlined, MobileOutlined, UserOutlined} from "@ant-design/icons";
import {FormattedMessage} from "@@/exports";
import {getVerificationCode} from "@/services/api-security/securityController";
import {useIntl} from "@umijs/max";
import {flushSync} from "react-dom";
import {
    bindPhoneOrEmail,
    deleteAccount,
    modifyPassword,
    modifyUsername,
    setUsernameAndPassword,
    unbindPhoneOrEmail
} from "@/services/api-security/userAccountController";


/**
 * 用户账户认证卡属性
 */
export type AccountAuthenticationCardProps = {
    // 当前登录用户
    currentUser?: API.LoginUserDTO;

    // 获取登录用户信息
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;

    // 设置全局初始状态
    setInitialState?: (initialState: (s: any) => any) => void
};

/**
 * 用户账户认证卡
 */
const AccountAuthenticationCard: React.FC<AccountAuthenticationCardProps> = (props: AccountAuthenticationCardProps) => {
        // 当前登录用户
        const {currentUser} = props;

        // 获取登录用户信息
        const {fetchUserInfo} = props;

        // 设置全局初始状态
        const {setInitialState} = props;

        // 验证码模态框开关
        const [captchaModalOpen, handleCaptchaModalOpen]
            = useState<boolean>(false);

        // 提示消息（用于身份验证模态框）
        const [tipMessage, setTipMessage]
            = useState<React.ReactNode>();

        // 国际化
        const locale = useIntl();

        // 表单数据
        const [form]
            = Form.useForm<{ name: string; company: string }>();

        // 验证模态框调用的函数
        const [verificationFinish, setVerificationFinish]
            = useState<OnFinishTypeEnum>();

        // 手机号前缀选项（如 +86）
        const [phoneOption, setPhoneOption]
            = useState<string>("+86")

        // 有密码的验证码模态框选项卡选项
        const passwordItems = [
            {
                key: 'PASSWORD',
                label: locale.formatMessage({
                    id: 'Password verification',
                    defaultMessage: '密码验证',
                }),
            },
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
        ];

        // 没有密码的验证码模态框选项卡选项
        const nonPasswordItems = [
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
        ];

        // 只有邮箱的验证码模态框选项卡选项
        const emailItems = [
            {
                key: "EMAIL",
                label: locale.formatMessage({
                    id: 'Email address verification',
                    defaultMessage: '邮箱号验证',
                }),
            },
        ];

        // 只有手机的验证码模态框选项卡选项
        const phoneItems = [
            {
                key: "PHONE",
                label: locale.formatMessage({
                    id: 'Mobile phone number verification',
                    defaultMessage: '手机号验证',
                }),
            },
        ];

        // 无需验证的验证码模态框选项卡选项
        const noItems = [
            {
                key: "NO",
                label: locale.formatMessage({
                    id: 'You\'ve only logged in with a third party and you don\'t need to verify to delete your account',
                    defaultMessage: '无需验证',
                }),
            },
        ];

        // 验证模态框的选项卡选项
        const [verificationTabItems, setVerificationTabItems]
            = useState<{ key: string; label: string; }[]>();

        // 身份验证策略
        const [authStrategy, setAuthStrategy]
            = useState<string>();

        /**
         * 刷新用户信息
         */
        async function flushUserInfo() {
            // 获取新的用户信息，并设置到全局状态中
            const newCurrentUser = await fetchUserInfo?.();
            if (newCurrentUser) {
                flushSync(() => {
                    setInitialState?.((s) => ({
                        ...s,
                        currentUser: newCurrentUser,
                    }));
                });
            }
        }

    /**
     * 确定身份验证策略
     *
     * 如果用户没有绑定对应的信息，则进行错误提示，并返回 null。
     */
    const determineAuthStrategy = () => {
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
    };

        // 修改账户名的提示消息
        const modifyUsernameTipMessage: React.ReactNode = (
            <Typography.Text>
                <Typography.Text strong>
                    账户名主要用于登录平台，且是平台内唯一的。
                </Typography.Text>
                新账户名可能已经被其他用户使用，请尽量选择独特的账户名。
                <Typography.Text strong>
                    修改账户名需进行已绑定邮箱或手机号的验证码身份验证
                </Typography.Text>
                。如未绑定，请前往
                <Typography.Text italic strong>
                    认证设置
                </Typography.Text>
                并至少绑定一个邮箱或手机号。感谢您的支持！
            </Typography.Text>
        );

        // 修改密码的提示消息
        const modifyPasswordTipMessage: React.ReactNode = (
            <Typography.Text>
                <Typography.Text strong>
                    修改密码可以通过原密码进行身份验证，也可使用已绑定邮箱或手机号的验证码身份验证
                </Typography.Text>
                。如需绑定邮箱或手机号，请前往
                <Typography.Text italic strong>
                    认证设置
                </Typography.Text>
                并至少绑定一个邮箱或手机号。感谢您的支持！
            </Typography.Text>
        );

        // 设置账户名的提示消息
        const setUsernameAndPasswordTipMessage: React.ReactNode = (
            <Typography.Text>
                平台没有账户名的用户是仅使用过第三方登录的用户。
                <Typography.Text strong>
                    账户名是平台内唯一的，主要用户登录平台，所以设置账户名的同时必须设置密码。
                </Typography.Text>
                新账户名可能已经被其他用户使用，请尽量选择独特的账户名。
                <Typography.Text strong>
                    设置账户名需进行已绑定邮箱或手机号的验证码身份验证
                </Typography.Text>
                。如未绑定，请前往
                <Typography.Text italic strong>
                    认证设置
                </Typography.Text>
                并至少绑定一个邮箱或手机号。感谢您的支持！
            </Typography.Text>
        );

        // 删除账户的提示消息
        const deleteAccountTipMessage: React.ReactNode = (
            <Typography.Text>
                <Typography.Text strong>
                    删除账户将停止所有平台服务，请谨慎操作
                </Typography.Text>
                <Typography.Text>
                    ！如果您没有绑定邮箱或手机，您可以直接删除账户，无需进行验证。这种情况的用户通常只使用第三方登录，没有自己的账户名和密码。
                </Typography.Text>
                <Typography.Text strong>
                    如果您已经绑定了邮箱或手机，您需要进行验证码身份验证。
                </Typography.Text>
            </Typography.Text>
        );

        // 绑定手机的提示消息
        const bindPhoneNumberTipMessage: React.ReactNode = (
            <Typography.Text>
                <Typography.Text strong>
                    绑定手机需要验证短信验证码，一个手机只能绑定一个账号，且只支持中国大陆的手机号码。
                </Typography.Text>
                <Typography.Text>
                    手机验证是使用平台功能的主要身份验证方法，也可以用于登录平台，可以帮助您更好地保护账号安全。
                </Typography.Text>
            </Typography.Text>
        );

        // 解绑手机的提示消息
        const unbindPhoneNumberTipMessage: React.ReactNode = (
            <Typography.Text>
                <Typography.Text strong>
                    解绑手机需要进行短信验证码验证，并且您必须已绑定邮箱，以确保至少有一种验证方式可用。
                </Typography.Text>
                <Typography.Text>
                    手机验证是使用平台功能的主要身份验证方法，此外也可以用于登录。除特殊情况外，不建议解绑。
                </Typography.Text>
            </Typography.Text>
        );

        // 绑定邮箱的提示消息
        const bindEmailAddressTipMessage: React.ReactNode = (
            <Typography.Text>
                <Typography.Text strong>
                    绑定邮箱需要验证邮件验证码，一个邮箱只能绑定一个账号。
                </Typography.Text>
                <Typography.Text>
                    邮箱验证是使用平台功能的主要身份验证方法，也可以用于登录平台，可以帮助您更好地保护账号安全。
                </Typography.Text>
            </Typography.Text>
        );

        // 解绑邮箱的提示消息
        const unbindEmailAddressTipMessage: React.ReactNode = (
            <Typography.Text>
                <Typography.Text strong>
                    解绑邮箱需要进行邮件验证码验证，并且您必须已绑定手机，以确保至少有一种验证方式可用。
                </Typography.Text>
                <Typography.Text>
                    邮箱验证是使用平台功能的主要身份验证方法，此外也可以用于登录。除特殊情况外，不建议解绑。
                </Typography.Text>
            </Typography.Text>
        );

        /**
         * 修改用户名提交函数
         */
        const modifyUsernameOnFinish = async (values: any) => {
            // 检查用户是否绑定信息
            const isUsingPhone = determineAuthStrategy();
            if (isUsingPhone === null) {
                return false;
            }
            // 检查要修改的账户名是否与原账户名相同
            const originalUsername = currentUser?.username;
            const newUsername = values.newUsername;
            if (originalUsername === newUsername) {
                message.error("修改的账户名与原账户名相同");
                return false;
            }
            message.loading("修改账户名中");
            try {
                await modifyUsername({
                    accountId: currentUser?.accountId,
                    newUsername: values.newUsername,
                    codeCheckDTO: {
                        phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                        emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                        strategy: authStrategy || "",
                        verificationCode: values.verificationCode
                    }
                })
                // 刷新用户信息
                await flushUserInfo();
                message.destroy();
                message.success("修改账户名成功");
                return true;
            } catch (error: any) {
                message.destroy();
                message.error(error.message || "修改账户名失败，请稍后再试");
                return false;
            }
        };

        /**
         * 设置用户名和密码的提交函数
         */
        const setUsernameAndPasswordOnFinish = async (values: any) => {
            // 检查用户是否绑定信息
            const isUsingPhone = determineAuthStrategy();
            if (isUsingPhone === null) {
                return false;
            }
            const originalUsername = currentUser?.username;
            if (originalUsername) {
                // Should never get here
                message.error("系统错误，你已拥有账户名");
                return false;
            }
            message.loading("设置账户名和密码中");
            try {
                await setUsernameAndPassword({
                    accountId: currentUser?.accountId,
                    newUsername: values.newUsername,
                    newPassword: values.newPassword,
                    codeCheckDTO: {
                        phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                        emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                        strategy: authStrategy || "",
                        verificationCode: values.verificationCode
                    }
                })
                // 刷新用户信息
                await flushUserInfo();
                message.destroy();
                message.success("设置账户名和密码成功");
                return true;
            } catch (error: any) {
                message.destroy();
                message.error(error.message || "设置账户名和密码失败，请稍后再试");
                return false;
            }
        };

        /**
         * 修改密码的提交函数
         */
        const modifyPasswordOnFinish = async (values: any) => {
            // @ts-ignored
            const modificationDTO: API.PasswordModificationDTO = {};
            if (authStrategy === "PASSWORD") {
                // 设置原来的密码
                modificationDTO.originalPassword = values.originalPassword;
            } else {
                // 检查用户是否绑定信息
                const isUsingPhone = determineAuthStrategy();
                if (isUsingPhone === null) {
                    return false;
                }
                // 设置验证码信息
                modificationDTO.codeCheckDTO = {
                    phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                    emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                    strategy: authStrategy || "",
                    verificationCode: values.verificationCode
                }
            }
            // 设置其他信息
            modificationDTO.accountId = currentUser?.accountId;
            modificationDTO.newPassword = values.newPassword;
            message.loading("修改密码中");
            try {
                await modifyPassword(modificationDTO);
                // 不必刷新用户信息
                message.destroy();
                message.success("修改密码成功");
                return true;
            } catch (error: any) {
                message.destroy();
                message.error(error.message || "修改密码失败，请稍后再试");
                return false;
            }
        };

        /**
         * 删除账户的提交函数
         */
        const deleteAccountOnFinish = async (values: any) => {
            // @ts-ignored
            const accountCodeCheckDTO: API.AccountVerificationCodeCheckDTO = {};
            if (authStrategy !== "NO") {
                // 检查用户是否绑定信息
                const isUsingPhone = determineAuthStrategy();
                if (isUsingPhone === null) {
                    return false;
                }
                accountCodeCheckDTO.codeCheckDTO = {
                    phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                    emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                    strategy: authStrategy || "",
                    verificationCode: values.verificationCode
                };
            }
            accountCodeCheckDTO.accountId = currentUser?.accountId;
            message.loading("删除账户中");
            try {
                await deleteAccount(accountCodeCheckDTO);
                // 刷新用户信息
                await flushUserInfo();
                message.destroy();
                message.success("删除账户成功，2 秒后跳转至登录页");
                // 页面跳转
                setTimeout(() => {
                    window.location.replace("/login");
                }, 2000);
                return true;
            } catch (error: any) {
                message.destroy();
                message.error(error.message || "删除账户失败，请稍后再试");
                return false;
            }
        };

        /**
         * 绑定手机或邮箱的提交函数
         */
        const bindPhoneOrEmailOnFinish = async (values: any) => {
            const isUsingPhone = (authStrategy === "PHONE");
            if (isUsingPhone && currentUser?.phoneNumber) {
                message.error("系统错误，你已绑定手机号");
                return false;
            } else if (!isUsingPhone && currentUser?.emailAddress) {
                message.error("系统错误，你已绑定邮箱");
                return false;
            }
            const argument = isUsingPhone ? "手机号" : "邮箱";
            message.loading("绑定" + argument + "中");
            try {
                await bindPhoneOrEmail({
                    accountId: currentUser?.accountId,
                    codeCheckDTO: {
                        phoneNumber: isUsingPhone ? phoneOption + values.phone : undefined,
                        emailAddress: !isUsingPhone ? values.email : undefined,
                        strategy: authStrategy || "",
                        verificationCode: values.verificationCode
                    }
                });
                // 刷新用户信息
                await flushUserInfo();
                message.destroy();
                message.success("绑定" + argument + "成功");
                return true;
            } catch (error: any) {
                message.destroy();
                message.error(error.message || "绑定" + argument + "失败，请稍后再试");
                return false;
            }
        };

        /**
         * 解绑手机或邮箱的提交函数
         */
        const unbindPhoneOrEmailOnFinish = async (values: any) => {
            // 检查用户是否绑定信息
            const isUsingPhone = determineAuthStrategy();
            if (isUsingPhone === null) {
                return false;
            }
            if (isUsingPhone && !currentUser?.emailAddress) {
                message.error("解绑手机必须已绑定邮箱，以确保至少有一种验证方式可用")
                return false;
            }
            if (!isUsingPhone && !currentUser?.phoneNumber) {
                message.error("解绑邮箱必须已绑定手机，以确保至少有一种验证方式可用")
                return false;
            }
            const argument = isUsingPhone ? "手机号" : "邮箱";
            message.loading("解绑" + argument + "中");
            try {
                await unbindPhoneOrEmail({
                    accountId: currentUser?.accountId,
                    codeCheckDTO: {
                        phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                        emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                        strategy: authStrategy || "",
                        verificationCode: values.verificationCode
                    }
                });
                // 刷新用户信息
                await flushUserInfo();
                message.destroy();
                message.success("解绑" + argument + "成功");
                return true;
            } catch (error: any) {
                message.destroy();
                message.error(error.message || "解绑" + argument + "失败，请稍后再试");
                return false;
            }
        };

        /**
         * 默认的发送验证码函数
         */
        const defaultOnGetCaptcha = async () => {
            // 检查用户是否绑定信息
            const isUsingPhone = determineAuthStrategy();
            if (isUsingPhone === null) {
                throw new Error();
            }
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
        };

        /**
         * 绑定手机或邮箱的发送验证码函数
         */
        const bindPhoneOrEmailOnGetCaptcha = async () => {
            // 绑定邮箱或手机
            const isUsingPhone = (authStrategy === "PHONE");
            if (isUsingPhone && currentUser?.phoneNumber) {
                message.error("系统错误，你已绑定手机号");
                throw new Error();
            } else if (!isUsingPhone && currentUser?.emailAddress) {
                message.error("系统错误，你已绑定邮箱");
                throw new Error();
            }
            try {
                await getVerificationCode({
                    phoneNumber: isUsingPhone ? phoneOption + form.getFieldValue("phone") : undefined,
                    emailAddress: !isUsingPhone ? form.getFieldValue("email") : undefined,
                    strategy: authStrategy || ""
                });
                message.success("获取验证码成功");
            } catch (error: any) {
                message.error(error.message || "获取验证码失败");
                // 让等待状态结束
                throw error;
            }
        };

        /**
         * 解绑手机或邮箱的发送验证码函数
         */
        const unbindPhoneOrEmailOnGetCaptcha = async () => {
            // 检查用户是否绑定信息
            const isUsingPhone = determineAuthStrategy();
            if (isUsingPhone === null) {
                throw new Error();
            }
            if (isUsingPhone && !currentUser?.emailAddress) {
                message.error("解绑手机必须已绑定邮箱，以确保至少有一种验证方式可用")
                throw new Error();
            }
            if (!isUsingPhone && !currentUser?.phoneNumber) {
                message.error("解绑邮箱必须已绑定手机，以确保至少有一种验证方式可用")
                throw new Error();
            }
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
        };

        /**
         * 验证码模态框 onFinish 的类型
         */
        enum OnFinishTypeEnum {
            // 修改用户名
            MODIFY_USERNAME = 1,
            // 设置用户名和密码
            SET_USERNAME_AND_PASSWORD = 2,
            // 修改用户名密码
            MODIFY_PASSWORD = 3,
            // 删除账户
            DELETE_ACCOUNT = 4,
            // 绑定手机或邮箱
            BIND_PHONE_OR_EMAIL_ON_FINISH = 5,
            // 解绑手机或邮箱
            UNBIND_PHONE_OR_EMAIL_ON_FINISH = 6
        }

        return (
            <><ModalForm<{
                name: string;
                company: string;
            }>
                // 验证码模态框
                onFinish={async (formData) => {
                    // 根据点击按钮的类型选择对应的函数
                    switch (verificationFinish) {
                        case OnFinishTypeEnum.MODIFY_USERNAME:
                            return modifyUsernameOnFinish(formData);
                        case OnFinishTypeEnum.SET_USERNAME_AND_PASSWORD:
                            return setUsernameAndPasswordOnFinish(formData);
                        case OnFinishTypeEnum.DELETE_ACCOUNT:
                            return deleteAccountOnFinish(formData);
                        case OnFinishTypeEnum.MODIFY_PASSWORD:
                            return modifyPasswordOnFinish(formData);
                        case OnFinishTypeEnum.BIND_PHONE_OR_EMAIL_ON_FINISH:
                            return bindPhoneOrEmailOnFinish(formData);
                        case OnFinishTypeEnum.UNBIND_PHONE_OR_EMAIL_ON_FINISH:
                            return unbindPhoneOrEmailOnFinish(formData);
                    }
                }}
                width={500}
                open={captchaModalOpen}
                onOpenChange={handleCaptchaModalOpen}
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
            >
                <Tabs
                    activeKey={authStrategy}
                    onChange={setAuthStrategy}
                    centered
                    items={verificationTabItems}/>
                {tipMessage}
                <br/>
                <br/>
                {(verificationFinish === OnFinishTypeEnum.MODIFY_USERNAME
                        || verificationFinish === OnFinishTypeEnum.SET_USERNAME_AND_PASSWORD) &&
                    <ProFormText
                        name="newUsername"
                        label={locale.formatMessage({
                            id: 'Account name',
                            defaultMessage: '新账户名',
                        })}
                        fieldProps={{
                            size: 'middle',
                            prefix: <UserOutlined/>,
                        }}
                        rules={[
                            {
                                required: true,
                                message: (
                                    <FormattedMessage
                                        id="Account name is required"
                                        defaultMessage="账户名是必填项"
                                    />
                                ),
                            },
                            {
                                pattern: /^\S.{1,23}\S$/,
                                message: "账户名长度应为 3 到 25 个字符，且不能仅含空白字符",
                            },
                        ]}
                    />
                }

                {authStrategy === "PHONE" && verificationFinish === OnFinishTypeEnum.BIND_PHONE_OR_EMAIL_ON_FINISH
                    && <ProFormText
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
                        name="phone"
                        label={locale.formatMessage({
                            id: 'Mobile phone number',
                            defaultMessage: '手机号',
                        })}
                        placeholder={locale.formatMessage({
                            id: 'Please enter',
                            defaultMessage: '请输入手机号',
                        })}
                        rules={[
                            {
                                required: true,
                                message: (
                                    <FormattedMessage
                                        id="Mobile phone number is required"
                                        defaultMessage="手机号是必填项"
                                    />
                                ),
                            },
                            {
                                pattern: /^1[3-9]\d{9}$/,
                                message: (
                                    <FormattedMessage
                                        id="is not a valid phone number"
                                        defaultMessage="不是有效的手机号"
                                    />
                                ),
                            }
                        ]}
                    />}

                {authStrategy === "EMAIL" && verificationFinish === OnFinishTypeEnum.BIND_PHONE_OR_EMAIL_ON_FINISH
                    && <ProFormText
                        fieldProps={{
                            size: 'large',
                            prefix: <MailOutlined/>,
                        }}
                        name="email"
                        label={locale.formatMessage({
                            id: 'pages.login.emailAddress',
                            defaultMessage: '邮箱号',
                        })}
                        placeholder={locale.formatMessage({
                            id: 'Please enter',
                            defaultMessage: '请输入邮箱号',
                        })}
                        rules={[
                            {
                                required: true,
                                message: (
                                    <FormattedMessage
                                        id="Please enter your email number"
                                        defaultMessage="请输入邮箱号"
                                    />
                                ),
                            },
                            {
                                type: 'email',
                                message: (
                                    <FormattedMessage
                                        id="The mailbox number format is incorrect"
                                        defaultMessage="邮箱号格式不正确"
                                    />
                                ),
                            },
                        ]}
                    />}

                {authStrategy === "PASSWORD" &&
                    <ProFormText.Password
                        name="originalPassword"
                        label={locale.formatMessage({
                            id: 'The original password',
                            defaultMessage: '原密码',
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
                                        defaultMessage="密码是必填项"
                                    />
                                ),
                            },
                            {
                                pattern: /^(?=.*\d)(?=.*\D).{8,25}$/,
                                message: (
                                    <FormattedMessage
                                        id="Passwords should be between 8 and 25 characters long and cannot be numeric only"
                                        defaultMessage="密码长度应为 8 至 25 个字符，且不能为纯数字"
                                    />
                                ),
                            }
                        ]}
                    />
                }

                {(verificationFinish === OnFinishTypeEnum.MODIFY_PASSWORD
                        || verificationFinish === OnFinishTypeEnum.SET_USERNAME_AND_PASSWORD)
                    && <ProFormText.Password
                        name="newPassword"
                        label={locale.formatMessage({
                            id: 'new password',
                            defaultMessage: '新密码',
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
                                        defaultMessage="密码是必填项"
                                    />
                                ),
                            },
                            {
                                pattern: /^(?=.*\d)(?=.*\D).{8,25}$/,
                                message: (
                                    <FormattedMessage
                                        id="Passwords should be between 8 and 25 characters long and cannot be numeric only"
                                        defaultMessage="密码长度应为 8 至 25 个字符，且不能为纯数字"
                                    />
                                ),
                            }
                        ]}
                    />
                }

                {authStrategy !== "PASSWORD" && authStrategy !== "NO" &&
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
                            id: 'Please enter the verification code',
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
                                        id="Please enter the verification code"
                                        defaultMessage="验证码是必填项"/>
                                ),
                            },
                            {
                                pattern: /^\d{6}$/,
                                message: (
                                    <FormattedMessage
                                        id="is not a valid verification code"
                                        defaultMessage="不是有效的验证码"/>
                                )
                            }
                        ]}
                        onGetCaptcha={async () => {
                            switch (verificationFinish) {
                                case OnFinishTypeEnum.BIND_PHONE_OR_EMAIL_ON_FINISH:
                                    await bindPhoneOrEmailOnGetCaptcha();
                                    break;
                                case OnFinishTypeEnum.UNBIND_PHONE_OR_EMAIL_ON_FINISH:
                                    await unbindPhoneOrEmailOnGetCaptcha();
                                    break;
                                default:
                                    await defaultOnGetCaptcha();
                                    break;
                            }
                        }
                        }/>
                }
            </ModalForm>

                <ProCard>
                    <ProForm.Group title={"账户设置"} tooltip={"在此处可以修改账户设置信息。"}/>

                    <Space align={"start"} size={"large"}>
                        <ProFormText tooltip="账户名用于登录和展示在页面上。"
                                     label="账户名">
                            <Typography.Text
                                copyable={!!currentUser?.username}
                                strong={!!currentUser?.username}
                                italic={!currentUser?.username}>
                                {currentUser?.username || "你还没有账户名，可点击设置账户名"}
                            </Typography.Text>
                        </ProFormText>

                        {currentUser?.username
                            && <Button size={"middle"} type="default"
                                       onClick={async () => {
                                           setAuthStrategy("PHONE");
                                           setVerificationTabItems(nonPasswordItems);
                                           setTipMessage(modifyUsernameTipMessage)
                                           setVerificationFinish(OnFinishTypeEnum.MODIFY_USERNAME);
                                           handleCaptchaModalOpen(true);
                                       }}>
                                修改账户名
                            </Button>}

                        {!currentUser?.username
                            && <Button size={"middle"} type="default"
                                       onClick={async () => {
                                           setAuthStrategy("PHONE");
                                           setVerificationTabItems(nonPasswordItems);
                                           setTipMessage(setUsernameAndPasswordTipMessage)
                                           setVerificationFinish(OnFinishTypeEnum.SET_USERNAME_AND_PASSWORD);
                                           handleCaptchaModalOpen(true);
                                       }}>
                                设置账户名
                            </Button>}
                    </Space>

                    <ProFormText label={"其他设置"} tooltip={"在此处可以修改其他的账户设置信息。"}>

                        <Space align={"center"} size={"large"}>
                            {currentUser?.username &&
                                <Button size={"middle"} type="default"
                                        onClick={async () => {
                                            setAuthStrategy("PASSWORD");
                                            setVerificationTabItems(passwordItems);
                                            setTipMessage(modifyPasswordTipMessage)
                                            setVerificationFinish(OnFinishTypeEnum.MODIFY_PASSWORD);
                                            handleCaptchaModalOpen(true);
                                        }}>修改密码</Button>
                            }
                            <Button size={"middle"} type="default"
                                    onClick={async () => {
                                        if (currentUser?.phoneNumber || currentUser?.emailAddress) {
                                            setAuthStrategy("PHONE");
                                            setVerificationTabItems(nonPasswordItems);
                                        } else {
                                            setAuthStrategy("NO")
                                            setVerificationTabItems(noItems);
                                        }
                                        setTipMessage(deleteAccountTipMessage)
                                        setVerificationFinish(OnFinishTypeEnum.DELETE_ACCOUNT);
                                        handleCaptchaModalOpen(true);
                                    }}>删除账户</Button>
                        </Space>
                    </ProFormText>

                    <ProForm.Group title={"手机和邮箱"} tooltip={"手机和邮箱主要用于验证码身份验证。"}/>

                    <ProForm.Group align={"start"} size={"large"}>
                        <Space align={"start"} size={"large"}>
                            <ProFormText tooltip="中国大陆的手机号码。（电话区号 +86）"
                                         label="手机">
                                <Typography.Text
                                    copyable={!!currentUser?.phoneNumber}
                                    strong={!!currentUser?.phoneNumber}
                                    italic={!currentUser?.phoneNumber}>
                                    {currentUser?.phoneNumber || "你还没有绑定手机"}
                                </Typography.Text>
                            </ProFormText>
                            {currentUser?.phoneNumber
                                && <Button size={"middle"} type="default"
                                           onClick={async () => {
                                               setAuthStrategy("PHONE");
                                               setVerificationTabItems(phoneItems);
                                               setTipMessage(unbindPhoneNumberTipMessage)
                                               setVerificationFinish(OnFinishTypeEnum.UNBIND_PHONE_OR_EMAIL_ON_FINISH);
                                               handleCaptchaModalOpen(true);
                                           }}>
                                    解绑手机
                                </Button>}
                            {!currentUser?.phoneNumber
                                && <Button size={"middle"} type="default"
                                           onClick={async () => {
                                               setAuthStrategy("PHONE");
                                               setVerificationTabItems(phoneItems);
                                               setTipMessage(bindPhoneNumberTipMessage)
                                               setVerificationFinish(OnFinishTypeEnum.BIND_PHONE_OR_EMAIL_ON_FINISH);
                                               handleCaptchaModalOpen(true);
                                           }}>
                                    绑定手机
                                </Button>}
                        </Space>
                    </ProForm.Group>

                    <ProForm.Group align={"start"} size={"large"}>
                        <Space align={"start"} size={"large"}>
                            <ProFormText tooltip="电子邮箱地址。"
                                         label="邮箱">
                                <Typography.Text
                                    copyable={!!currentUser?.emailAddress}
                                    strong={!!currentUser?.emailAddress}
                                    italic={!currentUser?.emailAddress}>
                                    {currentUser?.emailAddress || "你还没有绑定邮箱"}
                                </Typography.Text>
                            </ProFormText>
                            {currentUser?.emailAddress
                                && <Button size={"middle"} type="default"
                                           onClick={async () => {
                                               setAuthStrategy("EMAIL");
                                               setVerificationTabItems(emailItems);
                                               setTipMessage(unbindEmailAddressTipMessage)
                                               setVerificationFinish(OnFinishTypeEnum.UNBIND_PHONE_OR_EMAIL_ON_FINISH);
                                               handleCaptchaModalOpen(true);
                                           }}>
                                    解绑邮箱
                                </Button>}
                            {!currentUser?.emailAddress
                                && <Button size={"middle"} type="default"
                                           onClick={async () => {
                                               setAuthStrategy("EMAIL");
                                               setVerificationTabItems(emailItems);
                                               setTipMessage(bindEmailAddressTipMessage)
                                               setVerificationFinish(OnFinishTypeEnum.BIND_PHONE_OR_EMAIL_ON_FINISH);
                                               handleCaptchaModalOpen(true);
                                           }}>
                                    绑定邮箱
                                </Button>}
                        </Space>
                    </ProForm.Group>

                    <ProForm.Group size={"large"} title={"第三方登录"} align={"baseline"}
                                   tooltip={"第三方登录是指用户可以通过使用其已有的第三方账号（如 QQ、WeChat 等）直接登录到本网站，" +
                                       "而无需再次注册。这种方式可以减少用户注册流程的繁琐性和时间成本，给用户带来更好的使用体验。"}>
                        <Space size={"large"} align={"center"}>
                            <Typography.Text>
                                QQ：
                                <Typography.Text strong>
                                    {true ? "已绑定" : "未绑定"}
                                </Typography.Text>
                            </Typography.Text>
                            <Button size={"middle"} type="default">解绑</Button>
                        </Space>
                        <Space size={"large"} align={"center"}>
                            <Typography.Text>
                                微信：
                                <Typography.Text strong>
                                    {false ? "已绑定" : "未绑定"}
                                </Typography.Text>
                            </Typography.Text>
                            <Button size={"middle"} type="default">绑定</Button>
                        </Space>
                    </ProForm.Group>
                </ProCard>
            </>
        );
    };

export default AccountAuthenticationCard;
