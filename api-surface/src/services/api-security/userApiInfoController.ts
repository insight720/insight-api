// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 POST /userApiInfo/add */
export async function addUserApiInfo(
  body: API.UserApiInfoAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong>(`/security/userApiInfo/add`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /userApiInfo/delete */
export async function deleteUserApiInfo(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>(`/security/userApiInfo/delete`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /userApiInfo/get */
export async function getUserApiInfoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserApiInfoByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseUserApiInfo>(`/security/userApiInfo/get`, {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /userApiInfo/invokeCount */
export async function invokeCount(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.invokeCountParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean>(`/security/userApiInfo/invokeCount`, {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /userApiInfo/list */
export async function listUserApiInfo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listUserApiInfoParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseListUserApiInfo>(`/security/userApiInfo/list`, {
    method: 'GET',
    params: {
      ...params,
      userApiInfoQueryRequest: undefined,
      ...params['userApiInfoQueryRequest'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /userApiInfo/list/page */
export async function listUserApiInfoByPage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listUserApiInfoByPageParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageUserApiInfo>(`/security/userApiInfo/list/page`, {
    method: 'GET',
    params: {
      ...params,
      userApiInfoQueryRequest: undefined,
      ...params['userApiInfoQueryRequest'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /userApiInfo/update */
export async function updateUserApiInfo(
  body: API.UserApiInfoUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean>(`/security/userApiInfo/update`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
