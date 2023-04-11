package pers.project.api.security.constant.enumeration;


/**
 * 可搜索的枚举类接口
 * <p>
 * 这个接口的使用方法如下：
 * <pre>
 * MonthEnum 是一个实现了 EnumSearch 接口的枚举类：
 * {@code
 * @Getter
 * @Accessors(fluent = true)
 * @RequiredArgsConstructor
 * public enum MonthEnum implements EnumSearch {
 *     JANUARY(1);
 *
 *     private final Integer number;
 *
 *     @Override
 *     public Object searchValue() {
 *         return number;
 *     }
 * }}
 * 在其他地方这样得到 MonthEnum 中的 JANUARY：
 * {@code MonthEnum monthEnum = EnumSearch.searchByValue(MonthEnum.class, 1);}
 * </pre>
 * 搜索效率与在每个枚举类中写一次搜索方法几乎无异，但简化了开发和使用过程。
 * <p>
 * 注意：如果枚举常量很多，则建议使用 Map 结构建立映射，加快搜索速度。
 *
 * @author Luo Fei
 * @date 2023/04/06
 */
@FunctionalInterface
public interface EnumSearch {

    /**
     * 搜索值
     * <p>
     * 获取搜索中用于匹配的值。
     *
     * @return 不为 null
     * @see EnumSearch#searchByValue(Class, Object)
     */
    Object searchValue();

    /**
     * 用给定的值搜索匹配的枚举常量
     * <p>
     * 匹配的定义是使用 {@code equals} 比较两个值返回 {@code true}。
     *
     * @param enumClass 实现 {@link  EnumSearch} 接口的枚举类
     * @param value     搜索中用于匹配的值（不为 null）
     * @return 匹配的枚举常量
     * @throws IllegalArgumentException 如果无匹配的搜索结果则抛出该异常。
     */
    static <E extends Enum<E> & EnumSearch, V> E searchByValue(Class<E> enumClass, V value) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E enumConstant : enumConstants) {
            if (value.equals(enumConstant.searchValue())) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("No matching enum constant for value: " + value);
    }

}






