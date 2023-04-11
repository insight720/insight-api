declare namespace API {
  type AccountAuthorityDTO = {
    accountId: string;
    authority: string;
  };

  type AccountStatusDTO = {
    accountId: string;
    statusCode: number;
  };

  type generateKeyPairParams = {
    accountId: string;
  };

  type KeyPairDTO = {
    accountKey?: string;
    secretKey?: string;
  };

  type LoginUserDTO = {
    username?: string;
    email?: string;
    phoneNumber?: string;
    authority?: string;
    accountKey?: string;
    accountStatus?: number;
    profileId?: string;
    accountId?: string;
    avatar?: string;
    nickname?: string;
    website?: string;
    github?: string;
    gitee?: string;
    biography?: string;
    ipAddress?: string;
    ipOrigin?: string;
    lastLoginTime?: string;
  };

  type ResultKeyPairDTO = {
    code?: string;
    message?: string;
    data?: KeyPairDTO;
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

  type ResultVoid = {
    code?: string;
    message?: string;
    data?: Record<string, any>;
  };

  type UserProfileSettingVO = {
    profileId: string;
    avatar?: string;
    nickname?: string;
    website?: string;
    github?: string;
    gitee?: string;
    biography?: string;
  };

  type UserRegistryVO = {
    username: string;
    password: string;
    confirmedPassword: string;
  };
}
