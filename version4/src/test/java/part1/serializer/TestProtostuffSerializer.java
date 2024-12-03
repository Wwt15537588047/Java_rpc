package part1.serializer;

import lombok.extern.slf4j.Slf4j;
import part1.common.Message.RpcRequest;
import part1.common.serializer.mySerializer.ProtostuffSerializer;

/**
 * @Author wt
 * @Description 测试Protostuff序列化方式
 * @Data 2024/12/3 下午10:08
 */
@Slf4j
public class TestProtostuffSerializer {
    public static void main(String[] args) {

        // 构建请求消息
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("123").methodName("wt").paramsType(null).params(new Object[]{"Hello"}).build();
        // 构建Protostuff序列化器
        ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
        byte[] bytes = protostuffSerializer.serialize(rpcRequest);
        Object deserialize = protostuffSerializer.deserialize(bytes, 0);
        log.info("protostuff序列化后数组为bytes:{}, 反序列化后对象为object:{}", bytes, deserialize);
    }
}
