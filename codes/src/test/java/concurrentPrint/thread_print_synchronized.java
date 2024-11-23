package concurrentPrint;


public class thread_print_synchronized {

    // 使用Volatile和synchronized实现多线程打印123
    public static volatile  int a = 0;
    // 线程同步机制唯一变量
    public static final Object lock = new Object();
    public static void main(String[] args) {
        new Thread(()->print(0), "A").start();
        new Thread(()->print(1), "B").start();
        new Thread(()->print(2), "C").start();
    }
    public static void print(int target){
        while(true){
            // synchronized用于保证操作的原子性
            synchronized (lock){
                while(a % 3 != target){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(Thread.currentThread().getName() + "_" + target);
                ++a;
                lock.notifyAll();
            }
        }
    }
}
