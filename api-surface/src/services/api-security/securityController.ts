// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 GET /csrf */
export async function generateCsrfToken(options?: { [key: string]: any }) {
  return request<API.ResultVoid>(`/security/csrf`, {
    method: 'GET',
    ...(options || {}),
  });
}
