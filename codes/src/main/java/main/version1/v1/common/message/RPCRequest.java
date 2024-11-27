package main.version1.v1.common.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Data
@Builder
public class RPCRequest implements Serializable {
    /**
     * 为什么请求信息中的服务器类名定义为接口名,使用动态代理技术时,外部给定的信息是接口名.
     */
    //服务类名，客户端只知道接口
    private String interfaceName;
    //调用的方法名
    private String methodName;
    //参数列表
    private Object[] params;
    //参数类型
    private Class<?>[] paramsType;
}
