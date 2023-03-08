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

  type BaseResponseListPost = {
    code?: number;
    data?: Post[];
    message?: string;
  };

  type BaseResponseListUserApiInfo = {
    code?: number;
    data?: UserApiInfo[];
    message?: string;
  };

  type BaseResponseListUserVO = {
    code?: number;
    data?: UserVO[];
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

  type BaseResponsePagePost = {
    code?: number;
    data?: PagePost;
    message?: string;
  };

  type BaseResponsePageUserApiInfo = {
    code?: number;
    data?: PageUserApiInfo;
    message?: string;
  };

  type BaseResponsePageUserVO = {
    code?: number;
    data?: PageUserVO;
    message?: string;
  };

  type BaseResponsePost = {
    code?: number;
    data?: Post;
    message?: string;
  };

  type BaseResponseUser = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserApiInfo = {
    code?: number;
    data?: UserApiInfo;
    message?: string;
  };

  type BaseResponseUserVO = {
    code?: number;
    data?: UserVO;
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

  type getInvokeUserParams = {
    accessKey: string;
  };

  type getPostByIdParams = {
    id: number;
  };

  type getUserApiInfoByIdParams = {
    id: number;
  };

  type getUserByIdParams = {
    id: number;
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

  type listPostByPageParams = {
    postQueryRequest: PostQueryRequest;
  };

  type listPostParams = {
    postQueryRequest: PostQueryRequest;
  };

  type listUserApiInfoByPageParams = {
    userApiInfoQueryRequest: UserApiInfoQueryRequest;
  };

  type listUserApiInfoParams = {
    userApiInfoQueryRequest: UserApiInfoQueryRequest;
  };

  type listUserByPageParams = {
    userQueryRequest: UserQueryRequest;
  };

  type listUserParams = {
    userQueryRequest: UserQueryRequest;
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

  type PagePost = {
    records?: Post[];
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

  type PageUserApiInfo = {
    records?: UserApiInfo[];
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

  type PageUserVO = {
    records?: UserVO[];
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

  type Post = {
    id?: number;
    age?: number;
    gender?: number;
    education?: string;
    place?: string;
    job?: string;
    contact?: string;
    loveExp?: string;
    content?: string;
    photo?: string;
    reviewStatus?: number;
    reviewMessage?: string;
    viewNum?: number;
    thumbNum?: number;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type PostAddRequest = {
    age?: number;
    gender?: number;
    education?: string;
    place?: string;
    job?: string;
    contact?: string;
    loveExp?: string;
    content?: string;
    photo?: string;
  };

  type PostQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    age?: number;
    gender?: number;
    education?: string;
    place?: string;
    job?: string;
    contact?: string;
    loveExp?: string;
    content?: string;
    reviewStatus?: number;
    userId?: number;
  };

  type PostUpdateRequest = {
    id?: number;
    age?: number;
    gender?: number;
    education?: string;
    place?: string;
    job?: string;
    contact?: string;
    loveExp?: string;
    content?: string;
    photo?: string;
    reviewStatus?: number;
    reviewMessage?: string;
  };

  type User = {
    id?: number;
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    gender?: number;
    userRole?: string;
    userPassword?: string;
    accessKey?: string;
    secretKey?: string;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type UserAddRequest = {
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    gender?: number;
    userRole?: string;
    userPassword?: string;
  };

  type UserApiInfo = {
    id?: number;
    userId?: number;
    apiInfoId?: number;
    totalNum?: number;
    leftNum?: number;
    status?: number;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type UserApiInfoAddRequest = {
    userId?: number;
    apiInfoId?: number;
    totalNum?: number;
  };

  type UserApiInfoQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    userId?: number;
    apiInfoId?: number;
    totalNum?: number;
    leftNum?: number;
    status?: number;
  };

  type UserApiInfoUpdateRequest = {
    id?: number;
    totalNum?: number;
    leftNum?: number;
    status?: number;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    gender?: number;
    userRole?: string;
    createTime?: string;
    updateTime?: string;
  };

  type UserRegisterRequest = {
    userAccount?: string;
    userPassword?: string;
    checkPassword?: string;
  };

  type UserUpdateRequest = {
    id?: number;
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    gender?: number;
    userRole?: string;
    userPassword?: string;
  };

  type UserVO = {
    id?: number;
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    gender?: number;
    userRole?: string;
    createTime?: string;
    updateTime?: string;
  };
}
