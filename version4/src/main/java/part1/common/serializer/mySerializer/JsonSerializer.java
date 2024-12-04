package part1.common.serializer.mySerializer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import io.protostuff.Rpc;
import lombok.extern.slf4j.Slf4j;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcRequestSerializer;
import part1.common.Message.RpcResponse;
import part1.common.util.RequestTransForm;

/**
 * 使用 FastJSON 2 进行序列化和反序列化
 * @version 1.0
 * @create 2024/6/2 22:31
 */
@Slf4j
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        byte[] data = new byte[0];
        try {
            // FastJSON 2 中直接使用 JSON.toJSONBytes
            data = JSON.toJSONBytes(obj);
        } catch (RuntimeException e) {
            log.error("序列化失败: {}", e.getMessage(), e);
        }
        return data;
    }

    @Override
    public Object deserialize(byte[] data, int messageType) {
        Object obj = null;
        try {
            switch (messageType) {
                case 0: {
                    // 反序列化 RpcRequest 对象
                    RpcRequest req = new RpcRequest();
                    RpcRequestSerializer requestSerializer = JSON.parseObject(data, RpcRequestSerializer.class, JSONReader.Feature.SupportClassForName);
                    Object[] objects = requestSerializer.getParams() == null ? new Object[0] : new Object[requestSerializer.getParams().length];

                    // 处理 params 中的每个参数，确保类型匹配
                    for (int i = 0; i < objects.length; i++) {
                        Class<?> paramType = requestSerializer.getParamsType()[i];
                        Object param = requestSerializer.getParams()[i];
                        if (param instanceof JSONObject) {
                            objects[i] = ((JSONObject) param).toJavaObject(paramType);
                        } else {
                            objects[i] = param;
                        }
                    }
                    requestSerializer.setParams(objects);
                    req = RequestTransForm.GetRequest(requestSerializer);
                    obj = req;
                    break;
                }
                case 1: {
                    // 反序列化 RpcResponse 对象
                    RpcResponse resp = JSON.parseObject(data, RpcResponse.class, JSONReader.Feature.SupportClassForName);
                    Class<?> dataType = resp.getDataType();
                    Object responseData = resp.getData();

                    if (responseData instanceof JSONObject) {
                        resp.setData(((JSONObject) responseData).toJavaObject(dataType));
                    }
                    obj = resp;
                    break;
                }
                default:
                    throw new IllegalArgumentException("不支持的消息类型: " + messageType);
            }
        } catch (RuntimeException e) {
            log.error("反序列化失败: {}", e.getMessage(), e);
        }
        return obj;
    }

    @Override
    public int getType() {
        return 1;
    }
}
