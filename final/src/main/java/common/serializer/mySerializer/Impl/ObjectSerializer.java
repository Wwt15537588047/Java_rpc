package common.serializer.mySerializer.Impl;

import common.serializer.mySerializer.Serializer;

import java.io.*;

public class ObjectSerializer implements Serializer {
    // 使用Java io对象--->byte数组
    @Override
    public byte[] serialize(Object obj) {
        byte[] data = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // 对象输出流，用于将Java对象序列化为字节流，并将其连接到bos上
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            // 刷新ObjectOutputStream,确保所有缓冲区的数据都被写入到底层流中
            oos.flush();
            // 将bos内部缓冲区的数据转换为字节数组
            data = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public Object deSerialize(byte[] data, int messageType) {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public int getType() {
        return 0;
    }
}
