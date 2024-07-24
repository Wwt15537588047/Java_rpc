package test.ratelimit.testRateLimit;

import test.ratelimit.ratelimitImpl.LeakyBucketRateLimiteImpl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeakyBucketTest {
    public static void main(String[] args) throws InterruptedException {
        // 创建一个定时线程，以一定时间从漏桶中消费任务
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        ExecutorService singleThread = Executors.newSingleThreadExecutor();

        // 创建漏桶算法
        LeakyBucketRateLimiteImpl rateLimite = new LeakyBucketRateLimiteImpl(20, 20);
        // 存储流量的队列
        Queue<Integer> queue = new LinkedList<>();
        // 模拟请求，不确定速率进水
        singleThread.execute(()->{
            int count = 0;
            while (true){
                boolean flag = rateLimite.tryAquire();
                if(flag){
                    queue.offer(count);
                    System.out.println(count + "流量被放行...");
                }else{
                    System.out.println("流量被限制...");
                }
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++count;
            }
        });

        // 模拟请求被处理，固定速率漏水
        scheduledExecutorService.scheduleAtFixedRate(()->{
           if(!queue.isEmpty()){
               System.out.println(queue.poll() + "被处理");
           }
        }, 0, 100, TimeUnit.MILLISECONDS);

        // 保证主线程不会退出
        while (true){
            Thread.sleep(1000);
        }
    }
}
