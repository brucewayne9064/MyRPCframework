package org.example.myrpcframework.rpcFrameworkCommon.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

//读取属性文件（通常以.properties为后缀）的工具类

@Slf4j
public class PropertiesFileUtil {
    private PropertiesFileUtil() {

    }

    public static Properties readPropertiesFile(String fileName) {
        //获取当前线程的类加载器根路径的url
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        //用来存储配置文件的路径
        String rpcConfigPath = "";
        //如果获取到的URL不为null，则将URL的路径与传入的文件名拼接，形成完整的配置文件路径
        if (url != null) {
            rpcConfigPath = url.getPath() + fileName;
        }
        Properties properties = null;  // 创建一个Properties对象
        try(InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(rpcConfigPath), "UTF-8")){
            properties = new Properties();
            properties.load(inputStreamReader);
        }catch(IOException e){
            log.error("occur exception when read properties file [{}]", fileName);
        }
        //返回读取到的properties
        return properties;
    }
}
