import {PageContainer} from '@ant-design/pro-components';
import React, {useState} from "react";
import {useModel} from "@@/exports";
import UserOrderCard from "./components/UserOrderCard";
import ApiFormatAndUsageCard from "./components/ApiFormatAndUsageCard";


/**
 * 我的订单
 */
const MyOrder: React.FC = () => {
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
        "order": {
            tab: "用户订单",
            key: "order",
            closable: false,
        },
        "info": {
            tab: "订单信息",
            key: "info",
            closable: true,
        }
    };

    // 当前查看的用户订单
    const [userOrderVO, setUserOrderVO]
        = useState<API.UserOrderVO>();

    // tab 标签键
    type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

    // tab 标签显示项
    const [tabItem, setTabItem]
        = useState<TabItem>(tabItems['order'] || null);

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
                    title: '我的订单',
                    ghost: true,
                    breadcrumb: {
                        items: [
                            {title: '用户页'},
                            {title: '我的订单'}
                        ],
                    },
                }}
                tabList={items}
                tabProps={{
                    activeKey: tabItem.key,
                    defaultActiveKey: "digest",
                    onChange: onTabChange,
                    onEdit: onEdit,
                    type: 'editable-card',
                    hideAdd: true,
                }}
            >
                {
                    tabItem.key === 'order' &&
                    <UserOrderCard currentUser={currentUser}
                                   fetchUserInfo={fetchUserInfo}
                                   setInitialState={setInitialState}
                                   setUserApiDigestVO={setUserOrderVO}
                                   add={add}
                    />
                }
                {
                    tabItem.key === 'format' &&
                    <ApiFormatAndUsageCard
                        currentUser={currentUser}
                        fetchUserInfo={fetchUserInfo}
                        setInitialState={setInitialState}
                        userApiDigestVO={userOrderVO}/>
                }
            </PageContainer>
        </div>
    );

};

export default MyOrder;
