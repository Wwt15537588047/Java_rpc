package main.version3.v2.client.proxy;


import lombok.extern.slf4j.Slf4j;
import main.version3.v2.client.retry.GuavaRetry;
import main.version3.v2.client.rpcClient.RPCClient;
import main.version3.v2.client.rpcClient.impl.NettyRPCClientImpl;
import main.version3.v2.client.rpcClient.impl.SimpleSocketRPCClientImpl;
import main.version3.v2.client.serviceCenter.ServiceCenter;
import main.version3.v2.client.serviceCenter.impl.ZKServiceCenterImpl;
import main.version3.v2.common.message.RPCRequest;
import main.version3.v2.common.message.RPCResponse;

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
    public ClientProxy(int choose){
        switch (choose){
            case 0:
                rpcClient=new NettyRPCClientImpl();
                serviceCenter = new ZKServiceCenterImpl();
                break;
            case 1:
                rpcClient=new SimpleSocketRPCClientImpl();
                serviceCenter = new ZKServiceCenterImpl();
                break;
        }
    }
    public ClientProxy(){
        rpcClient=new NettyRPCClientImpl();
        serviceCenter = new ZKServiceCenterImpl();
    }
    //jdk动态代理，每一次代理对象调用方法，都会经过此方法增强（反射获取request对象，socket发送到服务端）
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建request
        RPCRequest request= RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramsType(method.getParameterTypes()).build();
        //数据传输
        RPCResponse response;
        if(serviceCenter.checkRetry(request.getInterfaceName())){
            log.info("根据Zookeeper白名单查询客户端调用服务：{}可重试，采用重试机制实现调用...", request.getInterfaceName());
            response = new GuavaRetry().sendServiceWithRetry(request, rpcClient);
        }else{
            response = rpcClient.sendRequest(request);
        }
        return response.getData();
    }
    public <T>T getProxy(Class<T> clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
