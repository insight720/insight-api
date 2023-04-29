// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 GET /details/login/user/info */
export async function getLoginUserInfo(options?: { [key: string]: any }) {
  return request<API.ResultLoginUserDTO>(`/security/details/login/user/info`, {
    method: 'GET',
    ...(options || {}),
  });
}
