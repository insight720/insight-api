import {PageContainer} from '@ant-design/pro-components';
import React, {useState} from "react";
import AccountAuthorizationCard from "@/pages/User/Account/components/AccountAuthorizationCard";
import {useModel} from "@@/exports";
import AuthenticationCard from "@/pages/User/Account/components/AuthenticationCard";

/**
 * 用户账户
 */
const UserAccount: React.FC = () => {
    // tab 标签选择状态
    const [tabType, setTabType] = useState('authorization');

    // 全局初始状态
    const {initialState} = useModel('@@initialState');

    // 设置全局初始状态
    const {setInitialState} = useModel('@@initialState');

    // 获取登录用户信息
    const {fetchUserInfo} = initialState || {};

    // 登陆用户信息
    const {currentUser} = initialState || {};

    return (
        <div
            style={{
                background: '#F5F7FA',
            }}
        >
            <PageContainer
                header={{
                    title: '账户设置',
                    ghost: true,
                    breadcrumb: {
                        items: [
                            {title: '用户页',},
                            {title: '账户设置',},
                        ],
                    },
                }}
                tabList={[
                    {
                        tab: '授权设置',
                        key: 'authorization',
                        closable: false,
                    },
                    {
                        tab: '认证设置',
                        key: 'authentication',
                        closable: false,
                    },
                ]}
                tabProps={{
                    type: 'editable-card',
                    hideAdd: true,
                }}
                // 根据 tabType 切换页面
                onTabChange={(key) => setTabType(key)}
            >
                {
                    tabType == 'authorization'
                    && <AccountAuthorizationCard currentUser={currentUser}
                                                 setInitialState={setInitialState}
                                                 fetchUserInfo={fetchUserInfo}/>
                }
                {
                    tabType == 'authentication'
                    && <AuthenticationCard currentUser={currentUser}/>
                }
            </PageContainer>

        </div>);
};

export default UserAccount;
