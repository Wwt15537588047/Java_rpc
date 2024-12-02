package common.message;

import lombok.*;
import server.integration.References;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RPCRequest implements Serializable {
    //服务类名，客户端只知道接口
    private String interfaceName;
    //客户端接口注解
    private References references;
    //调用的方法名
    private String methodName;
    //参数列表
    private Object[] params;
    //参数类型
    private Class<?>[] paramsType;
}
