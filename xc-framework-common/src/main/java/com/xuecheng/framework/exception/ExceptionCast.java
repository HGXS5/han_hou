package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;


/**
 * @author Administrator
 * @version 1.0
 * @ClassName ExceptionCast
 * @create 2020-08-28-10:50
 **/
public class ExceptionCast {
    //使用此静态方法抛出自定义异常
    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
