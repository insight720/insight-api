import {PageContainer,} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import ReactECharts from 'echarts-for-react';
import {listTopInvokeApiInfo} from "@/services/api-facade/analysisController";
import {message} from "antd";

const ApiAnalysis: React.FC = () => {

    const [data, setData] = useState<API.ApiInfoData[]>([]);
    const [loading, setLoading] = useState(false);

    const loadData = async () => {
        setLoading(true);
        try {
            const res = await listTopInvokeApiInfo();
            if (res.data) {
                setData(res.data);
            }
        } catch (error: any) {
            message.error('请求失败，' + error.message);
        }
        setLoading(false);
    };

    // 加载数据，[] 只加载一次
    useEffect(() => {
        loadData();
    }, []);

    // 映射：{ value: 1048, name: 'Search Engine' },
    const chartData = data.map(item => {
        return {
            value: item.totalNum,
            name: item.name,
        }
    });

    // 饼图
    const option = {
        title: {
            text: 'Referer of a Website',
            subtext: 'Fake Data',
            left: 'center'
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: 'left'
        },
        series: [
            {
                name: 'Access From',
                type: 'pie',
                radius: '50%',
                data: chartData,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };


    return (
        <PageContainer>
            <ReactECharts option={option} loadingOption={{
                showLoading: loading
            }}/>
        </PageContainer>
    );
};

export default ApiAnalysis;
