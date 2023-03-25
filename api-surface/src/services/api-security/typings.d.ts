declare namespace API {
  type CustomCsrfToken = {
    tokenValue?: string;
  };

  type LoginUserDTO = {
    username?: string;
    email?: string;
    phoneNumber?: string;
    authority?: string;
    accountStatus?: number;
    profileId?: number;
    accountId?: number;
    nickname?: string;
    avatar?: string;
    biography?: string;
  };

  type ResultCustomCsrfToken = {
    code?: string;
    message?: string;
    data?: CustomCsrfToken;
  };

  type ResultLoginUserDTO = {
    code?: string;
    message?: string;
    data?: LoginUserDTO;
  };

  type ResultVoid = {
    code?: string;
    message?: string;
    data?: Record<string, any>;
  };

  type UserRegistryVO = {
    username: string;
    password: string;
    confirmedPassword: string;
  };
}
