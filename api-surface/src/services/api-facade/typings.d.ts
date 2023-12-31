declare namespace API {
  type ApiAdminPageQuery = {
    size?: number;
    current?: number;
    usageTypeSet?: string[];
    digestUpdateTimeRange?: string[];
    formatId?: string;
    methodSet?: number[];
    formatUpdateTimeRange?: string[];
    apiStatusSet?: number[];
    createTimeRange?: string;
    isDeleted?: number;
    accountId?: string;
    apiName?: string;
    description?: string;
    url?: string;
    digestId?: string;
    requestParam?: string;
    requestHeader?: string;
    requestBody?: string;
    responseHeader?: string;
    responseBody?: string;
  };

  type ApiAdminPageVO = {
    total?: number;
    apiAdminVOList?: ApiAdminVO[];
  };

  type ApiAdminVO = {
    digestUpdateTime?: string;
    formatId?: string;
    formatUpdateTime?: string;
    apiName?: string;
    description?: string;
    method?: number;
    url?: string;
    usageType?: string;
    apiStatus?: number;
    digestId?: string;
    requestParam?: string;
    requestHeader?: string;
    requestBody?: string;
    responseHeader?: string;
    responseBody?: string;
    isDeleted?: number;
    createTime?: string;
  };

  type ApiDigestPageQuery = {
    size: number;
    current: number;
    apiName?: string;
    description?: string;
    methodSet?: string[];
    url?: string;
    usageTypeSet?: string[];
    apiStatusSet?: number[];
    createTimeRange?: string[];
    updateTimeRange?: string[];
  };

  type ApiDigestPageVO = {
    total?: number;
    digestVOList?: ApiDigestVO[];
  };

  type ApiDigestVO = {
    methodSet?: string[];
    usageTypeSet?: string[];
    digestId?: string;
    accountId?: string;
    apiName?: string;
    description?: string;
    url?: string;
    apiStatus?: number;
    createTime?: string;
    updateTime?: string;
  };

  type ApiFormatVO = {
    requestParam?: string;
    requestHeader?: string;
    requestBody?: string;
    responseHeader?: string;
    responseBody?: string;
    createTime?: string;
    updateTime?: string;
  };

  type ApiQuantityUsageVO = {
    total?: number;
    failure?: number;
    stock?: number;
    lockedStock?: number;
    usageStatus?: number;
    createTime?: string;
    updateTime?: string;
  };

  type ApiStockInfoVO = {
    stock?: number;
    lockedStock?: number;
    usageStatus?: number;
    updateTime?: string;
  };

  type ApiTestFormatVO = {
    requestParam?: string;
    requestHeader?: string;
    requestBody?: string;
    responseHeader?: string;
    responseBody?: string;
  };

  type QuantityUsageApiInfoDTO = {
    digestId?: string;
    usageId?: string;
  };

  type QuantityUsageApiInfoQuery = {
    accountId?: string;
    method: string;
    originalUrl: string;
  };

  type ResultApiAdminPageVO = {
    code?: string;
    message?: string;
    data?: ApiAdminPageVO;
  };

  type ResultApiDigestPageVO = {
    code?: string;
    message?: string;
    data?: ApiDigestPageVO;
  };

  type ResultApiFormatVO = {
    code?: string;
    message?: string;
    data?: ApiFormatVO;
  };

  type ResultApiQuantityUsageVO = {
    code?: string;
    message?: string;
    data?: ApiQuantityUsageVO;
  };

  type ResultApiStockInfoVO = {
    code?: string;
    message?: string;
    data?: ApiStockInfoVO;
  };

  type ResultApiTestFormatVO = {
    code?: string;
    message?: string;
    data?: ApiTestFormatVO;
  };

  type ResultQuantityUsageApiInfoDTO = {
    code?: string;
    message?: string;
    data?: QuantityUsageApiInfoDTO;
  };

  type ResultUserApiDigestPageVO = {
    code?: string;
    message?: string;
    data?: UserApiDigestPageVO;
  };

  type ResultUserApiFormatAndQuantityUsageVO = {
    code?: string;
    message?: string;
    data?: UserApiFormatAndQuantityUsageVO;
  };

  type ResultVoid = {
    code?: string;
    message?: string;
    data?: Record<string, any>;
  };

  type UserApiDigestPageQuery = {
    accountId?: string;
    size: number;
    current: number;
    apiName?: string;
    description?: string;
    methodSet?: string[];
    url?: string;
    usageTypeSet?: string[];
    apiStatusSet?: number[];
    createTimeRange?: string[];
    updateTimeRange?: string[];
  };

  type UserApiDigestPageVO = {
    total?: number;
    digestVOList?: UserApiDigestVO[];
  };

  type UserApiDigestVO = {
    methodSet?: string[];
    usageTypeSet?: string[];
    digestId?: string;
    accountId?: string;
    apiName?: string;
    description?: string;
    url?: string;
    apiStatus?: number;
    createTime?: string;
    updateTime?: string;
  };

  type UserApiFormatAndQuantityUsageQuery = {
    accountId?: string;
    digestId?: string;
  };

  type UserApiFormatAndQuantityUsageVO = {
    requestParam?: string;
    requestHeader?: string;
    requestBody?: string;
    responseHeader?: string;
    responseBody?: string;
    total?: number;
    failure?: number;
    stock?: number;
    usageStatus?: number;
  };

  type viewApiFormatParams = {
    digestId: string;
  };

  type viewApiQuantityUsageParams = {
    digestId: string;
  };

  type viewApiStockInfoParams = {
    digestId: string;
  };

  type viewApiTestFormatParams = {
    digestId: string;
  };
}
