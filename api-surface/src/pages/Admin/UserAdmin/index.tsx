import {PageContainer} from '@ant-design/pro-components';
import React, {useState} from "react";
import {useModel} from "@@/exports";
import UserAdminCard from "./components/UserAdminCard";
import ApiFormatAndUsageCard from "./components/ApiFormatAndUsageCard";


/**
 * 用户管理
 */
const UserAdmin: React.FC = () => {
    // 全局初始状态
    const {initialState} = useModel('@@initialState');

    // 设置全局初始状态
    const {setInitialState} = useModel('@@initialState');

    // 获取登录用户信息
    const {fetchUserInfo} = initialState || {};

    // 登陆用户信息
    const {currentUser} = initialState || {};

    type TabItem = {
        tab: string;
        key: string;
        closable: boolean;
    };

    const tabItems: { [key: string]: TabItem } = {
        "user": {
            tab: "所有用户",
            key: "user",
            closable: false,
        },
    };

    // 当前查看的用户
    const [userAdminVO, setUserAdminVO]
        = useState<API.UserAdminVO>();

    // tab 标签键
    type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

    // tab 标签显示项
    const [tabItem, setTabItem]
        = useState<TabItem>(tabItems['user'] || null);

    // 所有 tab 标签项
    const [items, setItems]
        = useState<TabItem[]>([tabItem]);

    const add = (tabType: TargetKey) => {
        const newPanes: TabItem[] = [...items];
        const newActiveKey = tabItems[tabType as string];
        // 如果 items 中已存在当前类型的选项卡，则先关闭原有选项卡
        const existingTabItemIndex = newPanes.findIndex(
            (item) => item.key === newActiveKey.key
        );
        if (existingTabItemIndex !== -1) {
            // 移除原有选项卡
            newPanes.splice(existingTabItemIndex, 1);
        }
        newPanes.push(newActiveKey);
        setItems(newPanes);
        setTabItem(newActiveKey);
    };

    // tab 标签改变
    const onTabChange = (newTabType: string) => {
        setTabItem(tabItems[newTabType]);
    };

    const remove = (targetKey: TargetKey) => {
        let newActiveKey = tabItem;
        let lastIndex = -1;
        items.forEach((item, i) => {
            if (item.key === targetKey) {
                lastIndex = i - 1;
            }
        });
        const newPanes = items.filter((item) => item.key !== targetKey);
        if (newPanes.length && newActiveKey.key === targetKey) {
            if (lastIndex >= 0) {
                newActiveKey = newPanes[lastIndex];
            } else {
                newActiveKey = newPanes[0];
            }
        }
        setItems(newPanes);
        setTabItem(newActiveKey);
    };

    const onEdit = (
        targetKey: React.MouseEvent | React.KeyboardEvent | string,
        action: 'add' | 'remove',
    ) => {
        if (action === 'add') {
            add(targetKey);
        } else {
            remove(targetKey);
        }
    };

    return (
        <div
            style={{background: '#F5F7FA'}}
        >
            <PageContainer
                header={{
                    title: '所有用户',
                    ghost: true,
                    breadcrumb: {
                        items: [
                            {title: '公共页'},
                            {title: '所有用户'}
                        ],
                    },
                }}
                tabList={items}
                tabProps={{
                    activeKey: tabItem.key,
                    defaultActiveKey: "user",
                    onChange: onTabChange,
                    onEdit: onEdit,
                    type: 'editable-card',
                    hideAdd: true,
                }}
            >
                {
                    tabItem.key === 'user' &&
                    <UserAdminCard currentUser={currentUser}
                                   fetchUserInfo={fetchUserInfo}
                                   setInitialState={setInitialState}
                                   setUserAdminVO={setUserAdminVO}
                                   add={add}
                    />
                }
                {
                    tabItem.key === 'format' &&
                    <ApiFormatAndUsageCard
                        currentUser={currentUser}
                        fetchUserInfo={fetchUserInfo}
                        setInitialState={setInitialState}
                        userApiDigestVO={userAdminVO}/>
                }
            </PageContainer>

        </div>
    );

};

export default UserAdmin;
