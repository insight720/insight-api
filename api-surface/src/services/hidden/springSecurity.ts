// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** Spring Security 的登录接口 POST /security/login */
export async function login(params: URLSearchParams, options?: { [key: string]: any }) {
    return request<API.ResultLoginUserDTO>(`/security/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        data: params,
        ...(options || {}),
    });
}

/** Spring Security 的注销接口（因为配置了 CSRF 防护，所以请求方法必须是 POST）POST /security/logout */
export async function logout(options?: { [key: string]: any }) {
    return request<API.ResultVoid>(`/security/logout`, {
        method: 'POST',
        ...(options || {}),
    });
}

/**
 * 后端不存在该类型（写在这里防止 OpenAPI 删除）
 */
export type UsernameLoginDTO = {
    username: string,
    password: string,
};
