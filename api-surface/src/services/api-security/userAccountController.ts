// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 PUT /account/authority */
export async function modifyAccountAuthority(
  body: API.AccountAuthorityDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/account/authority`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /account/key/${param0} */
export async function generateKeyPair(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.generateKeyPairParams,
  options?: { [key: string]: any },
) {
  const { accountId: param0, ...queryParams } = params;
  return request<API.ResultKeyPairDTO>(`/security/account/key/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /account/registry */
export async function registerUser(body: API.UserRegistryVO, options?: { [key: string]: any }) {
  return request<API.ResultVoid>(`/security/account/registry`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /account/status */
export async function modifyAccountStatus(
  body: API.AccountStatusDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/account/status`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
