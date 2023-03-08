/**
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
/*
export default function access(initialState: { currentUser?: API.CurrentUser } | undefined) {
  const { currentUser } = initialState ?? {};
  return {
    canAdmin: currentUser && currentUser.access === 'admin',
  };
}
*/

export default function access(initialState: { currentUser?: API.UserVO } | undefined) {
    const {currentUser} = initialState ?? {};
    return {
        // 用户权限
        canUser: currentUser,
        // 管理员权限
        canAdmin: currentUser && currentUser.userRole === 'admin',
    };
}
