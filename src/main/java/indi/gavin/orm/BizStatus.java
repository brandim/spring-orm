package indi.gavin.orm;

/**
 * 异常状态码定义。
 * 
 * 约定： 
 * 1. 异常代码由8位数字组成，前4位数字表示子系统，后4位由子系统自定义 
 * 2. 异常名以S开始，代表“STATUS” 
 * 3. 异常名最后一个数字表示该异常显示信息时需要几个参数，如“1”表示需要一个参数；没有数字表示不需要参数。
 */
public interface BizStatus {
    /////////////////////////////////////////////////
    //全局返回值
    /////////////////////////////////////////////////

    final int S_OK = 200;
    final int S_ERROR_UNDEFINED = 999;

    final int S_NOT_FOUND = 404;

    final int S_INVALID_REQUEST = 601;
    final int S_INVALID_PARAMETERS = 602;

    //非预期的SQL返回值
    final int S_SQL_UNEXPECTED_CODE = 701;

    final int S_UNSUPPORTED = 801;
    final int S_NOTIMPEMENTED = 802;

    final int S_BIZ_CONNECTIONFAILED = 901;
    final int S_BIZ_SERVER_ERROR = 902; //biz服务端访问异常

    //
    final int S_FIRST_COMPONENT_STATUS = 10000000;

    /////////////////////////////////////////////////
    //工具类错误
    /////////////////////////////////////////////////

    /**
     * 为对象属性赋值时失败。 参数0：属性名 参数1：属性值
     */
    final int S_COMMON_SET_POPERTY_FAILED_2 = 10000001;

    /**
     * 读取对象属性失败。 参数0：属性名
     */
    final int S_COMMON_GET_POPERTY_FAILED_1 = 10000002;

    /////////////////////////////////////////////////
    //persistence层错误
    /////////////////////////////////////////////////

    final int S_PERSISTENCE_NOT_ROWMAPPER_FOUND = 10010001;

    /////////////////////////////////////////////////
    // 编码模块错误码
    /////////////////////////////////////////////////

    final int S_CODE_CREATE_CONFLICT = 20010001;
    final int S_CODE_UPDATE_NO_SUPPORT = 20010002;

    final int S_BIZ_P_FIRST = 20010001;

    //////////////////////////////////////
    //持久化层错误代码
    /////////////////////////////////////
    
    /**
     * 错误：insert/update/delete同时操作多个表
     */
    final int S_BIZ_P_ERROR_UPDATE_MULTI_TABLE = S_BIZ_P_FIRST + 1;

    /**
     * 错误：错误的PO类
     */
    final int S_BIZ_P_ERROR_INVALID_PO_CLASS = S_BIZ_P_FIRST + 2;

    /**
     * 错误：错误的PO对象
     */
    final int S_BIZ_P_ERROR_INVALID_PO_OBJECT = S_BIZ_P_FIRST + 3;

    /**
     * 错误：指定了错误的PO属性
     */
    final int S_BIZ_P_ERROR_INVALID_PO_FIELD = S_BIZ_P_FIRST +4;

    /**
     * 错误：在需要指定PO属性时提供了空属性
     */
    final int S_BIZ_P_ERROR_NULL_PO_FIELDS = S_BIZ_P_FIRST + 5;
    
}
