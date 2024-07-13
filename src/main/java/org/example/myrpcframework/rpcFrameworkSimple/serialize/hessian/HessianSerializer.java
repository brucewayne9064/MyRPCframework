package org.example.myrpcframework.rpcFrameworkSimple.serialize.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import org.example.myrpcframework.rpcFrameworkSimple.serialize.Serializer;
import org.example.netty.kyro.serialize.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


//  Hessian is a dynamically-typed, binary serialization and Web Services protocol
//  designed for object-oriented transmission.
public class HessianSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            HessianInput hessianInput = new HessianInput(byteArrayInputStream);
            Object o = hessianInput.readObject();

            return clazz.cast(o);

        } catch (Exception e) {
            throw new SerializeException("Deserialization failed");
        }
    }
}
