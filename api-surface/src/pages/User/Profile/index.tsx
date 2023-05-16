import {PageContainer} from '@ant-design/pro-components';
import React, {useState} from "react";
import ProfileSettingCard from "@/pages/User/Profile/components/ProfileSettingCard";
import {useModel} from "@@/exports";
import ProfileDescriptionCard from "@/pages/User/Profile/components/ProfileDescriptionCard";

/**
 * 用户资料
 */
const UserProfile: React.FC = () => {
    // 全局初始状态
    const {initialState} = useModel('@@initialState');

    // 设置全局初始状态
    const {setInitialState} = useModel('@@initialState');

    // 获取登录用户信息
    const {fetchUserInfo} = initialState || {};

    // 登陆用户信息
    const {currentUser} = initialState || {};

    // Tab 标签选择状态
    const [tabType, setTabType] = useState('description');

    return (
        <div
            style={{background: '#F5F7FA'}}
        >
            <PageContainer
                header={{
                    title: '用户资料',
                    ghost: true,
                    breadcrumb: {
                        items: [
                            {title: '用户页'},
                            {title: '用户资料'}
                        ],
                    },
                }}
                tabList={[
                    {
                        tab: '我的资料',
                        key: 'description',
                        closable: false,
                    },
                    {
                        tab: '设置资料',
                        key: 'setting',
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
                    tabType === 'setting' &&
                    <ProfileSettingCard currentUser={currentUser}
                                        fetchUserInfo={fetchUserInfo}
                                        setInitialState={setInitialState}/>
                }
                {
                    tabType === 'description' &&
                    <ProfileDescriptionCard currentUser={currentUser}/>
                }
            </PageContainer>

        </div>
    );

};

export default UserProfile;
