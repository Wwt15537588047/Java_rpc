package main.version3.v1.server.server.impl;

import lombok.extern.slf4j.Slf4j;
import main.version3.v1.server.provider.ServiceProvider;
import main.version3.v1.server.server.RPCServer;
import main.version3.v1.server.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPoolRPCServerImpl implements RPCServer {
    private ServiceProvider serviceProvider;
    private final ThreadPoolExecutor threadPool;

    // 系统自定义线程池容量
    public ThreadPoolRPCServerImpl(ServiceProvider serviceProvider){
        threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                1000, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
        this.serviceProvider = serviceProvider;
    }

    // 用户自定义线程池容量
    public ThreadPoolRPCServerImpl(ServiceProvider serviceProvider, int corePoolSize,
                                   int maximumPoolSize,
                                   long keepAliveTime,
                                   TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue){
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.serviceProvider = serviceProvider;
    }


    @Override
    public void start(int port) {
        log.info("线程池版的服务端启动了");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                Socket socket = serverSocket.accept();
                threadPool.execute(new WorkThread(socket, serviceProvider));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
