// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 GET /quantity/usage/${param0} */
export async function viewApiQuantityUsage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.viewApiQuantityUsageParams,
  options?: { [key: string]: any },
) {
  const { digestId: param0, ...queryParams } = params;
  return request<API.ResultApiQuantityUsageVO>(`/facade/quantity/usage/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /quantity/usage/stock/info/${param0} */
export async function viewApiStockInfo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.viewApiStockInfoParams,
  options?: { [key: string]: any },
) {
  const { digestId: param0, ...queryParams } = params;
  return request<API.ResultApiStockInfoVO>(`/facade/quantity/usage/stock/info/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}
