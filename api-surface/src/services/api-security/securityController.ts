// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 GET /csrf */
export async function autoGetCsrfToken(options?: { [key: string]: any }) {
  return request<API.ResultCustomCsrfToken>(`/security/csrf`, {
    method: 'GET',
    ...(options || {}),
  });
}
