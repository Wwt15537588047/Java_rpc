package part1.common.serializer.mySerializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;

/**
 * @Author wt
 * @Description 新增Protostuff序列化方式
 * @Data 2024/12/3 下午10:02
 */
public class ProtostuffSerializer implements Serializer{
    // 线程私有的缓冲区，避免多线程竞争
    private static final ThreadLocal<LinkedBuffer> BUFFER_THREAD_LOCAL = ThreadLocal.withInitial(() ->
            LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    @Override
    public byte[] serialize(Object obj) {
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
                obj = deserialize(bytes, RpcRequest.class);
                break;
            case 1: // RpcResponse 类型
                obj = deserialize(bytes, RpcResponse.class);
                break;
            default:
                throw new IllegalArgumentException("不支持的消息类型：" + messageType);
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
