package main.version1.v3.client.serviceCenter.impl;

import lombok.extern.slf4j.Slf4j;
import main.version1.v3.client.serviceCenter.ServiceCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Zookeeper注册中心的实现
 */
@Slf4j
public class ZKServiceCenterImpl implements ServiceCenter {
    // curator提供的zookeeper客户端
    private CuratorFramework client;
    // zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";

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
    }

    /**
     * 根据服务名（接口）返回地址
     * @param serviceName
     * @return
     */
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            List<String> strings = client.getChildren().forPath("/" + serviceName);
            // 现在默认使用第一个，后面会加负载均衡
            String string = strings.get(0);
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
