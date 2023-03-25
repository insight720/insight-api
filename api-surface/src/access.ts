/**
 * 权限管理
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { currentUser?: API.LoginUserDTO } | undefined) {
    const {currentUser} = initialState ?? {};
    return {
        // 用户权限
        canUser: currentUser,
        // 管理员权限
        canAdmin: currentUser && currentUser.authority === 'ROLE_ADMIN',
    };
}
