package part1.Client.serviceCenter.balance;

import part1.Client.serviceCenter.balance.impl.ConsistenceHashBalance;
import part1.Client.serviceCenter.balance.impl.RandomLoadBalance;
import part1.Client.serviceCenter.balance.impl.RoundLoadBalance;

import java.util.List;

/**
 * @version 1.0
 * @create 2024/6/19 21:00
 * 给服务地址列表，根据不同的负载均衡策略选择一个
 */
public interface LoadBalance {
    String balance(List<String> addressList);
    void addNode(String node) ;
    void delNode(String node);

    static LoadBalance getLoadBalance(String LoadBalanceName){
        switch (LoadBalanceName){
            case "ConsistenceHash":
                return new ConsistenceHashBalance();
            case "Round":
                return new RoundLoadBalance();
            case "Random":
                return new RandomLoadBalance();
            default:
                return null;
        }
    }
}