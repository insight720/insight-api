import {PageContainer} from '@ant-design/pro-components';
import React, {useState} from "react";
import {useModel} from "@@/exports";
import QuantityUsageOrderCard from "./components/QuantityUsageOrderCard";
import ApiOrderResultCard from "@/pages/User/MyOrder/components/ApiOrderResultCard";


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
            tab: "计数用法订单",
            key: "order",
            closable: false,
        },
        "format": {
            tab: "订单操作结果",
            key: "format",
            closable: true,
        },
        "result": {
            tab: "订单操作结果",
            key: "result",
            closable: true,
        },
    };

    // tab 标签键
    type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

    // tab 标签显示项
    const [tabItem, setTabItem]
        = useState<TabItem>(tabItems['order'] || null);

    // 所有 tab 标签项
    const [items, setItems]
        = useState<TabItem[]>([tabItem]);

    // 订单结果状态
    const [orderResultStatus, setOrderResultStatus]
        = useState<string>();

    // 订单结果类型
    const [orderResultType, setOrderResultType]
        = useState<string>();

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
                    <QuantityUsageOrderCard currentUser={currentUser}
                                            fetchUserInfo={fetchUserInfo}
                                            setInitialState={setInitialState}
                                            setOrderResultStatus={setOrderResultStatus}
                                            setOrderResultType={setOrderResultType}
                                            add={add}
                    />
                }
                {
                    tabItem.key === 'result' &&
                    <ApiOrderResultCard
                        orderResultStatus={orderResultStatus}
                        orderResultType={orderResultType}
                        remove={remove}
                    />
                }
            </PageContainer>
        </div>
    );

};

export default MyOrder;
