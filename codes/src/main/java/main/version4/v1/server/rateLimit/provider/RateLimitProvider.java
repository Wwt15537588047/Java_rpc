package main.version4.v1.server.rateLimit.provider;

import lombok.extern.slf4j.Slf4j;
import main.version4.v1.server.rateLimit.RateLimit;
import main.version4.v1.server.rateLimit.impl.TokenBucketRateLimitImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * 限流算法提供者，获取服务对应的限流器
 */
@Slf4j
public class RateLimitProvider {
    private Map<String, RateLimit> rateLimitMap = new HashMap<>();

    public RateLimit getRateLimit(String interfaceName){
        if(!rateLimitMap.containsKey(interfaceName)){
            RateLimit rateLimit  = new TokenBucketRateLimitImpl(100, 10);
            rateLimitMap.put(interfaceName, rateLimit);
            log.info("服务名：{}获取到了：{}限流器...", interfaceName, rateLimit);
            return rateLimit;
        }else{
            log.info("服务名：{}获取到了：{}限流器...",interfaceName, rateLimitMap.get(interfaceName));
            return rateLimitMap.get(interfaceName);
        }
    }
}
