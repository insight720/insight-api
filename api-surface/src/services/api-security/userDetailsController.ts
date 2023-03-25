// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 GET /details/current */
export async function fetchLoginUserInfo(options?: { [key: string]: any }) {
  return request<API.ResultLoginUserDTO>(`/security/details/current`, {
    method: 'GET',
    ...(options || {}),
  });
}
