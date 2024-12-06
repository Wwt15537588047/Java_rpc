package part1.common.serializer.mySerializer.impl;


import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import lombok.extern.slf4j.Slf4j;
import part1.common.Message.RpcRequestSerializer;
import part1.common.Message.RpcResponse;
import part1.common.serializer.mySerializer.Serializer;
import part1.common.util.RequestTransForm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * @Author wt
 * @Description 新增Hessian序列化、反序列
 * @Data 2024/12/6 下午5:57
 */
@Slf4j
public class HessianSerializer implements Serializer {
    // kryo 线程不安全，所以使用 ThreadLocal 保存 kryo 对象
    @Override
    public byte[] serialize(Object obj) {
        try{
            if(obj == null){
                throw new NullPointerException("序列化对象不能为空.");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HessianSerializerOutput hso = new HessianSerializerOutput(baos);
            hso.writeObject(obj);
            hso.flush();
            return baos.toByteArray();
        }catch (RuntimeException e){
            log.info("Hessian序列化失败,失败消息为e：{}", e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            HessianSerializerInput hsi = new HessianSerializerInput(bis);

            switch (messageType){
                case 0:
                     return RequestTransForm.GetRequest((RpcRequestSerializer) hsi.readObject());
                case 1:
                    return (RpcResponse)hsi.readObject();
                default:
                    throw new IllegalArgumentException("不支持的消息类型：" + messageType);
            }
        }catch (RuntimeException e){
            log.info("Hessian反序列化失败, 失败消息为 e :{}", e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    @Override
    public int getType() {
        return 4;
    }
}
