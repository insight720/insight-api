package pers.project.api.gateway.constant;

/**
 * Gateway 交换机属性名常量
 * <p>
 * OpenFeign 请求查询后保存在 attributes 中供过滤器下游使用。
 *
 * @author Luo Fei
 * @date 2023/07/15
 */
public final class ExchangeAttributeNameConst {

    /**
     * 客户端账户主键
     */
    public static final String CLIENT_ACCOUNT_ID = "clientAccountId";

    /**
     * 用户接口计数用法主键
     */
    public static final String USER_QUANTITY_USAGE_ID = "userQuantityUsageId";

    /**
     * 用户接口计数用法主键
     */
    public static final String API_DIGEST_ID = "apiDigestId";

    private ExchangeAttributeNameConst() {
    }
}