package main.version3.v1.server.provider;

import main.version3.v1.server.serviceRegister.ServiceRegister;
import main.version3.v1.server.serviceRegister.impl.ZKServiceRegisterImpl;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

// 本地服务存放器
public class ServiceProvider {

    // 集合中存放服务的实例
    private Map<String, Object> interfaceProvider;
    private String host;
    private int port;
    // 注册服务类
    private ServiceRegister serviceRegister;
    public ServiceProvider(String host, int port){
        this.host =host;
        this.port =port;
        this.interfaceProvider = new HashMap<String, Object>();
        this.serviceRegister = new ZKServiceRegisterImpl();
    }

    // 本地注册服务
    public void providerServiceInterface(Object service){
        // 获取ServiceImpl的所有接口实现集合
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> clazz : interfaces) {
            // 本机的映射表
            interfaceProvider.put(clazz.getName(), service);
            // 在注册中心中中注册服务
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port));
        }
    }
    // 获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}
