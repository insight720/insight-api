declare namespace API {
    type BaseResponseBoolean = {
        code?: number;
        data?: boolean;
        message?: string;
    };

    type BaseResponseLong = {
        code?: number;
        data?: number;
        message?: string;
    };

    type BaseResponseUser = {
        code?: number;
        data?: User;
        message?: string;
    };

    type BaseResponseUserVO = {
        code?: number;
        data?: UserVO;
        message?: string;
    };

    type getInvokeUserParams = {
        accessKey: string;
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

    type UserLoginRequest = {
        userAccount?: string;
        userPassword?: string;
    };

    type UserRegisterRequest = {
        userAccount?: string;
        userPassword?: string;
        checkPassword?: string;
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
