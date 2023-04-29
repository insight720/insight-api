/**
 * 权限管理
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { currentUser?: API.LoginUserDTO } | undefined) {
    const {currentUser} = initialState ?? {};

    /**
     * 权限枚举
     */
    enum AuthorityEnum {
        ROLE_USER = "ROLE_USER",
        ROLE_TEST = "ROLE_TEST",
        ROLE_ADMIN = "ROLE_ADMIN"
    }

    const canAdmin: boolean | undefined =
        currentUser && currentUser?.authoritySet?.includes(AuthorityEnum.ROLE_ADMIN);
    const canTest: boolean | undefined =
        canAdmin || (currentUser && currentUser?.authoritySet?.includes(AuthorityEnum.ROLE_TEST));
    const canUser: boolean | undefined =
        canAdmin || canTest || (currentUser && currentUser?.authoritySet?.includes(AuthorityEnum.ROLE_USER));

    return {
        // 管理员权限
        canAdmin: canAdmin,
        // 测试权限
        canTest: canTest,
        // 用户权限
        canUser: canUser,
    };
}

