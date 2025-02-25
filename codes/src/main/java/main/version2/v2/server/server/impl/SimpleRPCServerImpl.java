package main.version2.v2.server.server.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.version2.v2.server.provider.ServiceProvider;
import main.version2.v2.server.server.RPCServer;
import main.version2.v2.server.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class SimpleRPCServerImpl implements RPCServer {
    private ServiceProvider serviceProvider;
    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("简易版服务器启动了......");
            while (true){
                // BIO,没有连接会一直阻塞
                Socket accept = serverSocket.accept();
                // 有链接，创建一个线程执行任务
                new Thread(new WorkThread(accept, serviceProvider)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
