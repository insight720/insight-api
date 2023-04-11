import {PageContainer} from '@ant-design/pro-components';
import React, {useState} from "react";
import AuthorizationCard from "@/pages/User/Account/components/AuthorizationCard";
import {useModel} from "@@/exports";
import AuthenticationCard from "@/pages/User/Account/components/AuthenticationCard";

const UserAccount: React.FC = () => {
    // 加载中图标状态
    const [loading, setLoading] = useState(false);
    // tab 标签选择状态
    const [tabType, setTabType] = useState('authorization');
    // 登录用户信息
    const {initialState} = useModel('@@initialState');
    const {currentUser} = initialState || {};
    return (
        <div
            style={{
                background: '#F5F7FA',
            }}
        >
            <PageContainer
                loading={loading}
                header={{
                    title: '账户设置',
                    ghost: true,
                    breadcrumb: {
                        items: [
                            {
                                title: '用户页',
                            },
                            {
                                title: '账户设置',
                            },
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
                // 改变 tab，根据 tabType 切换页面
                onTabChange={(key) => setTabType(key)}
            >

                {tabType == 'authorization' && <AuthorizationCard currentUser={currentUser}/>}
                {tabType == 'authentication' && <AuthenticationCard currentUser={currentUser}/>}
            </PageContainer>
        </div>);
}

export default UserAccount;
