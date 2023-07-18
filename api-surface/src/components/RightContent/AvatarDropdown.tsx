import {LogoutOutlined, SettingOutlined, UserOutlined} from '@ant-design/icons';
import {useEmotionCss} from '@ant-design/use-emotion-css';
import {history, useModel} from '@umijs/max';
import {message, Spin} from 'antd';
import type {MenuInfo} from 'rc-menu/lib/interface';
import React, {useCallback} from 'react';
import {flushSync} from 'react-dom';
import HeaderDropdown from '../HeaderDropdown';
import {logout} from "@/services/hidden/springSecurity";
import {getCsrfToken} from "@/services/api-security/securityController";

export type GlobalHeaderRightProps = {
    menu?: boolean;
    children?: React.ReactNode;
};

export const AvatarName = () => {
    const {initialState} = useModel('@@initialState');
    const {currentUser} = initialState || {};
    return <span className="anticon">{currentUser?.username}</span>;
};

export const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({children}) => {

    const {initialState, setInitialState} = useModel('@@initialState');

    /**
     * 退出登录
     */
    const loginOut = async () => {
        try {
            await logout()
            // 注销后 CSRF Cookie 会被清除
            await getCsrfToken();
            flushSync(() => {
                setInitialState((s) => ({...s, currentUser: undefined}));
            });
        } catch (error: any) {
            message.error(error.message || "注销失败，请重试！");
            return;
        }
        history.push('/login');
        message.success("注销成功");
    };

    const actionClassName = useEmotionCss(({token}) => {
        return {
            display: 'flex',
            height: '48px',
            marginLeft: 'auto',
            overflow: 'hidden',
            alignItems: 'center',
            padding: '0 8px',
            cursor: 'pointer',
            borderRadius: token.borderRadius,
            '&:hover': {
                backgroundColor: token.colorBgTextHover,
            },
        };
    });

    const onMenuClick = useCallback(
        (event: MenuInfo) => {
            const {key} = event;
            // 点击退出登录
            if (key === 'logout') {
                loginOut();
                return;
            }
            // 点击其他选项
            history.push(`/user/${key}`);
        },
        [setInitialState],
    );

    const loading = (
        <span className={actionClassName}>
      <Spin
          size="small"
          style={{
              marginLeft: 8,
              marginRight: 8,
          }}
      />
    </span>
    );

    if (!initialState) {
        return loading;
    }

    const {currentUser} = initialState;

    // 如果用户未登录或没有账户名，则显示加载中状态
    if (!currentUser || !currentUser.username) {
        return loading;
    }

    // 头像下拉菜单项
    const menuItems = [
        {
            key: 'profile',
            icon: <UserOutlined/>,
            label: '用户资料',
        },
        {
            key: 'account',
            icon: <SettingOutlined/>,
            label: '账户设置',
        },
        {
            key: 'logout',
            icon: <LogoutOutlined/>,
            label: '退出登录',
        },
        {
            type: 'divider' as const,
        },
    ];

    return (
        <HeaderDropdown
            menu={{
                selectedKeys: [],
                onClick: onMenuClick,
                items: menuItems,
            }}
        >
            {children}
        </HeaderDropdown>
    );
};
