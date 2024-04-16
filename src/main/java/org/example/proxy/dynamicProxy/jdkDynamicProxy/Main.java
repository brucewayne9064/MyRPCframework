package org.example.proxy.dynamicProxy.jdkDynamicProxy;

import org.example.proxy.staticProxy.MailService;
import org.example.proxy.staticProxy.MailServiceImpl;

public class Main {
    public static void main(String[] args) {
        SmsService smsService = new SmsServiceImpl();
        SmsService smsServiceImplProxy = (SmsService) JdkProxyFactory.getProxy(smsService);
        smsServiceImplProxy.send("hello java");

        MailService mailService = new MailServiceImpl();
        MailService mailServiceImplProxy = (MailService) JdkProxyFactory.getProxy(mailService);
        mailServiceImplProxy.send("mail is wrong");
    }
}
