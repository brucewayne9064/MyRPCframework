package org.example.myrpcframework.rpcFrameworkCommon.utils;

public class RuntimeUtil {

    /**
     * 获取CPU的核心数
     *
     * @return cpu的核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static void main(String[] args) {
        System.out.println(cpus());
    }
}
