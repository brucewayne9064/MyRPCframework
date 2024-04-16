package org.example.proxy.staticProxy;

public class MailProxy implements MailService{
    private MailService mailService;

    public MailProxy(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public String send(String message) {
        //调用方法之前，我们可以添加自己的操作
        System.out.println("before method send()");
        mailService.send(message);
        //调用方法之后，我们同样可以添加自己的操作
        System.out.println("after method send()");
        return null;
    }
}
