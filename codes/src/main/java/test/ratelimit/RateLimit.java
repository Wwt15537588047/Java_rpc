package test.ratelimit;

/**
 * 限流算法接口
 */
public interface RateLimit {
    boolean tryAquire();
}
