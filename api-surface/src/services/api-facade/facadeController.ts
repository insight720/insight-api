// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 POST /api/admin/page/result */
export async function getApiAdminPageResult(
  body: API.ApiAdminPageQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultApiAdminPageVO>(`/facade/api/admin/page/result`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /quantity/usage/api/info/result */
export async function getQuantityUsageApiInfoResult(
  body: API.QuantityUsageApiInfoQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultQuantityUsageApiInfoDTO>(`/facade/quantity/usage/api/info/result`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/api/digest/page/result */
export async function getUserApiDigestPageResult(
  body: API.UserApiDigestPageQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultUserApiDigestPageVO>(`/facade/user/api/digest/page/result`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/api/format/and/quantity/usage/result */
export async function getUserApiFormatAndQuantityUsageResult(
  body: API.UserApiFormatAndQuantityUsageQuery,
  options?: { [key: string]: any },
) {
  return request<API.ResultUserApiFormatAndQuantityUsageVO>(
    `/facade/user/api/format/and/quantity/usage/result`,
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
