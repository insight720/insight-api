import {ProCard, ProForm, ProFormText,} from '@ant-design/pro-components';
import React, {useState} from "react";
import {Button, message, Space, Typography} from "antd";
import {
    getNewApiKeyPair,
    modifyAccountAuthority,
    modifyAccountStatus
} from "@/services/api-security/userAccountController";
import {history} from "@@/core/history";
import {flushSync} from "react-dom";
import CaptchaModal from "@/pages/User/Account/components/CaptchaModel";

/**
 * 用户账户授权卡属性
 */
export type AccountAuthorizationCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
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

    // 新建密钥对的提示消息
    const newKeyPairTpiMessage: React.ReactNode = (
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

    // 启用或禁用密钥对的提示消息
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
            并至少绑定一个邮箱或手机号。感谢您的支持！"
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
     * 获取新密钥对
     */
    const getNewKeyPair = async () => {
        message.loading('获取中');
        try {
            let result = undefined;
            if (currentUser?.accountId) {
                result = await getNewApiKeyPair({
                    accountId: currentUser?.accountId,
                });
            } else {
                message.destroy();
                message.error('用户未登录');
            }
            // 将获取到密钥对用于显示
            // 可能需要更新用户信息
            await flushUserInfo();
            message.destroy();
            message.success('获取新密钥对成功');
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || '获取新密钥对失败，请稍后再试！');
            return false;
        }
    };

    /**
     * 启用或禁用密钥对
     */
    const enableOrDisableKeyPair = async (statusCode: number) => {
        const hide = message.loading('修改中');
        try {
            let result = undefined;
            if (currentUser?.accountId) {
                result = await modifyAccountStatus({
                    accountId: currentUser?.accountId,
                    statusCode: statusCode
                });
            } else {
                message.error('修改密钥状态失败，请稍后再试！');
                return false;
            }
            hide();
            // 重新获取用户信息，刷新页面，显示资料
            if (result && result.data && currentUser) {
                currentUser.secretId = result.data.accountKey;
            }
            history.push(window.location.pathname);
            message.success('修改密钥状态成功');
            return true;
        } catch (error: any) {
            hide();
            message.error(error.message || '修改密钥状态失败，请稍后再试！');
            return false;
        }
    };

    /**
     * 设置账户权限
     */
    const setAccountAuthority = async () => {
        const hide = message.loading('修改中');
        try {
            if (currentUser?.accountId) {
                await modifyAccountAuthority({
                    accountId: currentUser?.accountId || '',
                    authority: 'ROLE_ADMIN'
                })
            } else {
                message.error('设置账户权限失败，请稍后再试！');
                return false;
            }
            hide();
            history.push(window.location.pathname);
            message.success('设置账户权限成功');
            return true;
        } catch (error: any) {
            hide();
            message.error(error.message || '设置账户权限失败，请稍后再试！');
            return false;
        }

    }

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

            <CaptchaModal open={captchaModalOpen}
                          onOpenChange={handleCaptchaModalOpen}
                          setSecretKey={setSecretKey}
                          tipMessage={tipMessage}/>

            <ProForm.Group title="API 密钥"
                           tooltip="API 密钥是一种用于身份验证和授权的机密字符串，允许应用程序或服务访问特定的 API。"/>
            <ProForm.Group>

                <ProFormText tooltip="用于标识 API 调用者身份，可以简单类比为用户名。"
                             name="secretId"
                             label="secretId">
                    <Typography.Text
                        copyable={!!currentUser?.secretId}
                        strong={!!currentUser?.secretId}
                        italic={!currentUser?.secretId}
                    >
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
                            handleCaptchaModalOpen(true);
                        }}>
                            查看
                        </Button>
                    )}

                </ProFormText>

            </ProForm.Group>

            <ProForm.Group>
                <Space align={"center"} size={"large"}>

                    <Button size={"middle"} type="default" onClick={() => {
                        setTipMessage(newKeyPairTpiMessage);
                        handleCaptchaModalOpen(true);
                    }}>新建密钥</Button>

                    <Button size={"middle"} type="default" onClick={() => {
                        setTipMessage(enableOrDisableTipMessage);
                        handleCaptchaModalOpen(true);
                    }}>启用</Button>

                    <Typography.Text>
                        状态：<Typography.Text strong>启用</Typography.Text>
                    </Typography.Text>
                </Space>
            </ProForm.Group>
            <br/>
            <ProForm.Group title={"账户权限"} tooltip={"最长为 24 位"}/>
            <ProForm.Group align={"start"} size={"large"}>
                <ProFormText tooltip="最长为 24 位" label="权限">
                    <Typography.Text strong>用户</Typography.Text>
                </ProFormText>
                <Button size={"middle"} type="default" onClick={() => {
                    setAccountAuthority()
                }}>修改权限</Button>
            </ProForm.Group>
        </ProCard>);
};

export default AccountAuthorizationCard;
