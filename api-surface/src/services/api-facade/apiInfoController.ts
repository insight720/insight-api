// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 POST /add */
export async function addApiInfo(body: API.ApiInfoAddRequest, options?: { [key: string]: any }) {
  return request<API.ResultLong>(`/facade/add`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /delete */
export async function deleteApiInfo(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>(`/facade/delete`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /get */
export async function getApiInfoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getApiInfoByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultApiInfoEntity>(`/facade/get`, {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /getApiInfo */
export async function getApiInfo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getApiInfoParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultApiInfoEntity>(`/facade/getApiInfo`, {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /invoke */
export async function invokeApiInfo(
  body: API.ApiInfoInvokeRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultObject>(`/facade/invoke`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /list */
export async function listApiInfo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listApiInfoParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultListApiInfoEntity>(`/facade/list`, {
    method: 'GET',
    params: {
      ...params,
      apiInfoQueryRequest: undefined,
      ...params['apiInfoQueryRequest'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /list/page */
export async function listApiInfoByPage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listApiInfoByPageParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageApiInfoEntity>(`/facade/list/page`, {
    method: 'GET',
    params: {
      ...params,
      apiInfoQueryRequest: undefined,
      ...params['apiInfoQueryRequest'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /offline */
export async function offlineApiInfo(body: API.IdRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>(`/facade/offline`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /online */
export async function onlineApiInfo(body: API.IdRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>(`/facade/online`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /update */
export async function updateApiInfo(
  body: API.ApiInfoUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>(`/facade/update`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
