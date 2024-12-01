package common.serializer.mySerializer.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.message.RPCRequest;
import common.message.RPCResponse;
import common.serializer.mySerializer.Serializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        byte[] data = JSONObject.toJSONBytes(obj);
        return data;
    }

    @Override
    public Object deSerialize(byte[] data, int messageType) {
        Object obj = null;
        switch (messageType){
            case 0:
                RPCRequest req = JSON.parseObject(data, RPCRequest.class);
                Object[] objects = new Object[req.getParams().length];
                // 把json字串转化成对应的对象， fastjson可以读出基本数据类型，不用转化
                // 对转换后的request中的params属性逐个进行类型判断
                for (int i = 0; i < objects.length; i++) {
                    Class<?> paramType = req.getParamsType()[i];
                    // 判断每个对象类型是否和paramType中的对象类型一致
                    if(!paramType.isAssignableFrom(req.getParams()[i].getClass())){
                        objects[i] = JSONObject.toJavaObject((JSONObject)req.getParams()[i], req.getParamsType()[i]);
                    }else{
                        // 一致，直接赋值
                        objects[i] = req.getParams()[i];
                    }
                }
                req.setParams(objects);
                obj = req;
                break;
            case 1:
                RPCResponse resp = JSON.parseObject(data, RPCResponse.class);
                Class<?> dataType = resp.getDataType();
                if(!dataType.isAssignableFrom(resp.getData().getClass())){
                    resp.setData(JSONObject.toJavaObject((JSONObject) resp.getData(), resp.getDataType()));
                }
                obj = resp;
                break;
            default:
                log.info("暂不支持此种类型的消息...");
                throw new RuntimeException();
        }
        return obj;
    }

    @Override
    public int getType() {
        return 1;
    }
}
