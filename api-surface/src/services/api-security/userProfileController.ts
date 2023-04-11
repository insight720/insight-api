// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';

/** 此处后端没有提供注释 POST /profile/avatar */
export async function selectAvatar(body: {}, options?: { [key: string]: any }) {
  return request<API.ResultString>(`/security/profile/avatar`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /profile/setting */
export async function setProfile(body: API.UserProfileSettingVO, options?: { [key: string]: any }) {
  return request<API.ResultVoid>(`/security/profile/setting`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
