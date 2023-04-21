import Footer from '@/components/Footer';
import {
    AlipayCircleOutlined,
    LockOutlined,
    MailOutlined,
    MobileOutlined,
    TaobaoCircleOutlined,
    UserOutlined,
    WeiboCircleOutlined,
} from '@ant-design/icons';
import {LoginForm, ProFormCaptcha, ProFormCheckbox, ProFormText,} from '@ant-design/pro-components';
import {useEmotionCss} from '@ant-design/use-emotion-css';
import {FormattedMessage, Helmet, SelectLang, useIntl, useModel} from '@umijs/max';
import {Button, message, Select, Tabs} from 'antd';
import Settings from '../../../config/defaultSettings';
import React, {useState} from 'react';
import {flushSync} from 'react-dom';
import RegistryModal from "@/pages/Login/components/RegistryModal";
import {login} from "@/services/hidden/springSecurity";
import {history} from "@@/core/history";
import {getCsrfToken, getVerificationCode} from "@/services/api-security/securityController";
import {loginByVerificationCode} from "@/services/api-security/userDetailsController";

const ActionIcons = () => {
    const langClassName = useEmotionCss(({token}) => {
        return {
            marginLeft: '8px',
            color: 'rgba(0, 0, 0, 0.2)',
            fontSize: '24px',
            verticalAlign: 'middle',
            cursor: 'pointer',
            transition: 'color 0.3s',
            '&:hover': {
                color: token.colorPrimaryActive,
            },
        };
    });

    return (
        <>
            <AlipayCircleOutlined key="AlipayCircleOutlined" className={langClassName}/>
            <TaobaoCircleOutlined key="TaobaoCircleOutlined" className={langClassName}/>
            <WeiboCircleOutlined key="WeiboCircleOutlined" className={langClassName}/>
        </>
    );
};

const Lang = () => {
    const langClassName = useEmotionCss(({token}) => {
        return {
            width: 42,
            height: 42,
            lineHeight: '42px',
            position: 'fixed',
            right: 16,
            borderRadius: token.borderRadius,
            ':hover': {
                backgroundColor: token.colorBgTextHover,
            },
        };
    });

    return (
        <div className={langClassName} data-lang>
            {SelectLang && <SelectLang/>}
        </div>
    );
};

const Login: React.FC = () => {
    // 登录类型
    const [loginType, setLoginType] = useState<string>('USERNAME');

    // 全局初始状态
    const {setInitialState} = useModel('@@initialState');

    // 注册模态框开关
    const [registryModalOpen, handleRegistryModalOpen] = useState<boolean>(false);

    // 手机号前缀选项（如 +86）
    const [phoneOption, setPhoneOption] = useState<string>("+86")


    const containerClassName = useEmotionCss(() => {
        return {
            display: 'flex',
            flexDirection: 'column',
            height: '100vh',
            overflow: 'auto',
            backgroundImage:
                "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
            backgroundSize: '100% 100%',
        };
    });

    const locale = useIntl();
    // 表单数据
    /**
     * 设置用户的登陆状态
     */
    const setLoginUserInfo = async (userInfo: API.LoginUserDTO | undefined) => {
        if (userInfo) {
            flushSync(() => {
                setInitialState((s) => ({
                    ...s,
                    currentUser: userInfo,
                }));
            });
        }
    };

    /**
     * 处理用户名登录
     */
    const handleUsernameLogin = async (values: Record<string, any>) => {
        try {
            // 登录
            const urlParams = new URL(window.location.href).searchParams;
            urlParams.append("username", values.username);
            urlParams.append("password", values.password);
            urlParams.append("remember-me", values.autoLogin);
            const result = await login(urlParams);
            if (result.data) {
                const defaultLoginSuccessMessage = locale.formatMessage({
                    id: 'pages.login.success',
                    defaultMessage: '登录成功',
                });
                await setLoginUserInfo(result.data);
                // 登录后 CSRF Cookie 会被清除
                await getCsrfToken();
                history.push('/');
                message.success(defaultLoginSuccessMessage);
                return;
            }
        } catch (error: any) {
            const defaultLoginFailureMessage = locale.formatMessage({
                id: 'pages.login.failure',
                defaultMessage: '登录失败，请重试！',
            });
            message.error(error.message || defaultLoginFailureMessage);
        }
    };

    /**
     * 处理手机或邮箱登录
     */
    const handlePhoneOrEmailLogin = async (values: API.PhoneOrEmailLoginDTO) => {
        try {
            // 通过验证码登录
            const result = await loginByVerificationCode(values);
            if (result.data) {
                const defaultLoginSuccessMessage = locale.formatMessage({
                    id: 'pages.login.success',
                    defaultMessage: '登录成功！',
                });
                await setLoginUserInfo(result.data);
                // 登录后 CSRF Cookie 会被清除
                await getCsrfToken();
                history.push('/');
                message.success(defaultLoginSuccessMessage);
                return;
            }
        } catch (error: any) {
            const defaultLoginFailureMessage = locale.formatMessage({
                id: 'pages.login.failure',
                defaultMessage: '登录失败，请重试！',
            });
            message.error(error.message || defaultLoginFailureMessage);
        }
    };

    // @ts-ignore
    return (
        <div className={containerClassName}>
            <Helmet>
                <title>
                    {locale.formatMessage({
                        id: 'menu.login',
                        defaultMessage: '登录页',
                    })}
                    - {Settings.title}
                </title>
            </Helmet>
            <Lang/>
            <div
                style={{
                    flex: '1',
                    padding: '32px 0',
                }}
            >
                <LoginForm
                    contentStyle={{
                        minWidth: 280,
                        maxWidth: '75vw',
                    }}
                    logo={<img alt="logo" src="/logo.svg"/>}
                    title="Insight API"
                    subTitle={locale.formatMessage({id: 'pages.layouts.userLayout.title'})}
                    initialValues={{
                        autoLogin: false,
                    }}
                    actions={[
                        <FormattedMessage
                            key="loginWith"
                            id="pages.login.loginWith"
                            defaultMessage="其他登录方式"
                        />,
                        <ActionIcons key="icons"/>,
                    ]}
                    onFinish={async (values) => {
                        // 根据登录类型进行登录
                        switch (loginType) {
                            case "USERNAME":
                                await handleUsernameLogin(values);
                                break;
                            case "PHONE":
                                await handlePhoneOrEmailLogin({
                                    rememberMe: values.autoLogin,
                                    strategy: "PHONE",
                                    phoneNumber: phoneOption + values.phone,
                                    ...values
                                })
                                break;
                            case "EMAIL":
                                await handlePhoneOrEmailLogin({
                                    rememberMe: values.autoLogin,
                                    strategy: "EMAIL",
                                    emailAddress: values.email,
                                    ...values
                                });
                                break;
                            default:
                                alert("登录类型错误！");
                        }
                    }}
                >
                    <Tabs
                        activeKey={loginType}
                        onChange={setLoginType}
                        centered
                        items={[
                            {
                                key: 'USERNAME',
                                label: locale.formatMessage({
                                    id: 'pages.login.accountLogin.tab',
                                    defaultMessage: '账户密码登录',
                                }),
                            },
                            {
                                key: 'PHONE',
                                label: locale.formatMessage({
                                    id: 'pages.login.phoneLogin.tab',
                                    defaultMessage: '手机号登录',
                                }),
                            },
                            {
                                key: 'EMAIL',
                                label: locale.formatMessage({
                                    id: 'pages.login.emailLogin.tab',
                                    defaultMessage: '邮箱号登录',
                                }),
                            },
                        ]}
                    />

                    {loginType === "USERNAME" && (
                        <>
                            <ProFormText
                                name="username"
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
                                fieldProps={{
                                    size: 'large',
                                    prefix: <LockOutlined/>,
                                }}
                                placeholder={locale.formatMessage({
                                    id: 'pages.login.password.placeholder',
                                    defaultMessage: '密码: ant.design',
                                })}
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
                        </>
                    )}

                    {loginType === "PHONE" && (
                        <>
                            <ProFormText
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
                                placeholder={locale.formatMessage({
                                    id: 'pages.login.phoneNumber.placeholder',
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
                                name="verificationCode"
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
                                phoneName="phone"
                                onGetCaptcha={async (phone) => {
                                    // 如果需要失败会 throw 一个错误出来，onGetCaptcha 会自动停止
                                    // throw new Error("获取验证码错误")
                                    await getVerificationCode({
                                        // phoneOption 是 +86 前缀
                                        phoneNumber: phoneOption + phone,
                                        emailAddress: undefined,
                                        strategy: loginType
                                    });
                                    message.success("获取验证码成功！");
                                }}
                            />
                        </>
                    )}

                    {loginType === "EMAIL" && (
                        <>
                            <ProFormText
                                fieldProps={{
                                    size: 'large',
                                    prefix: <MailOutlined/>,
                                }}
                                name="email"
                                placeholder={locale.formatMessage({
                                    id: 'pages.login.emailAddress.placeholder',
                                    defaultMessage: '请输入邮箱号！',
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
                                name="verificationCode"
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
                                phoneName="email"
                                onGetCaptcha={async (email) => {
                                    // 如果需要失败会 throw 一个错误出来，onGetCaptcha 会自动停止
                                    // throw new Error("获取验证码错误！")
                                    await getVerificationCode({
                                        phoneNumber: undefined,
                                        emailAddress: email,
                                        strategy: loginType
                                    });
                                    message.success('获取验证码成功！');
                                }}
                            />
                        </>
                    )}

                    <div
                        style={{
                            marginBottom: 24,
                        }}
                    >
                        <ProFormCheckbox noStyle name="autoLogin" fieldProps={{
                            onChange: e => {
                                if (e.target.checked) {
                                    message.warning("若登录成功，7 天内可自动登录！");
                                }
                            }
                        }}>
                            <FormattedMessage id="pages.login.rememberMe" defaultMessage="自动登录"/>
                        </ProFormCheckbox>

                        <Button type="link" size={'small'} style={{
                            float: 'right',
                        }} onClick={() => {
                            handleRegistryModalOpen(true);
                        }}>
                            <FormattedMessage id="pages.login" defaultMessage="注册账号"/>
                        </Button>
                        <RegistryModal open={registryModalOpen}
                                       onOpenChange={handleRegistryModalOpen}/>

                    </div>

                </LoginForm>
            </div>
            <Footer/>
        </div>
    );
};

export default Login;
