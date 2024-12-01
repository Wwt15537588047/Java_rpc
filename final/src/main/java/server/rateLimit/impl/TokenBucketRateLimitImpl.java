package server.rateLimit.impl;

import lombok.extern.slf4j.Slf4j;
import server.rateLimit.RateLimit;


/**
 * 令牌桶限流算法实现
 * CAPACITY为令牌桶的最大容量，curCAPACITY为当前令牌桶中令牌的数量，timeStamp为上一次请求获取令牌的时间，
 * 我们在这里并没有实现计数器每秒产生多少令牌放入容器中，而是记住了上一次请求到来的时间，和这次请求之间的时间差值
 * 进一步根据RATE计算出这段时间能够产生的令牌数量，取min(CAPACITY, CURCAPAITY)。
 */
@Slf4j
public class TokenBucketRateLimitImpl implements RateLimit {
    // 令牌产生速率(单位ms)
    private static int RATE;
    // 桶容量
    private static int CAPACITY;
    // 当前桶容量
    private volatile static int curCapacity;
    // 时间戳
    private volatile long timeStamp = System.currentTimeMillis();
    public TokenBucketRateLimitImpl(int rate, int capacity){
        RATE = rate;
        CAPACITY = capacity;
        // 初始化时，服务当前容量等于桶能够承受的最大容量
        curCapacity = CAPACITY;
    }

    // 令牌桶获取Token需要加锁，多线程并发安全
    @Override
    public synchronized boolean getToken() {
        // 如果当前桶有剩余，直接返回
        if(curCapacity > 0){
            curCapacity--;
            return true;
        }
        // 如果桶无剩余
        long currentTime = System.currentTimeMillis();
        // 如果距离上一次的请求的时间大于RATE的时间
        if(currentTime - timeStamp >= RATE){
            //计算这段时间间隔中生成的令牌，如果>2,桶容量加上（计算的令牌-1）
            //如果==1，就不做操作（因为这一次操作要消耗一个令牌）
            if((currentTime - timeStamp) / RATE >= 2){
                curCapacity += (int) (currentTime - timeStamp) / RATE - 1;
            }
            //保持桶内令牌容量<=CAPACITY
            if(curCapacity > CAPACITY){
                curCapacity = CAPACITY;
            }
            //刷新时间戳为本次请求
            timeStamp = currentTime;
            return true;
        }
        return false;
    }
}
