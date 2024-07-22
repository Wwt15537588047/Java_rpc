package main.version4.v1.client.circuitBreaker;

import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 管理熔断器的状态，并根据请求的成功和失败的情况进行状态转移
 */
public class CircuitBreaker {
    // 当前状态
    private CircuitBreakerStatus status = CircuitBreakerStatus.CLOSED;
    private AtomicInteger failureCount = new AtomicInteger(0);
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger requestCount = new AtomicInteger(0);

    // 失败次数阈值
    private final int failureThreshold;
    // 半开启-》关闭状态的成功次数的比例
    private final double halfOpenSuccessRate;
    // 恢复时间
    private final long retryTimePeriod;
    // 上一次失败时间
    private long lastFailureTime = 0;
    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate, long retryTimePeriod){
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.retryTimePeriod = retryTimePeriod;
    }

    // 查看当前熔断器是否允许请求通过
    public synchronized boolean allowRequest(){
        long currentTime = System.currentTimeMillis();
        switch (status){
            case OPEN:
                if(currentTime - lastFailureTime > retryTimePeriod){
                    status = CircuitBreakerStatus.HALF_OPEN;
                    resetCounts();
                    return true;
                }
                return false;
            case HALF_OPEN:
                requestCount.incrementAndGet();
                return true;
            case CLOSED:
            default:
                return true;
        }
    }

    // 记录成功
    public synchronized void recordSuccess(){
        if(status == CircuitBreakerStatus.HALF_OPEN){
            successCount.incrementAndGet();
            if(successCount.get() >= halfOpenSuccessRate * requestCount.get()){
                status = CircuitBreakerStatus.CLOSED;
                resetCounts();
            }
        }else{
            resetCounts();
        }
    }
    // 记录失败
    public synchronized void recordFailure(){
        failureCount.incrementAndGet();
        lastFailureTime = System.currentTimeMillis();
        if(status == CircuitBreakerStatus.HALF_OPEN){
            status = CircuitBreakerStatus.OPEN;
            lastFailureTime = System.currentTimeMillis();
        }else if(failureCount.get() > failureThreshold){
            status = CircuitBreakerStatus.OPEN;
        }
    }

    // 重置次数
    public void resetCounts(){
        failureCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }
    // 获取状态
    public CircuitBreakerStatus getStatus(){
        return status;
    }
}

enum CircuitBreakerStatus{
    // 关闭、开启、半开启
    CLOSED, OPEN, HALF_OPEN
}
