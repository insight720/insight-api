// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 GET /userApiInfo/invokeCount */
export async function invokeCount(
    // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
    params: API.invokeCountParams,
    options?: { [key: string]: any },
) {
    return request<API.BaseResponseBoolean>(`/facade/userApiInfo/invokeCount`, {
        method: 'GET',
        params: {
            ...params,
        },
        ...(options || {}),
    });
}
