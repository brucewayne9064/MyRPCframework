package org.example.proxy.dynamicProxy.jdkDynamicProxy;

public class MailServiceImpl implements MailService {
    @Override
    public String send(String message) {
        System.out.println("sned message: " + message);
        return message;
    }

}
