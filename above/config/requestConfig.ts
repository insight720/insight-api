import type {RequestOptions} from '@@/plugin-request/request';
import type {RequestConfig} from '@umijs/max';

// 与后端约定的响应数据格式
interface BaseResponse {
    code?: number;
    data: any;
    message?: string;
}

// 请求配置
export const requestConfig: RequestConfig = {
    // 后端地址
    // baseURL: 'http://localhost:7529/security',
    baseURL: 'http://124.222.100.202:7529/security',
    // 请求带上 Cookie
    withCredentials: true,

    // 请求拦截器
    requestInterceptors: [
        (config: RequestOptions) => {
            // 拦截请求配置，进行个性化处理。
            const url = config?.url?.concat('?token = 123');
            return {...config, url};
        },
    ],

    // 响应拦截器
    responseInterceptors: [
        (response) => {
            // 拦截响应数据，进行个性化处理
            const {data} = response as unknown as BaseResponse;

            // 响应错误信息
            if (data?.message !== 'ok') {
                throw new Error(data.message || undefined);
            }
            return response;
        },
    ],
};
