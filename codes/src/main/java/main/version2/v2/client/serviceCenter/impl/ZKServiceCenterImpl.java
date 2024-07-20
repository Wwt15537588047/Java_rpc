package main.version2.v2.client.serviceCenter.impl;

import lombok.extern.slf4j.Slf4j;
import main.version2.v2.client.cache.ServiceCache;
import main.version2.v2.client.serviceCenter.ServiceCenter;
import main.version2.v2.client.serviceCenter.zkWatcher.WatchZK;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZKServiceCenterImpl implements ServiceCenter {
    // curator提供的zookeeper客户端
    private final CuratorFramework client;
    // zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";
    // serviceCache
    private final ServiceCache cache;

    // 无参构造函数，负责ZK客户端的初始化，并与zookeeper服务端进行连接
    public ZKServiceCenterImpl(){
        // 指数时间重试
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(retryPolicy).namespace(ROOT_PATH).build();
        this.client.start();
        log.info("客户端与Zookeeper注册中心成功建立连接...");

        // 初始化本地缓存
        cache = new ServiceCache();
        WatchZK watchZK = new WatchZK(client, cache);
        watchZK.watchToUpdate(ROOT_PATH);
        log.info("客户端WatchZK启动，监听路径为：{}", ROOT_PATH);
    }

    /**
     * 根据服务名（接口）返回地址
     * @param serviceName
     * @return
     */
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            // 先从本地缓存中查找
            List<String> addressList = cache.getServiceFromCache(serviceName);
            log.info("从本地cache中拿取到的服务地址列表为:{}",addressList);
            //如果找不到，再去zookeeper中找
            //这种i情况基本不会发生，或者说只会出现在初始化阶段
            if(addressList == null){
                addressList = client.getChildren().forPath("/" + serviceName);
//                cache.addServiceToCache(serviceName, addressList.get(0));
            }
            // 现在默认使用第一个，后面会加负载均衡
            String string = addressList.get(0);
            return parseAddress(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 字符串解析为地址
    private InetSocketAddress parseAddress(String address){
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }

    // 地址->XXX.XXX.XXX.XXX:port字符串
    private String getServiceAddress(InetSocketAddress serviceAddress){
        return serviceAddress.getHostName() + ":" + serviceAddress.getPort();
    }
}
