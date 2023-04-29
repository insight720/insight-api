// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 PUT /account/api/key/status */
export async function modifyApiKeyStatus(
  body: API.ApiKeyStatusDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/account/api/key/status`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /account/new/api/key */
export async function getNewApiKey(
  body: API.AccountVerificationCodeCheckDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultString>(`/security/account/new/api/key`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /account/non/admin/authority */
export async function modifyNonAdminAuthority(
  body: API.NonAdminAuthorityDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/account/non/admin/authority`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /account/registry */
export async function register(body: API.UserRegistryDTO, options?: { [key: string]: any }) {
  return request<API.ResultVoid>(`/security/account/registry`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /account/secret/key */
export async function viewSecretKey(
  body: API.AccountVerificationCodeCheckDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultString>(`/security/account/secret/key`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
