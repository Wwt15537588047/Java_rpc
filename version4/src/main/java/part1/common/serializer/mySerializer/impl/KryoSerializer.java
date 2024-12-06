package part1.common.serializer.mySerializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import part1.common.Message.RpcRequestSerializer;
import part1.common.Message.RpcResponse;
import part1.common.serializer.mySerializer.Serializer;
import part1.common.util.RequestTransForm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Author wt
 * @Description 增加kryo序列化方式
 * @Data 2024/12/6 下午4:58
 */
@Slf4j
public class KryoSerializer implements Serializer {

    // kryo 线程不安全，所以使用 ThreadLocal 保存 kryo 对象
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        /**
         * Kryo要求所有序列化的类都必须被注册，内部Object[]、Class<?>[]并不会被注册到Kryo，需要显示注册
         */
        Kryo kryo = new Kryo();
        kryo.register(Object[].class);
        kryo.register(Class.class);
        kryo.register(Class[].class);
        kryo.register(RpcRequestSerializer.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        try{
            if(obj == null){
                throw new NullPointerException("序列化对象不能为空.");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Output output = new Output(baos);
            Kryo kryo = kryoThreadLocal.get();
            // 将对象序列化为Byte数组
            kryo.writeObject(output, obj);
//            kryoThreadLocal.remove();
            return output.toBytes();
        }catch (RuntimeException e){
//            e.printStackTrace();
            log.info("Kryo序列化失败,失败消息为e：{}", e.toString());
        }
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        try{
            if (bytes == null || bytes.length == 0) {
                throw new IllegalArgumentException("反序列化字节数组不能为空");
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Input input = new Input(bais);
            Kryo kryo = kryoThreadLocal.get();
            switch (messageType){
                case 0:
                    obj = RequestTransForm.GetRequest(kryo.readObject(input, RpcRequestSerializer.class));
                    break;
                case 1:
                    obj = kryo.readObject(input, RpcResponse.class);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的消息类型：" + messageType);
            }

        }catch (RuntimeException e){
            log.info("Kryo反序列化失败, 失败消息为 e :{}", e.toString());
        }
        return obj;
    }

    @Override
    public int getType() {
        return 3;
    }
}
