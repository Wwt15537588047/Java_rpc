package main.version3.v1.client.serviceCenter.balance.impl;

import lombok.extern.slf4j.Slf4j;
import main.version3.v1.client.serviceCenter.balance.LoadBalance;

import java.util.*;

/**
 * 一致性哈希负载均衡算法实现
 */
@Slf4j
public class ConsistencyHashLoadBalanceImpl implements LoadBalance {
    // 虚拟节点的个数
    private static final int VIRTUAL_NUM = 5;

    // 虚拟节点分配，key是hash值，value是虚拟节点服务器名称
    private static SortedMap<Integer, String> shards = new TreeMap<Integer, String>();

    // 真实节点列表
    private static List<String> realNodes = new LinkedList<String>();

    //模拟初始服务器
    private static String[] servers =null;

    private static void init(List<String> serviceList) {
        for (String server :serviceList) {
            realNodes.add(server);
            System.out.println("真实节点[" + server + "] 被添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = server + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
            }
        }
    }
    /**
     * 获取被分配的节点名
     *
     * @param node
     * @return
     */
    public static String getServer(String node,List<String> serviceList) {
        init(serviceList);
        int hash = getHash(node);
        Integer key = null;
        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()) {
            // subMap为空，说明hash已经超出了哈希环的最大值，则选择环上的最后一个节点
            key = shards.lastKey();
        } else {
            // subMap不为空，说明存在比hash更大的节点，则选择第一个节点(最接近hash)
            key = subMap.firstKey();
        }
        // 获取目标虚拟节点
        String virtualNode = shards.get(key);
        // 根据虚拟节点获取到真实节点，并返回
        String readNode = virtualNode.substring(0, virtualNode.indexOf("&&"));
        System.out.println("真实节点为："+ readNode + ",虚拟节点为：" + virtualNode + "的节点被选择了...");
        return readNode;
    }

    /**
     * 添加节点
     *
     * @param node
     */
    public  void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            System.out.println("真实节点[" + node + "] 上线添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
            }
        }
    }

    /**
     * 删除节点
     *
     * @param node
     */
    public  void delNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            System.out.println("真实节点[" + node + "] 下线移除");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被移除");
            }
        }
    }

    /**
     * FNV1_32_HASH算法
     */
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    @Override
    public String balance(List<String> addressList) {
        String random= UUID.randomUUID().toString();
        return getServer(random,addressList);
    }
}
