import {ModalForm, ProFormCaptcha, ProFormText,} from '@ant-design/pro-components';
import React, {useState} from "react";
import {Alert, DrawerProps, Form, message, Tabs, Typography} from "antd";
import {FormattedMessage, useIntl} from "@@/exports";
import {LockOutlined} from "@ant-design/icons";
import {CheckboxValueType} from "antd/es/checkbox/Group";

/**
 * 验证码模态框属性
 */
export type CaptchaModalProps = {
    // 开启标志
    open?: DrawerProps['open'];

    // 开启函数
    onOpenChange?: (visible: boolean) => void;

    // 提示消息
    tipMessage?: string
};

const CaptchaMessage: React.FC<{
    content: string;
}> = ({content}) => {
    return (
        <Alert
            style={{
                marginBottom: 24,
            }}
            message={content}
            type="error"
            showIcon
        />
    );
};

/**
 * 验证码模态框
 */
const CaptchaModal: React.FC<CaptchaModalProps> = (props: CaptchaModalProps) => {
    // 国际化
    const locale = useIntl();

    // 表单数据
    const [form] = Form.useForm<{ name: string; company: string }>();

    // 身份验证信息类型
    const [authMsgType, setAuthMsgType] = useState<string>('account');

    // 提示消息
    const {tipMessage} = props;

    const options = [
        {label: 'Apple', value: 'Apple'},
        {label: 'Pear', value: 'Pear'},
        {label: 'Orange', value: 'Orange'},
    ];

    const optionsWithDisabled = [
        {label: 'Apple', value: 'Apple'},
        {label: 'Pear', value: 'Pear'},
        {label: 'Orange', value: 'Orange', disabled: false},
    ];

    const onChange = (checkedValues: CheckboxValueType[]) => {
        console.log('checked = ', checkedValues);
    };

    const plainOptions = ['Apple', 'Pear', 'Orange'];

    /**
     * 注册表单提交
     */
    const onFinish = async (fields: any) => {
        const hide = message.loading('注册中');
        try {
            // await register({
            //     username: fields.username,
            //     password: fields.password,
            //     confirmedPassword: fields.confirmedPassword
            // });
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
                onCancel: () => console.log('run'),
            }}
            submitTimeout={2000}
            onFinish={onFinish}
        >

            <Tabs
                activeKey={authMsgType}
                onChange={setAuthMsgType}
                centered
                items={[
                    {
                        key: 'email',
                        label: locale.formatMessage({
                            id: 'pages.login.emailLogin.tab',
                            defaultMessage: '邮箱号验证',
                        }),
                    },
                    {
                        key: 'mobile',
                        label: locale.formatMessage({
                            id: 'dadadada',
                            defaultMessage: '手机号验证',
                        }),
                    },
                ]}
            />
            <ProFormText>
                <Typography.Text strong>
                    {tipMessage}
                </Typography.Text>
            </ProFormText>

            <ProFormCaptcha
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
                name="captcha"
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
