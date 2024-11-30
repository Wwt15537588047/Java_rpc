package ratelimit.testRateLimit;

import ratelimit.ratelimitImpl.LeakyBucketRateLimiteImpl;

import java.util.Queue;
import java.util.concurrent.*;

public class LeakyBucketTest {
    public static void main(String[] args) throws InterruptedException {
        // 创建一个定时线程，以一定时间从漏桶中消费任务
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        ExecutorService singleThread = Executors.newSingleThreadExecutor();

        // 创建漏桶算法
        LeakyBucketRateLimiteImpl rateLimit = new LeakyBucketRateLimiteImpl(20, 2);
        // 存储流量的队列
        Queue<Integer> queue = new ConcurrentLinkedQueue<>();
        // 模拟请求，不确定速率进水「新增任务」
        singleThread.execute(()->{
            int count = 0;
            while (true){
                boolean flag = rateLimit.tryAquire();
                if(flag){
                    queue.offer(count);
                    System.out.println("任务数量count : " + count + "流量被放行...");
                }else{
                    System.out.println("任务数量count : " + count +  "流量被限制...");
                }
                try {
                    // 将等待时间间隔设置为一个较合理的值,避免极端情况如0ms的存在
                    Thread.sleep(100 + (long) (Math.random() * 900));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++count;
            }
        });

        // 模拟请求被处理，固定速率漏水
        scheduledExecutorService.scheduleAtFixedRate(()->{
           if(!queue.isEmpty()){
               System.out.println("定时任务线程池进行任务处理：" + queue.poll() + " 被处理");
           }
           // 每50ms漏水，模拟更平滑的漏水,漏桶算法的要求是每秒漏出固定速率的水,所以将时间间隔缩小,模拟更加平滑的漏水过程
        }, 0, 50, TimeUnit.MILLISECONDS);

        // 保证主线程不会退出
        while (true){
            Thread.sleep(1000);
        }
    }
}
