package org.example.proxy.staticProxy;

public class Main {
    public static void main(String[] args) {
        SmsService smsService = new SmsServiceImpl();
        SmsProxy smsProxy = new SmsProxy(smsService);
        smsProxy.send("hello java");

        MailService mailService = new MailServiceImpl();
        MailProxy mailProxy = new MailProxy(mailService);
        mailProxy.send("my mail is xxx@gmail.com");
    }
}
