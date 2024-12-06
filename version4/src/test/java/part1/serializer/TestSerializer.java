package part1.serializer;

import lombok.extern.slf4j.Slf4j;
import part1.Server.integration.References;
import part1.Server.integration.impl.ReferencesImpl;
import part1.common.Message.RpcRequest;
import part1.common.serializer.mySerializer.Serializer;
import part1.common.serializer.mySerializer.impl.*;
import part1.common.util.RequestTransForm;

import java.util.*;

/**
 * @Author wt
 * @Description 测试Protostuff序列化方式
 * @Data 2024/12/3 下午10:08
 */
@Slf4j
@References(group = "TestGroup", version = "2.0", loadBalance = "RandomLoadBalance")
public class TestSerializer {
    public static void main(String[] args) {
//        RpcRequest rpcRequest = RpcRequest.builder()
//                .interfaceName("123").methodName("wt").paramsType(new Class[]{"Hello".getClass()}).params(new Object[]{"Hello"}).
//                references(new ReferencesImpl("1.1", "1.0", "consistenceHash")).build();

        // 提取注解信息
        References references = TestSerializer.class.getAnnotation(References.class);

        // 构造 RpcRequest
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("com.example.service.TestService")
                .methodName("processData")
                .paramsType(new Class[]{
                        String.class,
                        int[].class,
                        List.class,
                        Map.class
                })
                .params(new Object[]{
                        "TestData",
                        new int[]{1, 2, 3, 4},
                        Arrays.asList("item1", "item2", "item3"),
                        new HashMap<String, Object>() {{
                            put("key1", "value1");
                            put("key2", 12345);
                            put("key3", Arrays.asList("nestedItem1", "nestedItem2"));
                        }}
                })
                .references(references) // 注解对象直接传入
                .build();
        log.info("原始对象为：{}", RequestTransForm.RequestTo(rpcRequest));
        testSerialization(new ObjectSerializer(), rpcRequest, "JDK");
        testSerialization(new ProtostuffSerializer(), rpcRequest, "Protostuff");
        testSerialization(new JsonSerializer(), rpcRequest, "JSON");
        testSerialization(new KryoSerTest(), rpcRequest, "Kryo");
        testSerialization(new HessianSerializer(), rpcRequest, "Hessian");
    }
    public static void testSerialization(Serializer serializer, RpcRequest rpcRequest, String serializerName) {
        byte[] bytes = serializer.serialize(RequestTransForm.RequestTo(rpcRequest));
        Object deserialize = serializer.deserialize(bytes, 0);
        log.info("{} 序列化后数组字节长度为:{}, 序列化数组为：{}", serializerName, bytes.length, bytes);
        log.info("{} 反序列化后得到的对象为：{}", serializerName, RequestTransForm.RequestTo((RpcRequest) deserialize));
    }
}
