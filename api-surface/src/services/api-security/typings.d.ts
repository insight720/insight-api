declare namespace API {
  type AccountVerificationCodeCheckDTO = {
    accountId?: string;
    codeCheckDTO: VerificationCodeCheckDTO;
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

  type UserAccountRegistryDTO = {
    username: string;
    password: string;
    codeCheckDTO: VerificationCodeCheckDTO;
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
}
