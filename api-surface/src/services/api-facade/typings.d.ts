declare namespace API {
  type ApiInfoAddRequest = {
    name?: string;
    description?: string;
    url?: string;
    requestParams?: string;
    requestHeader?: string;
    responseHeader?: string;
    method?: string;
  };

  type ApiInfoData = {
    id?: number;
    name?: string;
    description?: string;
    url?: string;
    requestParams?: string;
    requestHeader?: string;
    responseHeader?: string;
    status?: number;
    method?: string;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
    totalNum?: number;
  };

  type ApiInfoEntity = {
    id?: number;
    name?: string;
    description?: string;
    url?: string;
    requestParams?: string;
    requestHeader?: string;
    responseHeader?: string;
    status?: number;
    method?: string;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type ApiInfoInvokeRequest = {
    id?: number;
    requestParams?: string;
  };

  type ApiInfoQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    name?: string;
    description?: string;
    url?: string;
    requestParams?: string;
    requestHeader?: string;
    responseHeader?: string;
    status?: number;
    method?: string;
    userId?: number;
  };

  type ApiInfoUpdateRequest = {
    id?: number;
    name?: string;
    description?: string;
    url?: string;
    requestParams?: string;
    requestHeader?: string;
    responseHeader?: string;
    status?: number;
    method?: string;
  };

  type DeleteRequest = {
    id?: number;
  };

  type getApiInfoByIdParams = {
    id: number;
  };

  type getApiInfoParams = {
    url: string;
    method: string;
  };

  type IdRequest = {
    id?: number;
  };

  type invokeCountParams = {
    apiInfoId: number;
    userId: number;
  };

  type listApiInfoByPageParams = {
    apiInfoQueryRequest: ApiInfoQueryRequest;
  };

  type listApiInfoParams = {
    apiInfoQueryRequest: ApiInfoQueryRequest;
  };

  type OrderItem = {
    column?: string;
    asc?: boolean;
  };

  type PageApiInfoEntity = {
    records?: ApiInfoEntity[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: boolean;
    searchCount?: boolean;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type ResponseApiInfoEntity = {
    code?: number;
    data?: ApiInfoEntity;
    message?: string;
  };

  type ResponseBoolean = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type ResponseListApiInfoData = {
    code?: number;
    data?: ApiInfoData[];
    message?: string;
  };

  type ResponseListApiInfoEntity = {
    code?: number;
    data?: ApiInfoEntity[];
    message?: string;
  };

  type ResponseLong = {
    code?: number;
    data?: number;
    message?: string;
  };

  type ResponseObject = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type ResponsePageApiInfoEntity = {
    code?: number;
    data?: PageApiInfoEntity;
    message?: string;
  };
}
