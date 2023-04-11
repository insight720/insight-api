import {ProCard, ProForm, ProFormText,} from '@ant-design/pro-components';
import React from "react";
import {Button, Space} from "antd";

export type AuthenticationCardProps = {
    currentUser?: API.LoginUserDTO;
};

/**
 * 账户认证设置
 */
const AuthenticationCard: React.FC<AuthenticationCardProps> = (props: AuthenticationCardProps) => {
    // 当前登录用户
    const {currentUser} = props;
    return (
        <ProCard>
            <ProForm.Group title={"账户设置"} tooltip={"其他信息"}/>
            <Space align={"start"} size={"large"}>
                <ProFormText disabled={true} width="md" tooltip="最长为 24 位"
                             name={['contract', 'name']}
                             label="账户名" initialValue={"哈哈"}/>
                <Button size={"middle"} type="default" disabled={true}>修改账户名</Button>
                <Button size={"middle"} type="default">绑定账户名</Button>
            </Space>

            <ProFormText label={"其他设置"} tooltip={"其他信息"}>
                <Space align={"center"} size={"large"}>
                    <Button size={"middle"} type="default">修改密码</Button>
                    <Button size={"middle"} type="default">删除账户</Button>
                </Space>
            </ProFormText>

            <ProForm.Group title={"邮箱和手机"} tooltip={"最长为 24 位"}/>
            <ProForm.Group align={"start"} size={"large"}>
                <ProFormText disabled={true} width="md" tooltip="最长为 24 位"
                             name={['contract', 'name']}
                             label="邮箱"/>
                <Button size={"middle"} type="default" disabled={true}>修改邮箱</Button>
                <Button size={"middle"} type="default">绑定邮箱</Button>
            </ProForm.Group>
            <ProForm.Group align={"start"} size={"large"}>
                <ProFormText disabled={true} width="md" tooltip="最长为 24 位"
                             name={['contract', 'name']}
                             label="手机"/>
                <Button size={"middle"} type="default" disabled={true}>修改手机</Button>
                <Button size={"middle"} type="default">绑定手机</Button>
            </ProForm.Group>

            <ProForm.Group size={"large"} title={"第三方登录"} align={"baseline"}
                           tooltip={"其他信息"}>
                <ProFormText>QQ</ProFormText>
                <Space size={"large"} align={"center"}>
                    <Button size={"middle"} type="default" disabled={true}>修改</Button>
                    <Button size={"middle"} type="default">绑定</Button>
                </Space>
                <ProFormText>微信</ProFormText>
                <Space size={"large"} align={"center"}>
                    <Button type="default" disabled={true}>修改</Button>
                    <Button type="default">绑定</Button>
                </Space>
            </ProForm.Group>
        </ProCard>);
};

export default AuthenticationCard;
