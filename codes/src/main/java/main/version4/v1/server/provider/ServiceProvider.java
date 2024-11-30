package main.version4.v1.server.provider;

import main.version4.v1.server.rateLimit.provider.RateLimitProvider;
import main.version4.v1.server.serviceRegister.ServiceRegister;
import main.version4.v1.server.serviceRegister.impl.ZKServiceRegisterImpl;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 和Server一同使用的，绑定某种通信，如Netty，提供需要的服务组件，如:ZK服务注册、限流器...
 */
public class ServiceProvider {

    // 集合中存放服务的实例
    private Map<String, Object> interfaceProvider;
    private String host;
    private int port;
    // 注册服务类
    private ServiceRegister serviceRegister;
    // 限流器
    private RateLimitProvider rateLimitProvider;
    public ServiceProvider(String host, int port){
        this.host =host;
        this.port =port;
        this.interfaceProvider = new HashMap<String, Object>();
        this.serviceRegister = new ZKServiceRegisterImpl();
        this.rateLimitProvider = new RateLimitProvider();
    }

    // 本地注册服务
    public void providerServiceInterface(Object service, boolean canRetry){
        // 获取ServiceImpl的所有接口实现集合
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> clazz : interfaces) {
            // 本机的映射表
            interfaceProvider.put(clazz.getName(), service);
            // 在注册中心中中注册服务
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port), canRetry);
        }
    }
    // 获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

    public RateLimitProvider getRateLimitProvider(){
        return rateLimitProvider;
    }
}
