// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 POST /post/add */
export async function addPost(body: API.PostAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/post/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /post/delete */
export async function deletePost(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/post/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /post/get */
export async function getPostById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPostByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePost>('/post/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /post/list */
export async function listPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listPostParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseListPost>('/post/list', {
    method: 'GET',
    params: {
      ...params,
      postQueryRequest: undefined,
      ...params['postQueryRequest'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /post/list/page */
export async function listPostByPage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listPostByPageParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePagePost>('/post/list/page', {
    method: 'GET',
    params: {
      ...params,
      postQueryRequest: undefined,
      ...params['postQueryRequest'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /post/update */
export async function updatePost(body: API.PostUpdateRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/post/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
