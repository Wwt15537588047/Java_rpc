package part1.common.serializer.mySerializer.impl;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcRequestSerializer;
import part1.common.Message.RpcResponse;
import part1.common.serializer.mySerializer.Serializer;
import part1.common.util.RequestTransForm;

import java.lang.reflect.Proxy;
/**
 * @Author wt
 * @Description 新增Protostuff序列化方式
 * @Data 2024/12/3 下午10:02
 */
public class ProtostuffSerializer implements Serializer {
    // 线程私有的缓冲区，避免多线程竞争
    private static final ThreadLocal<LinkedBuffer> BUFFER_THREAD_LOCAL = ThreadLocal.withInitial(() ->
            LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    @Override
    public byte[] serialize(Object obj) {
//        obj = replaceProxyWithTarget(obj); // 替换动态代理类
        if (obj == null) {
            throw new NullPointerException("序列化对象不能为空");
        }
        @SuppressWarnings("unchecked")
        Schema schema = RuntimeSchema.getSchema(obj.getClass());
        // 通过线程私有的LinkedBuffer来避免多线程竞争
        LinkedBuffer buffer = BUFFER_THREAD_LOCAL.get();
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear(); // 每次使用完清理缓存，供下次使用
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("反序列化字节数组不能为空");
        }
        Object obj;
        switch (messageType) {
            case 0: // RpcRequest 类型
                // 此时消息类型为请求类型，由于编码时已经将其转换为RpcRequestSerializer类型，所以此时反序列化时也需要中间类转换
                obj = deserialize(bytes, RpcRequestSerializer.class);
                RpcRequest rpcRequest = RequestTransForm.GetRequest((RpcRequestSerializer) obj);
                obj = rpcRequest;
                break;
            case 1: // RpcResponse 类型
                obj = deserialize(bytes, RpcResponse.class);
                break;
            default:
                throw new IllegalArgumentException("不支持的消息类型：" + messageType);
        }
        return obj;
    }

    public Object replaceProxyWithTarget(Object obj) {
        if (Proxy.isProxyClass(obj.getClass())) {
            // 代理对象处理，可以通过框架获取真实对象，如 Spring AOP 提供的 AopProxyUtils
            return org.springframework.aop.framework.AopProxyUtils.getSingletonTarget(obj);
        }
        return obj;
    }

    /**
     * 通用的反序列化方法
     */
    private <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage(); // 创建一个空对象
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema); // 填充对象
        return obj;
    }

    @Override
    public int getType() {
        return 2;
    }
}
