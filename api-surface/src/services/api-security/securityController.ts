// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 GET /csrf/token */
export async function getCsrfToken(options?: { [key: string]: any }) {
  return request<API.ResultVoid>(`/security/csrf/token`, {
    method: 'GET',
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
