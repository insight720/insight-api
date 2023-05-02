// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 PUT /account/api/key/status */
export async function modifyApiKeyStatus(
  body: API.ApiKeyStatusModificationDTO,
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

/** 此处后端没有提供注释 DELETE /account/deletion */
export async function deleteAccount(
  body: API.AccountVerificationCodeCheckDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/account/deletion`, {
    method: 'DELETE',
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
  body: API.NonAdminAuthorityModificationDTO,
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

/** 此处后端没有提供注释 PUT /account/password */
export async function modifyPassword(
  body: API.PasswordModificationDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/account/password`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /account/phone/or/email/binding */
export async function bindPhoneOrEmail(
  body: API.AccountVerificationCodeCheckDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/account/phone/or/email/binding`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /account/phone/or/email/unbinding */
export async function unbindPhoneOrEmail(
  body: API.AccountVerificationCodeCheckDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/account/phone/or/email/unbinding`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /account/registry */
export async function register(body: API.UserAccountRegistryDTO, options?: { [key: string]: any }) {
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

/** 此处后端没有提供注释 PUT /account/username */
export async function modifyUsername(
  body: API.UsernameModificationDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/account/username`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /account/username/and/password/setting */
export async function setUsernameAndPassword(
  body: API.UsernameAndPasswordSettingDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/account/username/and/password/setting`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
