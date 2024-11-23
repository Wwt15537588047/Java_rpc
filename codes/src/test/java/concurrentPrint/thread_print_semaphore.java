package concurrentPrint;

import java.util.concurrent.Semaphore;

// 使用信号量机制实现交替打印123
public class thread_print_semaphore {

    private static final Semaphore semaphoreA = new Semaphore(1);
    private static final Semaphore semaphoreB = new Semaphore(0);
    private static final Semaphore semaphoreC = new Semaphore(0);
    public static void main(String[] args) {
        new Thread(()->printSemaphore(0, semaphoreA, semaphoreB), "A").start();
        new Thread(()->printSemaphore(1, semaphoreB, semaphoreC), "B").start();
        new Thread(()->printSemaphore(2, semaphoreC, semaphoreA), "C").start();
    }
    public static void printSemaphore(int target, Semaphore currSema, Semaphore nextSema){
        while(true){
            try {
                currSema.acquire();
                System.out.println(Thread.currentThread().getName() + "_" + target);
                nextSema.release();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
