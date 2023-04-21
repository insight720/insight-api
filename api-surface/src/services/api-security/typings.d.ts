declare namespace API {
    type ApiKeyPairVO = {
        secretId?: string;
        secretKey?: string;
    };

    type getNewApiKeyPairParams = {
        accountId: string;
    };

    type LoginUserDTO = {
        username?: string;
        emailAddress?: string;
        phoneNumber?: string;
        authority?: string;
        secretId?: string;
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
        ipLocation?: string;
        lastLoginTime?: string;
    };

    type PhoneOrEmailLoginDTO = {
        rememberMe: string,
        emailAddress?: string;
        phoneNumber?: string;
        strategy: string;
        verificationCode?: string;
    };

    type ResultApiKeyPairVO = {
        code?: string;
        message?: string;
        data?: ApiKeyPairVO;
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

    type UserAccountAuthorityDTO = {
        accountId?: string;
        authority: string;
    };

    type UserAccountStatusDTO = {
        accountId?: string;
        statusCode: number;
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
        emailAddress?: string;
        phoneNumber?: string;
        strategy: string;
        verificationCode?: string;
    };

    type VerificationCodeSendingDTO = {
        phoneNumber?: string;
        emailAddress?: string;
        strategy: string;
    };
}
