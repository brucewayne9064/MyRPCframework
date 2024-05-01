package org.example.myrpcframework.rpcFrameworkCommon.utils;


//String工具类
public class StringUtil {

    //检查字符串是否为空
    public static boolean isBlank(String s) {
        //如果是null或者长度为0，返回真
        if (s == null || s.length() == 0) {
            return true;
        }
        //如果整个字符串里面有一个不是空格，返回假
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        //全是空格，返回真
        return true;
    }
}
