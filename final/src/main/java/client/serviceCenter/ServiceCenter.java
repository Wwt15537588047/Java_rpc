package client.serviceCenter;

import common.message.RPCRequest;

import java.net.InetSocketAddress;

/**
 * 服务中心接口，
 */
public interface ServiceCenter {
    // 根据服务名查找地址
    InetSocketAddress serviceDiscovery(String serviceName);
    // 判断是否可以重试
    boolean checkRetry(RPCRequest rpcRequest);
}
