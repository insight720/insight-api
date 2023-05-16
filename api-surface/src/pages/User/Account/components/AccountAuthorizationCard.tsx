import {ModalForm, ProCard, ProForm, ProFormCaptcha, ProFormText,} from '@ant-design/pro-components';
import React, {useState} from "react";
import {Button, Checkbox, Form, message, Space, Tabs, Typography} from "antd";

import {flushSync} from "react-dom";
import {LockOutlined} from "@ant-design/icons";
import {FormattedMessage, useIntl} from "@@/exports";
import {getVerificationCode} from "@/services/api-security/securityController";
import {CheckboxChangeEvent} from "antd/es/checkbox";
import {
    getNewApiKey,
    modifyApiKeyStatus,
    modifyNonAdminAuthority,
    viewSecretKey
} from "@/services/api-security/userAccountController";

/**
 * 用户账户授权卡属性
 */
export type AccountAuthorizationCardProps = {
    // 当前登录用户
    currentUser?: API.LoginUserDTO;

    // 获取登录用户信息
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;

    // 设置全局初始状态
    setInitialState?: (initialState: (s: any) => any) => void
};

/**
 * 用户账户授权卡
 */
const AccountAuthorizationCard: React.FC<AccountAuthorizationCardProps> = (props: AccountAuthorizationCardProps) => {
    // 当前登录用户
    const {currentUser} = props;

    // 获取登录用户信息
    const {fetchUserInfo} = props;

    // 设置全局初始状态
    const {setInitialState} = props;

    // 密钥值
    const [secretKey, setSecretKey] = useState<string>();

    // 验证码模态框开关
    const [captchaModalOpen, handleCaptchaModalOpen] = useState<boolean>(false);

    // 提示消息（用于身份验证模态框）
    const [tipMessage, setTipMessage] = useState<React.ReactNode>();

    // 国际化
    const locale = useIntl();

    // 表单数据
    const [form] = Form.useForm<{ name: string; company: string }>();

    // 身份验证策略
    const [authStrategy, setAuthStrategy] = useState<string>('PHONE');

    // 选择权限 Tab 的 Key
    const [authorityTabKey, setAuthorityTabKey] = useState<string>('CHECK');

    // 验证模态框调用的函数
    const [verificationFinish, setVerificationFinish]
        = useState<OnFinishTypeEnum>();

    // 目标密钥状态
    const [targetApiKeyStatus, setTargetApiKeyStatus]
        = useState<ApiKeyStatusEnum>();

    // 权限模态框开关
    const [authorityModalOpen, setAuthorityModalOpen]
        = useState(false);

    // 初始权限勾选值
    const initialAuthorityChecked: { [key: string]: boolean } = {
        ROLE_USER: currentUser?.authoritySet?.includes('ROLE_USER') ?? false,
        ROLE_TEST: currentUser?.authoritySet?.includes('ROLE_TEST') ?? false,
        ROLE_ADMIN: currentUser?.authoritySet?.includes('ROLE_ADMIN') ?? false,
    };

    // 权限勾选框
    const [authorityChecked, setAuthorityChecked]
        = useState(initialAuthorityChecked);

    // 查看访问密钥的提示消息
    const accessKeyTipMessage: React.ReactNode = (
        <Typography.Text>
            为保障 API 密钥安全，
            <Typography.Text strong>
                查看密钥需进行已绑定邮箱或手机号的验证码身份验证
            </Typography.Text>
            。如未绑定，请前往
            <Typography.Text italic strong>
                认证设置
            </Typography.Text>
            并至少绑定一个邮箱或手机号。感谢您的支持！
        </Typography.Text>
    );

    // 新建密钥的提示消息
    const newKeyTipMessage: React.ReactNode = (
        <Typography.Text>
            请注意，
            <Typography.Text strong>
                新建密钥会使原有的密钥失效
            </Typography.Text>
            。同时，为了保障密钥安全，
            <Typography.Text strong>
                您需进行已绑定邮箱或手机号的验证码身份验证
            </Typography.Text>
            。如未绑定，请前往
            <Typography.Text italic strong>
                认证设置
            </Typography.Text>
            并至少绑定一个邮箱或手机号。感谢您的支持！
        </Typography.Text>
    );

    // 启用或禁用密钥的提示消息
    const enableOrDisableTipMessage: React.ReactNode = (
        <Typography.Text>
            为保障 API 使用安全，
            <Typography.Text strong>
                修改密钥状态需进行已绑定邮箱或手机号的验证码身份验证
            </Typography.Text>
            。如未绑定，请前往
            <Typography.Text italic strong>
                认证设置
            </Typography.Text>
            并至少绑定一个邮箱或手机号。感谢您的支持！
        </Typography.Text>
    );

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
     * 验证码模态框 onFinish 的类型
     */
    enum OnFinishTypeEnum {
        // 查看访问密钥
        VIEW_SECRET_KEY = 1,
        // 创建新密钥
        GET_NEW_API_KEY = 2,
        // 修改 API 密钥状态
        MODIFY_API_KEY_STATUS = 3
    }

    /**
     * API 密钥状态枚举
     */
    enum ApiKeyStatusEnum {
        NORMAL_KEY_AVAILABLE = "NORMAL_KEY_AVAILABLE",
        NORMAL_KEY_UNAVAILABLE = "NORMAL_KEY_UNAVAILABLE"
    }

    /**
     * 权限枚举
     */
    enum AuthorityEnum {
        ROLE_USER = "ROLE_USER",
        ROLE_TEST = "ROLE_TEST",
        ROLE_ADMIN = "ROLE_ADMIN"
    }

    /**
     * 权限描述枚举
     */
    enum AuthorityDescriptionEnum {
        ROLE_USER = "用户",
        ROLE_TEST = "测试",
        ROLE_ADMIN = "管理员"
    }

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
     * 显示 SecretKey
     */
    const viewSecretKeyOnCaptchaFinish = async (values: any) => {
        // 检查用户是否绑定信息
        const isUsingPhone = determineAuthStrategy();
        if (isUsingPhone === null) {
            return false;
        }
        message.loading("获取密钥中");
        try {
            const result = await viewSecretKey({
                accountId: currentUser?.accountId,
                codeCheckDTO: {
                    phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                    emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                    strategy: authStrategy,
                    verificationCode: values.verificationCode
                }
            });
            // 将获取到密钥对用于显示
            setSecretKey(result.data);
            message.destroy();
            message.success("获取密钥成功，请尽快保存");
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || '获取密钥失败，请稍后再试');
            return false;
        }
    };

    /**
     * 获取新的 API 密钥
     */
    const getNewApiKeyOnFinish = async (values: any) => {
        // 检查用户是否绑定信息
        const isUsingPhone = determineAuthStrategy();
        if (isUsingPhone === null) {
            return false;
        }
        message.loading("创建新密钥中");
        try {
            const result = await getNewApiKey({
                accountId: currentUser?.accountId,
                codeCheckDTO: {
                    phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                    emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                    strategy: authStrategy,
                    verificationCode: values.verificationCode
                }
            });
            // 刷新用户信息
            await flushUserInfo();
            // 将获取到密钥对用于显示
            setSecretKey(result.data);
            message.destroy();
            message.success("新密钥创建成功，请尽快保存");
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || '创建密钥新失败，请稍后再试');
            return false;
        }
    };

    /**
     * 修改 API 密钥状态
     */
    const modifyApiKeyStatusOnFinish = async (values: any) => {
        // 检查用户是否绑定信息
        const isUsingPhone = determineAuthStrategy();
        if (isUsingPhone === null) {
            return false;
        }
        const originalStatus = currentUser?.accountStatus;
        if (originalStatus === targetApiKeyStatus) {
            // Should never get here
            message.error("目标密钥状态与原状态相同")
            return false;
        }
        message.loading("修改密钥状态中");
        try {
            await modifyApiKeyStatus({
                newStatus: targetApiKeyStatus || "",
                accountId: currentUser?.accountId,
                codeCheckDTO: {
                    phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                    emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                    strategy: authStrategy,
                    verificationCode: values.verificationCode
                }
            });
            // 刷新用户信息
            await flushUserInfo();
            message.destroy();
            message.success("修改密钥状态成功");
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || "修改密钥状态失败，请稍后再试");
            return false;
        }
    };

    /**
     * 处理 Authority 值改变事件
     */
    const handleAuthorityCheckboxChange = (name: string) => (event: CheckboxChangeEvent) => {
        setAuthorityChecked({
            ...authorityChecked,
            [name]: event.target.checked,
        });
    };

    /**
     * 权限模态框提交函数
     */
    const onAuthorityModalFinish = async () => {
        // 转换为权限 string[]
        const targetAuthoritySet = Object.keys(authorityChecked)
            .filter(key => authorityChecked[key])
            .map(key => key);
        if (targetAuthoritySet.length === 0) {
            message.error("至少拥有一个权限");
            return false;
        }
        const authoritySet = currentUser?.authoritySet || [];
        // 检查是否不必修改权限
        const isMatch = targetAuthoritySet.length === authoritySet.length
            && targetAuthoritySet.every(item => authoritySet.includes(item));
        if (isMatch) {
            message.error("修改的权限与原权限相同");
            return false;
        }
        message.loading("修改账户权限中");
        try {
            await modifyNonAdminAuthority({
                accountId: currentUser?.accountId,
                newAuthoritySet: targetAuthoritySet
            });
            // 刷新用户信息
            await flushUserInfo();
            message.destroy();
            message.success("修改账户权限成功");
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || "修改账户权限失败，请稍后再试");
            return false;
        }
    };

    /**
     * 获取密钥值显示内容
     */
    function getSecretKeyDisplayContent(): string {
        // 有密钥对
        if (currentUser?.secretId) {
            // 现在需要显示
            if (secretKey) {
                return secretKey;
            }
            // 现在不需要显示
            return "*".repeat(currentUser.secretId.length);
        }
        // 没有密钥对
        return "你还没有 secretKey，可点击新建密钥创建";
    }

    return (
        <ProCard>

            <ModalForm<{
                name: string;
                company: string;
            }>
                // 权限模态框
                onFinish={onAuthorityModalFinish}
                width={500}
                open={authorityModalOpen}
                onOpenChange={setAuthorityModalOpen}
                title={locale.formatMessage({
                    id: 'Sign up for an account',
                    defaultMessage: '修改权限',
                })}
                autoFocusFirstInput
                modalProps={{
                    destroyOnClose: true,
                }}
                submitTimeout={2000}
            >

                <Tabs
                    activeKey={authorityTabKey}
                    onChange={setAuthorityTabKey}
                    centered
                    items={[
                        {
                            key: "CHECK",
                            label: locale.formatMessage({
                                id: 'Select Authorities',
                                defaultMessage: '选择权限',
                            }),
                        },
                    ]}
                />

                {authorityTabKey === "CHECK" &&
                    <>
                        <Typography.Text>
                            权限是指
                            <Typography.Text strong>
                                平台使用者在平台管理系统中所具备的访问页面和操作的权限
                            </Typography.Text>
                            ，而非使用平台开放 API 接口的权限。所有用户都可以使用平台开放的 API 接口。
                            <Typography.Text strong>
                                一个平台使用者可以同时拥有多个权限，此时会使用其中的最高权限
                            </Typography.Text>
                            。暂不支持自主选择管理员权限。
                        </Typography.Text>
                        <br/>
                        <br/>
                        <Checkbox value={AuthorityEnum.ROLE_USER} checked={authorityChecked.ROLE_USER}
                                  onChange={handleAuthorityCheckboxChange(AuthorityEnum.ROLE_USER)}>
                            <Typography.Text strong><Typography.Text strong>用户</Typography.Text></Typography.Text>
                        </Checkbox>
                        <Typography.Text type={"secondary"}
                                         strong>能访问个人页面，并可进行用户可自主决定的操作。</Typography.Text>
                        <br/>
                        <br/>
                        <Checkbox value={AuthorityEnum.ROLE_TEST} checked={authorityChecked.ROLE_TEST}
                                  onChange={handleAuthorityCheckboxChange(AuthorityEnum.ROLE_TEST)}>
                            <Typography.Text strong>测试</Typography.Text>
                        </Checkbox>
                        <Typography.Text
                            type={"secondary"}
                            strong>能看到所有的页面和可操作内容，但不能进行部分管理操作。</Typography.Text>
                        <br/>
                        <br/>
                        <Checkbox value={AuthorityEnum.ROLE_ADMIN} disabled checked={authorityChecked.ROLE_ADMIN}
                                  onChange={handleAuthorityCheckboxChange(AuthorityEnum.ROLE_ADMIN)}>
                            <Typography.Text strong>管理</Typography.Text>
                        </Checkbox>
                        <Typography.Text
                            type={"secondary"} strong>平台最高权限，能查看所有页面，可进行所有操作。</Typography.Text>
                        <br/>
                        <br/>
                    </>

                }
            </ModalForm>

            <ModalForm<{
                name: string;
                company: string;
            }>
                // 验证码模态框
                onFinish={async (formData) => {
                    // 根据点击按钮的类型选择对应的函数
                    switch (verificationFinish) {
                        case OnFinishTypeEnum.VIEW_SECRET_KEY:
                            return viewSecretKeyOnCaptchaFinish(formData);
                        case OnFinishTypeEnum.GET_NEW_API_KEY:
                            return getNewApiKeyOnFinish(formData);
                        case OnFinishTypeEnum.MODIFY_API_KEY_STATUS:
                            return modifyApiKeyStatusOnFinish(formData);
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
                <br/>
                <br/>
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
                        // 检查用户是否绑定信息
                        const isUsingPhone = determineAuthStrategy();
                        if (isUsingPhone === null) {
                            throw new Error();
                        }
                        try {
                            await getVerificationCode({
                                phoneNumber: isUsingPhone ? currentUser?.phoneNumber : undefined,
                                emailAddress: !isUsingPhone ? currentUser?.emailAddress : undefined,
                                strategy: authStrategy
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

            <ProForm.Group title="API 密钥"
                           tooltip="API 密钥是一种用于身份验证和授权的机密字符串，允许应用程序或服务访问特定的 API。"/>
            <ProForm.Group>

                <ProFormText tooltip="用于标识 API 调用者身份，可以简单类比为用户名。"
                             name="secretId"
                             label="secretId">
                    <Typography.Text
                        copyable={!!currentUser?.secretId}
                        strong={!!currentUser?.secretId}
                        italic={!currentUser?.secretId}>
                        {currentUser?.secretId || "你还没有 secretId，可点击新建密钥创建"}
                    </Typography.Text>
                </ProFormText>

            </ProForm.Group>

            <ProForm.Group>

                <ProFormText tooltip="用于验证 API 调用者的身份，可以简单类比为密码。"
                             name="secretKey"
                             label="secretKey">

                    <Typography.Text copyable={!!secretKey} strong={!!secretKey}
                                     italic={!secretKey}>
                        {getSecretKeyDisplayContent()}
                    </Typography.Text>

                    {currentUser?.secretId && !secretKey && (
                        // 有密钥对，并且没有显示 secretKey 时
                        <Button type="link" onClick={() => {
                            setTipMessage(accessKeyTipMessage);
                            setVerificationFinish(OnFinishTypeEnum.VIEW_SECRET_KEY);
                            handleCaptchaModalOpen(true);
                        }}>
                            查看
                        </Button>
                    )}

                </ProFormText>

            </ProForm.Group>

            <ProForm.Group>

                <Space align={"center"} size={"large"}>

                    <Button size={"middle"} type="default"
                            onClick={() => {
                                setTipMessage(newKeyTipMessage);
                                setVerificationFinish(OnFinishTypeEnum.GET_NEW_API_KEY);
                                handleCaptchaModalOpen(true);
                            }}>
                        新建密钥
                    </Button>

                    <Button size={"middle"} type="default"
                            onClick={() => {
                                // 确定要修改的状态
                                const originalAccountStatus = currentUser?.accountStatus;
                                let targetStatus = undefined;
                                switch (originalAccountStatus) {
                                    case ApiKeyStatusEnum.NORMAL_KEY_AVAILABLE:
                                        targetStatus = ApiKeyStatusEnum.NORMAL_KEY_UNAVAILABLE;
                                        break;
                                    case ApiKeyStatusEnum.NORMAL_KEY_UNAVAILABLE:
                                        targetStatus = ApiKeyStatusEnum.NORMAL_KEY_AVAILABLE;
                                        break;
                                }
                                setTargetApiKeyStatus(targetStatus);
                                setTipMessage(enableOrDisableTipMessage);
                                handleCaptchaModalOpen(true);
                                setVerificationFinish(OnFinishTypeEnum.MODIFY_API_KEY_STATUS)
                            }}>
                        {currentUser?.accountStatus === ApiKeyStatusEnum.NORMAL_KEY_UNAVAILABLE ? "启用" : "禁用"}
                    </Button>

                    <Typography.Text>
                        状态：
                        <Typography.Text strong>
                            {currentUser?.accountStatus === ApiKeyStatusEnum.NORMAL_KEY_UNAVAILABLE ? "已禁用" : "已启用"}
                        </Typography.Text>
                    </Typography.Text>

                </Space>

            </ProForm.Group>

            <br/>
            {}
            <ProForm.Group title={"账户权限"} tooltip={"指用户账户在后台管理系统中具备的操作权限。"}/>

            <ProForm.Group align={"start"} size={"large"}>

                <ProFormText tooltip="包括用户、测试、管理员。普通用户仅支持同时拥有用户和测试两种权限。"
                             label="权限">
                    <Typography.Text strong>
                        {currentUser?.authoritySet?.map((authority: string | number) => (AuthorityDescriptionEnum as any)[authority]).join(', ')}
                    </Typography.Text>
                </ProFormText>

                <Button size={"middle"} type="default"
                        onClick={() => {
                            setAuthorityModalOpen(true);
                        }}>
                    修改权限
                </Button>

            </ProForm.Group>

        </ProCard>
    );
};

export default AccountAuthorizationCard;
