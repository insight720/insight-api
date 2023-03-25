import Footer from '@/components/Footer';
import {Question, SelectLang} from '@/components/RightContent';
import {LinkOutlined} from '@ant-design/icons';
import type {Settings as LayoutSettings} from '@ant-design/pro-components';
import {SettingDrawer} from '@ant-design/pro-components';
import type {RunTimeLayoutConfig} from '@umijs/max';
import {history, Link} from '@umijs/max';
import defaultSettings from '../config/defaultSettings';
import {requestConfig} from './requestConfig';
import React from 'react';
import {AvatarDropdown, AvatarName} from './components/RightContent/AvatarDropdown';
import {InitialState} from "@/typings";
import {message} from "antd";
import {autoGetCsrfToken} from "@/services/api-security/securityController";
import {fetchLoginUserInfo} from "@/services/api-security/userDetailsController";

const isDev = process.env.NODE_ENV === 'development';
const loginPath = '/login';

/**
 * 获取 CSRF 令牌
 */
const fetchCsrfToken = async () => {
    try {
        const result = await autoGetCsrfToken();
        return result.data;
    } catch (error: any) {
        message.error(error.message || '服务器内部错误，请重试！');
        history.push(loginPath);
    }
    return undefined;
};

/**
 * 获取用户信息
 */
const fetchUserInfo = async () => {
    try {
        const result = await fetchLoginUserInfo();
        return result.data;
    } catch (error: any) {
        message.error(error.message || '用户未登录');
        history.push(loginPath);
    }
    return undefined;
};

/**
 * 页面初始状态
 *
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 */
export async function getInitialState(): Promise<InitialState> {
    // 如果不是登录页面，执行
    const {location} = history;
    if (location.pathname !== loginPath) {
        const currentUser = await fetchUserInfo();
        return {
            currentUser,
            fetchUserInfo,
            settings: defaultSettings as Partial<LayoutSettings>,
        };
    }
    // 登陆页面获取 CsrfToken（无需获取数据，它会自动保存在 Cookie 中）
    await fetchCsrfToken();
    return {
        fetchUserInfo,
        settings: defaultSettings as Partial<LayoutSettings>,
    };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({initialState, setInitialState}) => {
    return {
        actionsRender: () => [<Question key="doc"/>, <SelectLang key="SelectLang"/>],
        avatarProps: {
            src: initialState?.currentUser?.avatar,
            title: <AvatarName/>,
            render: (_, avatarChildren) => {
                return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
            },
        },
        waterMarkProps: {
            content: initialState?.currentUser?.username,
        },
        footerRender: () => <Footer/>,
        onPageChange: () => {
            const {location} = history;
            // 如果没有登录，重定向到 login
            if (!initialState?.currentUser && location.pathname !== loginPath) {
                history.push(loginPath);
            }
        },
        layoutBgImgList: [
            {
                src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
                left: 85,
                bottom: 100,
                height: '303px',
            },
            {
                src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
                bottom: -68,
                right: -45,
                height: '303px',
            },
            {
                src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
                bottom: 0,
                left: 0,
                width: '331px',
            },
        ],
        links: isDev
            ? [
                <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
                    <LinkOutlined/>
                    <span>OpenAPI 文档</span>
                </Link>,
            ]
            : [],
        menuHeaderRender: undefined,
        // 自定义 403 页面
        // unAccessible: <div>unAccessible</div>,
        // 增加一个 loading 的状态
        childrenRender: (children) => {
            // if (initialState?.loading) return <PageLoading />;
            return (
                <>
                    {children}
                    <SettingDrawer
                        disableUrlParams
                        enableDarkTheme
                        settings={initialState?.settings}
                        onSettingChange={(settings) => {
                            setInitialState((preInitialState) => ({
                                ...preInitialState,
                                settings,
                            }));
                        }}
                    />
                </>
            );
        },
        ...initialState?.settings,
    };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = {
    ...requestConfig,
};
