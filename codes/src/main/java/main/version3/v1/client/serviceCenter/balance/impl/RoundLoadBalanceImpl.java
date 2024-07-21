package main.version3.v1.client.serviceCenter.balance.impl;

import lombok.extern.slf4j.Slf4j;
import main.version3.v1.client.serviceCenter.balance.LoadBalance;

import java.util.List;

/**
 * 轮询负载均衡算法实现
 */
@Slf4j
public class RoundLoadBalanceImpl implements LoadBalance {
    private int choose = -1;
    @Override
    public String balance(List<String> addressList) {
        choose++;
        choose = choose % addressList.size();
        log.info("轮询负载均衡算法选择了第：{}个服务的地址...",choose);
        return addressList.get(choose);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
