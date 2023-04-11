// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 GET /details/user */
export async function getLoginUserInfo(options?: { [key: string]: any }) {
  return request<API.ResultLoginUserDTO>(`/security/details/user`, {
    method: 'GET',
    ...(options || {}),
  });
}
