// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 GET /userApiInfo/list/page */
export async function listUserApiInfoByPage(
    // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
    params: API.listUserApiInfoByPageParams,
    options?: { [key: string]: any },
) {
    return request<API.BaseResponsePageUserApiInfo>(`/security/userApiInfo/list/page`, {
        method: 'GET',
        params: {
            ...params,
            userApiInfoQueryRequest: undefined,
            ...params['userApiInfoQueryRequest'],
        },
        ...(options || {}),
    });
}
