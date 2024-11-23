package concurrentPrint;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 使用ReentrantLock和Condition实现多线程交替打印
public class thread_print_reentrantlock {
    public static volatile int a = 0;
    private static final Lock lock = new ReentrantLock();
    private static final Condition conditionA = lock.newCondition();
    private static final Condition conditionB = lock.newCondition();
    private static final Condition conditionC = lock.newCondition();
    public static void main(String[] args) {
        new Thread(()->printLock(0,conditionA, conditionB),"A").start();
        new Thread(()->printLock(1,conditionB, conditionC),"B").start();
        new Thread(()->printLock(2,conditionC, conditionA),"C").start();
    }
    public static void printLock(int target, Condition currentCond, Condition nextCond){
        while(true){
            lock.lock();
            try {
                while (a % 3 != target){
                    try {
                        currentCond.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(Thread.currentThread().getName() + "_" + a % 3);
                a++;
                // 唤醒下一个线程
                nextCond.signal();
            }finally {
                lock.unlock();
            }
        }
    }
}
