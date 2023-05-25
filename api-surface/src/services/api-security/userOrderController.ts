// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 POST /order/page */
export async function viewUserOrderPage(
  body: API.UserOrderPageQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultUserOrderPageVO>(`/security/order/page`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}