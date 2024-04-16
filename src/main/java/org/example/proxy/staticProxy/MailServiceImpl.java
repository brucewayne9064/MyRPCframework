package org.example.proxy.staticProxy;

public class MailServiceImpl implements MailService{
    @Override
    public String send(String message) {
        System.out.println("sned message: " + message);
        return message;
    }

}
