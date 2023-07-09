// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 POST /order/page */
export async function viewQuantityUsageOrderPage(
  body: API.QuantityUsageOrderPageQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultQuantityUsageOrderPageVO>(`/security/order/page`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /order/quantity/usage/cancellation */
export async function cancelQuantityUsageOrder(
  body: API.QuantityUsageOrderCancellationDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/order/quantity/usage/cancellation`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /order/quantity/usage/confirmation */
export async function confirmQuantityUsageOrder(
  body: API.QuantityUsageOrderConfirmationDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/order/quantity/usage/confirmation`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /order/quantity/usage/creation */
export async function placeQuantityUsageOrder(
  body: API.QuantityUsageOrderCreationDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/order/quantity/usage/creation`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
