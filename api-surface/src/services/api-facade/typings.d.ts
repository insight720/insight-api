declare namespace API {
    type ApiInfo = {
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

    type ApiInfoAddRequest = {
        name?: string;
        description?: string;
        url?: string;
        requestParams?: string;
        requestHeader?: string;
        responseHeader?: string;
        method?: string;
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

    type ApiInfoVO = {
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

    type BaseResponseApiInfo = {
        code?: number;
        data?: ApiInfo;
        message?: string;
    };

    type BaseResponseBoolean = {
        code?: number;
        data?: boolean;
        message?: string;
    };

    type BaseResponseListApiInfo = {
        code?: number;
        data?: ApiInfo[];
        message?: string;
    };

    type BaseResponseListApiInfoVO = {
        code?: number;
        data?: ApiInfoVO[];
        message?: string;
    };

    type BaseResponseLong = {
        code?: number;
        data?: number;
        message?: string;
    };

    type BaseResponseObject = {
        code?: number;
        data?: Record<string, any>;
        message?: string;
    };

    type BaseResponsePageApiInfo = {
        code?: number;
        data?: PageApiInfo;
        message?: string;
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

    type PageApiInfo = {
        records?: ApiInfo[];
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
}
