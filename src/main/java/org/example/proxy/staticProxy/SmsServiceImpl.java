package org.example.proxy.staticProxy;

public class SmsServiceImpl implements SmsService{

    @Override
    public String send(String message) {
        System.out.println("sned message: " + message);
        return message;
    }
}
