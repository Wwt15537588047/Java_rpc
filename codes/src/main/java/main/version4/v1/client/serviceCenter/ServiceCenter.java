package main.version4.v1.client.serviceCenter;

import java.net.InetSocketAddress;

/**
 * 服务中心接口，
 */
public interface ServiceCenter {
    // 根据服务名查找地址
    InetSocketAddress serviceDiscovery(String serviceName);
    // 判断是否可以重试
    boolean checkRetry(String serviceName);
}
