package concurrentPrint;

import java.util.concurrent.atomic.AtomicInteger;

// 使用CAS多线程交替打印123
public class thread_print_cas {
    private static AtomicInteger a = new AtomicInteger(0);
    public static void main(String[] args) {
        new Thread(()->printCAS(0),"A").start();
        new Thread(()->printCAS(1),"B").start();
        new Thread(()->printCAS(2),"C").start();
    }

    public static void printCAS(int target){
        while(true){
            if(a.get() % 3 == target){
                System.out.println(Thread.currentThread().getName() + "_" + target);
                a.getAndIncrement();
            }
        }
    }
}
