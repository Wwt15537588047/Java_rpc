package part1.common.Message;

import lombok.*;
import part1.Server.integration.References;

import java.io.Serializable;

/**
 * @version 1.0
 * @create 2024/2/1 18:30
 * 定义发送的消息格式
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcRequestSerializer implements Serializable {
    //服务类名，客户端只知道接口
    private String interfaceName;
    //客户端接口注解字段提取
    /**
     * 组号
     * @return
     */
    private String group;

    /**
     * 版本号
     * @return
     */
    private String version;

    /**
     * 负载均衡策略
     */
    private String loadBalance;
//    private References references;
    //调用的方法名
    private String methodName;
    //参数列表
    private Object[] params;
    //参数类型
    private Class<?>[] paramsType;
}
