package main.version3.v1.client.serviceCenter.balance.impl;

import lombok.extern.slf4j.Slf4j;
import main.version3.v1.client.serviceCenter.balance.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡算法
 */
@Slf4j
public class RandomLoadBalanceImpl implements LoadBalance {
    @Override
    public String balance(List<String> addressList) {
        Random random = new Random();
        int idx = random.nextInt(addressList.size());
        log.info("随机负载均衡算法选择了第：{}个服务的地址...",idx);
        return addressList.get(idx);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
