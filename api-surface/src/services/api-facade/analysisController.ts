// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /analysis/top/api/invoke */
export async function listTopInvokeApiInfo(options?: { [key: string]: any }) {
  return request<API.ResponseListApiInfoData>(`/facade/analysis/top/api/invoke`, {
    method: 'GET',
    ...(options || {}),
  });
}
