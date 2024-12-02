package client.proxy;


import client.circuitBreaker.CircuitBreaker;
import client.circuitBreaker.CircuitBreakerProvider;
import client.retry.GuavaRetry;
import client.rpcClient.RPCClient;
import client.rpcClient.impl.NettyRPCClientImpl;
import client.rpcClient.impl.SimpleSocketRPCClientImpl;
import client.serviceCenter.ServiceCenter;
import client.serviceCenter.impl.ZKServiceCenterImpl;
import common.message.RPCResponse;
import lombok.extern.slf4j.Slf4j;
import common.message.RPCRequest;
import server.integration.References;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author wwt
 * @version 1.0
 * @create 2024/2/6 16:49
 */
@Slf4j
public class ClientProxy implements InvocationHandler {

    //传入参数service接口的class对象，反射封装成一个request
    private RPCClient rpcClient;
    private ServiceCenter serviceCenter;
    private CircuitBreakerProvider circuitBreakerProvider;
    private References references;
    private Class<?> clazz;
    public ClientProxy(int choose){
        switch (choose){
            case 0:
//                rpcClient=new NettyRPCClientImpl();
                serviceCenter = new ZKServiceCenterImpl();
                rpcClient = new NettyRPCClientImpl(serviceCenter);
                circuitBreakerProvider = new CircuitBreakerProvider();
                break;
            case 1:
                rpcClient=new SimpleSocketRPCClientImpl();
                serviceCenter = new ZKServiceCenterImpl();
                circuitBreakerProvider = new CircuitBreakerProvider();
                break;
        }
    }
    public ClientProxy(){
        // 客户端代理会实例化：服务中心，rpc客户端，熔断器
        serviceCenter = new ZKServiceCenterImpl();
        rpcClient = new NettyRPCClientImpl(serviceCenter);
        circuitBreakerProvider = new CircuitBreakerProvider();
    }
    //jdk动态代理，每一次代理对象调用方法，都会经过此方法增强（反射获取request对象，socket发送到服务端）
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建request
        RPCRequest request= RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramsType(method.getParameterTypes())
                .references(references)
                .build();
        // 获取熔断器
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(request.getInterfaceName());
        log.info("服务名：{}的服务获取到的熔断器为：{}", request.getInterfaceName(), circuitBreaker);
        // 判断熔断器是否允许请求通过
        if(!circuitBreaker.allowRequest()){
            // 此处可以针对熔断器进行特殊处理,返回特殊值
            return null;
        }

        //数据传输
        RPCResponse response;
        //后续添加逻辑：为保持幂等性，只对白名单上的服务进行重试
        if(serviceCenter.checkRetry(request)){
            log.info("根据Zookeeper白名单查询客户端调用服务：{}可重试，采用重试机制实现调用...", request.getInterfaceName());
            response = new GuavaRetry().sendServiceWithRetry(request, rpcClient);
        }else{
            response = rpcClient.sendRequest(request);
        }
        //记录response的状态，上报给熔断器
        if (response.getCode() ==200){
            circuitBreaker.recordSuccess();
        }
        if (response.getCode()==500){
            circuitBreaker.recordFailure();
        }
        return response.getData();
    }
    public <T>T getProxy(Class<T> clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }

    /**
     * 获取代理对象
     * @param clazz
     * @param references
     * @return
     * @param <T>
     */
    public <T>T getProxy(Class<T> clazz, References references){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        //记录接口服务与注解信息
        log.info("客户端服务:{},注解信息:{}",clazz.getName(),references.toString());
        this.references = references;
        this.clazz = clazz;
        return (T)o;
    }
}
