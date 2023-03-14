import {PageContainer} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import {List, message} from 'antd';
import {listApiInfoByPage} from "@/services/api-facade/apiInfoController";


/**
 * 主页
 * @constructor
 */
const ApiInfoList: React.FC = () => {
    const [loading, setLoading] = useState(false);
    const [list, setList] = useState<API.ApiInfo[]>([]);
    const [total, setTotal] = useState<number>(0);
    const defaultPageSize = 5;

    const loadData = async (current = 1, pageSize: number = defaultPageSize) => {
        setLoading(true);
        try {
            const res = await listApiInfoByPage(
                {
                    apiInfoQueryRequest: {
                        current: current,
                        pageSize: pageSize,
                    },
                }
            );
            console.log(res);
            setList(res?.data?.records ?? []);
            setTotal(res?.data?.total ?? 0);
        } catch (error: any) {
            message.error('请求失败，' + error.message);
        }
        setLoading(false);
    };

    useEffect(() => {
        loadData();
    }, []);

    return (
        <PageContainer title="在线接口开放平台">
            <List
                className="my-list"
                loading={loading}
                itemLayout="horizontal"
                dataSource={list}
                renderItem={(item) => {
                    const apiLink = `/user/api_info/${item.id}`;
                    return (
                        <List.Item actions={[<a key={item.id} href={apiLink}>查看</a>]}>
                            <List.Item.Meta
                                title={<a href={apiLink}>{item.name}</a>}
                                description={item.description}
                            />
                        </List.Item>
                    );
                }}
                pagination={{
                    showTotal(total: number) {
                        return '总数：' + total;
                    },
                    pageSize: defaultPageSize,
                    total,
                    onChange(page, pageSize) {
                        loadData(page, pageSize);
                    },
                }}
            />
        </PageContainer>
    );
};

export default ApiInfoList;
