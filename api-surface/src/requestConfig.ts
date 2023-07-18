import type {RequestOptions} from '@@/plugin-request/request';
import {RequestConfig} from "@umijs/max";


// 与后端约定的响应结果模型
interface Result {
    code: string;
    message: string;
    data: any;
}

export const requestConfig: RequestConfig = {
    // 后端地址
    // baseURL: 'http://localhost:80/gateway',
    baseURL: 'https://insightapi.cn/gateway',
    // 请求带上 Cookie
    withCredentials: true,

    // 请求拦截器
    requestInterceptors: [// 请求配置
        (config: RequestOptions) => {
            // 拦截请求配置，进行个性化处理。
            return config;
        },
    ],

    // 响应拦截器
    responseInterceptors: [
        (result) => {
            // 拦截响应数据，进行个性化处理
            const {data} = result as unknown as Result;
            // 响应错误信息
            if (data.code !== '00000' || data.message !== 'ok') {
                throw new Error(data.message);
            }
            return result;
        },
    ],
};
