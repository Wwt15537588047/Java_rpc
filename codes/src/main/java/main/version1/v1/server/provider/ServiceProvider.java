package main.version1.v1.server.provider;

import java.util.HashMap;
import java.util.Map;

// 本地服务存放器
public class ServiceProvider {
    // 集合中存放服务的实例
    private Map<String, Object> interfaceProvider;

    public ServiceProvider(){
        this.interfaceProvider = new HashMap<String, Object>();
    }

    // 本地注册服务
    public void providerServiceInterface(Object service){
        // 获取ServiceImpl的所有接口实现集合
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
