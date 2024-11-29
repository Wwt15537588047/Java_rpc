package main.version2.v1.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RPCResponse implements Serializable {
    //状态码
    private int code;
    //状态信息
    private String message;
    // 加入传输数据的类型，以便在自定义序列化器中解析
    private Class<?> dataType;
    //具体数据
    /**
     * 为什么这一步需要在响应参数里面加入数据类型的校验，参考请求参数，每个参数都应该有对应的类型。
     * 当反序列化时，根据数据类型判断当前数据是否为此中类型，如果不是此种类型，此时需要转换到指定类型。
     */
    private Object data;
    //构造成功信息
    public static RPCResponse sussess(Object data){
        return RPCResponse.builder().code(200).dataType(data.getClass()).data(data).build();
    }
    //构造失败信息
    public static RPCResponse fail(){
        return RPCResponse.builder().code(500).message("服务器发生错误").build();
    }
}
