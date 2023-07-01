declare namespace API {
  type AccountVerificationCodeCheckDTO = {
    accountId?: string;
    codeCheckDTO: VerificationCodeCheckDTO;
  };

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

  type ApiCreatorVO = {
    accountUpdateTime?: string;
    profileUpdateTime?: string;
    authoritySet?: string[];
    createTime?: string;
    username?: string;
    emailAddress?: string;
    accountStatus?: number;
    avatar?: string;
    nickname?: string;
    website?: string;
    github?: string;
    gitee?: string;
    biography?: string;
    ipLocation?: string;
    lastLoginTime?: string;
  };

  type ApiKeyStatusModificationDTO = {
    accountId?: string;
    newStatus: string;
    codeCheckDTO: VerificationCodeCheckDTO;
  };

  type LoginUserDTO = {
    accountStatus?: string;
    authoritySet?: string[];
    username?: string;
    emailAddress?: string;
    phoneNumber?: string;
    secretId?: string;
    profileId?: string;
    accountId?: string;
    avatar?: string;
    nickname?: string;
    website?: string;
    github?: string;
    gitee?: string;
    biography?: string;
    ipAddress?: string;
    ipLocation?: string;
    lastLoginTime?: string;
  };

  type NonAdminAuthorityModificationDTO = {
    accountId?: string;
    newAuthoritySet: string[];
  };

  type PasswordModificationDTO = {
    accountId?: string;
    originalPassword?: string;
    newPassword: string;
    codeCheckDTO?: VerificationCodeCheckDTO;
  };

  type QuantityUsageOrderCreationDTO = {
    codeCheckDTO: VerificationCodeCheckDTO;
    accountId?: string;
    digestId?: string;
    methodSet?: string[];
    usageTypeSet?: string[];
    orderQuantity: string;
    apiName: string;
    description: string;
    url: string;
  };

  type ResultApiAdminPageVO = {
    code?: string;
    message?: string;
    data?: ApiAdminPageVO;
  };

  type ResultApiCreatorVO = {
    code?: string;
    message?: string;
    data?: ApiCreatorVO;
  };

  type ResultLoginUserDTO = {
    code?: string;
    message?: string;
    data?: LoginUserDTO;
  };

  type ResultString = {
    code?: string;
    message?: string;
    data?: string;
  };

  type ResultUserAdminPageVO = {
    code?: string;
    message?: string;
    data?: UserAdminPageVO;
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

  type ResultUserApiTestVO = {
    code?: string;
    message?: string;
    data?: UserApiTestVO;
  };

  type ResultUserOrderPageVO = {
    code?: string;
    message?: string;
    data?: UserOrderPageVO;
  };

  type ResultVoid = {
    code?: string;
    message?: string;
    data?: Record<string, any>;
  };

  type UserAccountRegistryDTO = {
    username: string;
    password: string;
    codeCheckDTO: VerificationCodeCheckDTO;
  };

  type UserAdminPageQuery = {
    size?: number;
    current?: number;
    authoritySet?: string[];
    accountStatusSet?: number[];
    accountUpdateTimeRange?: string[];
    profileUpdateTimeRange?: string[];
    createTimeRange?: string[];
    lastLoginTimeRange?: string[];
    isDeleted?: number;
    username?: string;
    emailAddress?: string;
    phoneNumber?: string;
    secretId?: string;
    secretKey?: string;
    accountId?: string;
    nickname?: string;
    website?: string;
    github?: string;
    gitee?: string;
    biography?: string;
    ipAddress?: string;
    ipLocation?: string;
  };

  type UserAdminPageVO = {
    total?: number;
    userAdminVOList?: UserAdminVO[];
  };

  type UserAdminVO = {
    profileId?: string;
    accountUpdateTime?: string;
    profileUpdateTime?: string;
    createTime?: string;
    isDeleted?: number;
    username?: string;
    emailAddress?: string;
    phoneNumber?: string;
    authority?: string;
    secretId?: string;
    secretKey?: string;
    accountStatus?: number;
    accountId?: string;
    avatar?: string;
    nickname?: string;
    website?: string;
    github?: string;
    gitee?: string;
    biography?: string;
    ipAddress?: string;
    ipLocation?: string;
    lastLoginTime?: string;
  };

  type UserApiDigestPageQuery = {
    accountId?: string;
    apiName?: string;
    description?: string;
    method?: number[];
    url?: string;
    usageType?: string[];
    apiStatus?: number[];
    createTime?: string[];
    updateTime?: string[];
    size?: number;
    current?: number;
  };

  type UserApiDigestPageVO = {
    total?: number;
    digestVOList?: UserApiDigestVO[];
  };

  type UserApiDigestVO = {
    digestId?: string;
    apiName?: string;
    description?: string;
    method?: number;
    url?: string;
    usageType?: string;
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

  type UserApiTestDTO = {
    accountId?: string;
    secretId: string;
    digestId?: string;
    method: string;
    requestParam?: string;
    requestHeader?: string;
    requestBody?: string;
  };

  type UserApiTestVO = {
    responseHeader?: string;
    responseBody?: string;
  };

  type UsernameAndPasswordSettingDTO = {
    accountId?: string;
    newUsername: string;
    newPassword: string;
    codeCheckDTO: VerificationCodeCheckDTO;
  };

  type UsernameModificationDTO = {
    accountId?: string;
    newUsername: string;
    codeCheckDTO: VerificationCodeCheckDTO;
  };

  type UserOrderPageQuery = {
    size?: number;
    current?: number;
    accountId?: string;
    orderSn?: string;
    description?: string;
    usageType?: number[];
    orderStatus?: number[];
    createTime?: string[];
    updateTime?: string[];
  };

  type UserOrderPageVO = {
    total?: number;
    userOrderVOList?: UserOrderVO[];
  };

  type UserOrderVO = {
    orderId?: string;
    orderSn?: string;
    description?: string;
    digestId?: string;
    usageId?: string;
    usageType?: string;
    orderStatus?: number;
    createTime?: string;
    updateTime?: string;
  };

  type UserProfileSettingDTO = {
    profileId?: string;
    originalAvatar?: string;
    nickname?: string;
    website?: string;
    github?: string;
    gitee?: string;
    biography?: string;
  };

  type VerificationCodeCheckDTO = {
    phoneNumber?: string;
    emailAddress?: string;
    strategy: string;
    verificationCode: string;
  };

  type VerificationCodeSendingDTO = {
    phoneNumber?: string;
    emailAddress?: string;
    strategy: string;
  };

  type viewApiCreatorParams = {
    accountId: string;
  };
}
