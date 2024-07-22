package main.version4.v1.server.rateLimit;

/**
 * 限流算法接口类
 */
public interface RateLimit {
    // 获取访问许可
    boolean getToken();
}
