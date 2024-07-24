package test.ratelimit.ratelimitImpl;

import test.ratelimit.RateLimit;

/**
 * 判断漏桶算法是否能够继续添加容量
 * 以上代码中只是对流量是否会被抛弃进行校验，
 * 即tryAcquire返回true表示漏桶未满，否则表示漏桶已满丢弃请求。
 * 想要以恒定的速率漏出流量，通常还应配合一个FIFO队列来实现，
 * 当tryAcquire返回true时，将请求入队，然后再以固定频率从队列中取出请求进行处理。
 */
public class LeakyBucketRateLimiteImpl implements RateLimit {
    // 桶的容量
    private final int capacity;
    // 漏出速率
    private final int permitsPerSecond;
    // 剩余水量
    private long leftWater;
    // 上次注入时间
    private long lastTimeStamp = System.currentTimeMillis();

    public LeakyBucketRateLimiteImpl(int capacity, int permitsPerSecond){
        this.capacity = capacity;
        this.permitsPerSecond = permitsPerSecond;
    }

    @Override
    public boolean tryAquire() {
        // 计算剩余水量
        long now = System.currentTimeMillis();
        long timeGap = now - lastTimeStamp;
        lastTimeStamp = now;
        leftWater = Math.max(0, leftWater - timeGap * permitsPerSecond);
        // 如果容量未满，则放行；否则限流
        if(leftWater < capacity){
            leftWater += 1;
            return true;
        }
        return false;
    }
}
