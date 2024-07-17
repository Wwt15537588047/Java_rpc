package main.version1.v1.server.server.provider;

import java.util.HashMap;
import java.util.Map;

// 本地服务提供者
public class ServiceProvider {
    private Map<String, Object> interfaceProvider;

    public ServiceProvider(){
        this.interfaceProvider = new HashMap<String, Object>();
    }

    // 本地注册服务
    public void providerServiceInterface(Object service){
//        String serviceName = service.getClass().getName();
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> clazz : interfaces) {
            interfaceProvider.put(clazz.getName(), service);
        }
    }
    // 获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}
