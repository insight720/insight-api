// @ts-ignore
/* eslint-disable */
import {request} from '@umijs/max';
import {RcFile} from "antd/es/upload/interface";

/** 此处后端没有提供注释 PUT /profile/setting */
export async function setProfile(
    avatarFile: RcFile,
    profileSettingVO: API.UserProfileSettingVO,
    options ?: {
        [key: string]: any
    }
) {
    const formData = new FormData();
    formData.append('avatarFile', avatarFile);
    // 用 multipart/form-data 同时上传 file 和 json，必须额外设置 application/json
    formData.append('profileSettingVO', new Blob([JSON.stringify(profileSettingVO)], {
        type: 'application/json',
    }));
    return request<API.ResultVoid>('/security/profile/setting', {
        method: 'PUT',
        headers: {
            'Content-Type': 'multipart/form-data',
        },
        data: formData,
        ...options,
    });
}

