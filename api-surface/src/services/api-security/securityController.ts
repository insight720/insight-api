// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 POST /api/admin/page */
export async function viewApiAdminPage(
  body: API.ApiAdminPageQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultApiAdminPageVO>(`/security/api/admin/page`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /api/creator/${param0} */
export async function viewApiCreator(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.viewApiCreatorParams,
  options?: { [key: string]: any },
) {
  const { accountId: param0, ...queryParams } = params;
  return request<API.ResultApiCreatorVO>(`/security/api/creator/${param0}`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /csrf/token */
export async function getCsrfToken(options?: { [key: string]: any }) {
  return request<API.ResultVoid>(`/security/csrf/token`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /request/user/info/result */
export async function getClientUserInfoResult(
  body: API.ClientUserInfoQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultClientUserInfoDTO>(`/security/request/user/info/result`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/admin/page */
export async function viewUserAdminPage(
  body: API.UserAdminPageQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultUserAdminPageVO>(`/security/user/admin/page`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/api/digest/page */
export async function viewUserApiDigestPage(
  body: API.UserApiDigestPageQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultUserApiDigestPageVO>(`/security/user/api/digest/page`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/api/format/and/quantity/usage */
export async function viewUserApiFormatAndQuantityUsage(
  body: API.UserApiFormatAndQuantityUsageQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultUserApiFormatAndQuantityUsageVO>(
    `/security/user/api/format/and/quantity/usage`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      data: body,
      ...(options || {}),
    },
  );
}

/** 此处后端没有提供注释 POST /user/api/test */
export async function testUserApi(body: API.UserApiTestDTO, options?: { [key: string]: any }) {
  return request<API.ResultUserApiTestVO>(`/security/user/api/test`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /verification/code */
export async function getVerificationCode(
  body: API.VerificationCodeSendingDTO,
  options?: { [key: string]: any },
) {
  return request<API.ResultVoid>(`/security/verification/code`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
