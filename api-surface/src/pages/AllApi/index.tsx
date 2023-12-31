import {PageContainer} from '@ant-design/pro-components';
import React, {useState} from "react";
import {useModel} from "@@/exports";
import ApiDigestCard from "./components/ApiDigestCard";
import ApiViewCard from "./components/ApiViewCard";
import ApiQuantityUsageViewCard from "@/pages/AllApi/components/ApiQuantityUsageViewCard";
import ApiCreatorViewCard from "@/pages/AllApi/components/ApiCreatorViewCard";
import ApiTestCard from "@/pages/AllApi/components/ApiTestCard";
import ApiOrderResultCard from "@/pages/AllApi/components/ApiOrderResultCard";


/**
 * 所有接口
 */
const AllApi: React.FC = () => {
    // 全局初始状态
    const {initialState} = useModel('@@initialState');

    // 设置全局初始状态
    const {setInitialState} = useModel('@@initialState');

    // 获取登录用户信息
    const {fetchUserInfo} = initialState || {};

    // 登陆用户信息
    const {currentUser} = initialState || {};

    // 订单结果状态
    const [orderResultStatus, setOrderResultStatus]
        = useState<string>();

    // tab 项
    type TabItem = {
        tab: string;
        key: string;
        closable: boolean;
    };

    // 当前查看的 API 摘要
    const [apiDigestVO, setApiDigestVO]
        = useState<API.ApiDigestVO>();

    // tab 项的映射
    const tabItems: { [key: string]: TabItem } = {
        "digest": {
            tab: "接口摘要",
            key: "digest",
            closable: false,
        },
        "format": {
            tab: "查看接口",
            key: "format",
            closable: true,
        },
        "quantityUsage": {
            tab: "查看用法",
            key: "quantityUsage",
            closable: true,
        },
        "creator": {
            tab: "查看创建者",
            key: "creator",
            closable: true,
        },
        "test": {
            tab: "测试调用",
            key: "test",
            closable: true,
        },
        "result": {
            tab: "下单结果",
            key: "result",
            closable: true,
        }
    };

    type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

    // tab 标签显示项
    const [tabItem, setTabItem]
        = useState<TabItem>(tabItems['digest'] || null);

    // 所有 tab 标签项
    const [items, setItems]
        = useState<TabItem[]>([tabItem]);

    // 添加标签
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

    // 删除标签
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
                    title: '所有接口',
                    ghost: true,
                    breadcrumb: {
                        items: [
                            {title: '公共页'},
                            {title: '所有接口'}
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
                    tabItem.key === 'digest' &&
                    <ApiDigestCard currentUser={currentUser}
                                   fetchUserInfo={fetchUserInfo}
                                   setInitialState={setInitialState}
                                   apiDigestVO={apiDigestVO}
                                   add={add}
                                   setApiDigestVO={setApiDigestVO}
                                   setOrderResultStatus={setOrderResultStatus}
                    />
                }
                {
                    tabItem.key === 'format' &&
                    <ApiViewCard
                        currentUser={currentUser}
                        fetchUserInfo={fetchUserInfo}
                        setInitialState={setInitialState}
                        apiDigestVO={apiDigestVO}/>
                }
                {
                    tabItem.key === 'quantityUsage' &&
                    <ApiQuantityUsageViewCard
                        currentUser={currentUser}
                        fetchUserInfo={fetchUserInfo}
                        setInitialState={setInitialState}
                        apiDigestVO={apiDigestVO}/>
                }
                {
                    tabItem.key === 'creator' &&
                    <ApiCreatorViewCard
                        currentUser={currentUser}
                        fetchUserInfo={fetchUserInfo}
                        setInitialState={setInitialState}
                        apiDigestVO={apiDigestVO}/>
                }
                {
                    tabItem.key === 'test' &&
                    <ApiTestCard
                        currentUser={currentUser}
                        fetchUserInfo={fetchUserInfo}
                        setInitialState={setInitialState}
                        apiDigestVO={apiDigestVO}/>
                }
                {
                    tabItem.key === 'result' &&
                    <ApiOrderResultCard
                        orderResultStatus={orderResultStatus}
                        remove={remove}
                    />
                }
            </PageContainer>
        </div>
    );

};

export default AllApi;
