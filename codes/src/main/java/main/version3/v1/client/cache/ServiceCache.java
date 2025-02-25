package main.version3.v1.client.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建服务端缓存，每次找寻服务端地址时，先从本地缓存中查找，如果本地缓存中不存在，去zookeeper注册中心查找
 * 更新24-11-29上面的说法是不对的，本地缓存的实现机制，使用ZK的事件监听机制，客户端注册ZKWatcher到注册中心，同时将ZKWatcher保存到
 * 客户端ZKManager对象中，当ZK服务端监听的节点状态发生变化时，服务端会通知客户端，随后客户端从ZKManager中找到对应的ZKWatcher对象，进行对应的处理
 */
@Slf4j
public class ServiceCache {
    // key：serviceName 服务名
    // value: addressList 服务提供者列表
    private static final Map<String, List<String>> cache = new HashMap<>();
    // 添加服务
    public void addServiceToCache(String serviceName, String address){
        if(cache.containsKey(serviceName)){
            List<String> addressList = cache.get(serviceName);
            addressList.add(address);
//            log.info("将服务名：{}和地址为：{}的服务添加到了本地缓存中...",serviceName,address);
        }else{
            List<String> addressList = new ArrayList<>();
            addressList.add(address);
            cache.put(serviceName, addressList);

        }
        log.info("将服务名：{}和地址为：{}的服务添加到了本地缓存中...",serviceName,address);
    }
    // 修改服务地址
    public void replaceServiceAddress(String serviceName, String oldAddress, String newAddress){
        if(cache.containsKey(serviceName)){
            List<String> addressList = cache.get(serviceName);
            addressList.remove(oldAddress);
            addressList.add(newAddress);
        }else{
            log.info("服务名{}不存在于本地缓存中...",serviceName);
        }
    }
    // 从缓存中去服务地址
    public List<String> getServiceFromCache(String serviceName){
        return cache.getOrDefault(serviceName, null);
    }
    // 从缓存中删除服务
    public void deleteServiceFromCache(String serviceName, String address){
        List<String> addressList = cache.get(serviceName);
        addressList.remove(address);
        log.info("将服务名：{}中的地址为：{}的服务从缓存中删除...",serviceName, address);
    }
}
