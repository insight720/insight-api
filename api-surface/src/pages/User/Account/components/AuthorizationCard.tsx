import {ProCard, ProForm, ProFormText,} from '@ant-design/pro-components';
import React from "react";
import {Button, message, Space, Typography} from "antd";
import {
    generateKeyPair,
    modifyAccountAuthority,
    modifyAccountStatus
} from "@/services/api-security/userAccountController";
import {history} from "@@/core/history";

export type AuthorizationCardProps = {
    currentUser?: API.LoginUserDTO;
};

/**
 * 账户授权设置
 */
const AuthorizationCard: React.FC<AuthorizationCardProps> = (props: AuthorizationCardProps) => {
    // 当前登录用户
    const {currentUser} = props;

    /**
     * 获取新密钥对
     */
    const getNewKeyPair = async () => {
        const hide = message.loading('获取中');
        try {
            let result = undefined;
            if (currentUser?.accountId) {
                result = await generateKeyPair({
                    accountId: currentUser.accountId,
                });
            } else {
                message.error('获取新密钥对失败，请稍后再试！');
                return false;
            }
            hide();
            // 重新获取用户信息，刷新页面，显示资料
            if (result && result.data && currentUser) {
                currentUser.accountKey = result.data.accountKey;
            }
            history.push(window.location.pathname);
            message.success('获取新密钥对成功');
            return true;
        } catch (error: any) {
            hide();
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
                currentUser.accountKey = result.data.accountKey;
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

    return (
        <ProCard>
            <ProForm.Group title={"API 密钥"} tooltip={"最长为 24 位"}/>
            <ProForm.Group>
                <ProFormText colon={true} tooltip="最长为 24 位" name='AccountKey' label="AccountKey">
                    <Typography.Text copyable strong>{currentUser?.accountKey || 'not found'}</Typography.Text>
                </ProFormText>
            </ProForm.Group>
            <ProForm.Group>
                <ProFormText tooltip="最长为 24 位" name="AccessKey" label="AccessKey">
                    <Typography.Text copyable strong>{'not found'}</Typography.Text>
                </ProFormText>
            </ProForm.Group>
            <ProForm.Group>
                <Space align={"center"} size={"large"}>
                    <Button size={"middle"} type="default" onClick={getNewKeyPair}>新建密钥</Button>
                    <Button size={"middle"} type="default" onClick={() => {
                        enableOrDisableKeyPair(0);
                    }}>启用</Button>
                    <Button size={"middle"} type="default" onClick={() => {
                        enableOrDisableKeyPair(1);
                    }}>禁用</Button>
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

export default AuthorizationCard;
