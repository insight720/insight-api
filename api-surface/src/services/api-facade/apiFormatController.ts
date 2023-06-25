// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 GET /format/${param0} */
export async function viewApiFormat(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.viewApiFormatParams,
  options?: { [key: string]: any },
) {
  const { digestId: param0, ...queryParams } = params;
  return request<API.ResultApiFormatVO>(`/facade/format/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /format/test/${param0} */
export async function viewApiTestFormat(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.viewApiTestFormatParams,
  options?: { [key: string]: any },
) {
  const { digestId: param0, ...queryParams } = params;
  return request<API.ResultApiTestFormatVO>(`/facade/format/test/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}
