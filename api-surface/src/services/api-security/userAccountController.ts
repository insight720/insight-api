// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 POST /account/registry */
export async function registerUser(body: API.UserRegistryVO, options?: { [key: string]: any }) {
  return request<API.ResultVoid>(`/security/account/registry`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
