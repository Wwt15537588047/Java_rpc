package client.circuitBreaker;


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
    public boolean allowRequest(){
        long currentTime = System.currentTimeMillis();
        switch (status){
            case OPEN:
                // 当前处于打开状态，超过一定时间，进入半开状态
                if(currentTime - lastFailureTime > retryTimePeriod){
                    // 细化锁的粒度，将锁从allowRequest判断中移除
                    synchronized (this){
                        if(status == CircuitBreakerStatus.OPEN){
                            status = CircuitBreakerStatus.HALF_OPEN;
                            resetCounts();
                            return true;
                        }
                    }
                }
                return false;
            case HALF_OPEN:
                // 在半开状态下，熔断器允许请求通过，并根据请求的成功率决定是否恢复到闭合状态或重新进入打开状态。
                requestCount.incrementAndGet();
                return true;
            case CLOSED:
            default:
                return true;
        }
    }

    // 记录成功
    public synchronized void recordSuccess(){
        if(status == CircuitBreakerStatus.HALF_OPEN) {
            // 半开状态下,统计成功的请求数量，当成功请求数量达到阈值后，关闭熔断器
            successCount.incrementAndGet();
            if (successCount.get() >= halfOpenSuccessRate * requestCount.get()) {
                status = CircuitBreakerStatus.CLOSED;
                resetCounts();
            }
        }else if(status == CircuitBreakerStatus.CLOSED){
            // 当熔断器关闭时,不清空数据
        }else{
            resetCounts();
        }
    }
    // 记录失败
    public synchronized void recordFailure(){
        failureCount.incrementAndGet();
        lastFailureTime = System.currentTimeMillis();
        if(status == CircuitBreakerStatus.HALF_OPEN){
            // 在半开状态下,请求再次失败,此时再次打开熔断器
            status = CircuitBreakerStatus.OPEN;
            lastFailureTime = System.currentTimeMillis();
        }else if(failureCount.get() > failureThreshold){
            // 失败次数达到阈值,熔断器进入打开状态,拒绝请求
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
