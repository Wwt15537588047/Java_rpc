package main.version3.v1.client.serviceCenter;

import java.net.InetSocketAddress;

/**
 * 服务中心接口，根据服务名查找地址
 */
public interface ServiceCenter {
    InetSocketAddress serviceDiscovery(String serviceName);
}
