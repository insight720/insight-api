declare namespace API {
  type AccountVerificationCodeCheckDTO = {
    accountId?: string;
    codeCheckDTO?: VerificationCodeCheckDTO;
  };

  type ApiKeyStatusDTO = {
    accountId?: string;
    originalStatus: string;
    targetStatus: string;
    codeCheckDTO?: VerificationCodeCheckDTO;
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

  type NonAdminAuthorityDTO = {
    accountId?: string;
    originalAuthoritySet: string[];
    targetAuthoritySet: string[];
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

  type UserProfileSettingDTO = {
    profileId?: string;
    originalAvatar?: string;
    nickname?: string;
    website?: string;
    github?: string;
    gitee?: string;
    biography?: string;
  };

  type UserRegistryDTO = {
    username: string;
    password: string;
    confirmedPassword: string;
    codeCheckDTO?: VerificationCodeCheckDTO;
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
}
