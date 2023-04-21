// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 POST /details/login */
export async function loginByVerificationCode(
  body: API.PhoneOrEmailLoginDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultLoginUserDTO>(`/security/details/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /details/user */
export async function getLoginUserInfo(options?: { [key: string]: any }) {
  return request<API.ResultLoginUserDTO>(`/security/details/user`, {
    method: 'GET',
    credentials: 'include', // 带上Cookie
    ...(options || {}),
  });
}
