package indi.gavin.orm;


/**
 * 取得通过泛型定义的Po类的Class实例。
 * 
 * @author Gavin
 *
 * @param <T>
 */
public interface ParameterizedPoHandler<T> {

    @SuppressWarnings("unchecked")
    default Class<T> getPoClass() {
        Class<?> poClass = PoUtils.getParameterizedPoClass(getClass(), ParameterizedPoHandler.class);
        if (poClass == null) {
            throw new BizException(BizStatus.S_BIZ_P_ERROR_INVALID_PO_CLASS);
        }
        return (Class<T>) poClass;
    }
}
