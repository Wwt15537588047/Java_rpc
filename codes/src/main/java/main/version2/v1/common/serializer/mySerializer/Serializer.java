package main.version2.v1.common.serializer.mySerializer;

import main.version2.v1.common.message.MessageType;
import main.version2.v1.common.serializer.mySerializer.Impl.JsonSerializer;
import main.version2.v1.common.serializer.mySerializer.Impl.ObjectSerializer;

// 定义序列化接口，可以有不同的实现，如：JSON、Protobuf
public interface Serializer {
    // 把对象转换为字节数组
    byte[] serialize(Object obj);
    // 从字节数组反序列化成消息, 使用java自带序列化方式不用messageType也能得到相应的对象（序列化字节数组里包含类信息）
    // 其它方式需指定消息格式，再根据message转化成相应的对象
    Object deSerialize(byte[] data, int messageType);
    // 返回指定的序列化器，是哪个
    int getType();

    /**
     * 用于解码时根据类型得到对应的序列化器，进一步进行反序列化
     * @param code
     * @return
     */
    static Serializer getSerializerByCode(int code){
        switch (code){
            case 0:
                return new ObjectSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
