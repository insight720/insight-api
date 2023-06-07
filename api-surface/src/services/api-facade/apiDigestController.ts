// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 POST /digest/page */
export async function viewApiDigestPage(
  body: API.ApiDigestPageQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultApiDigestPageVO>(`/facade/digest/page`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
