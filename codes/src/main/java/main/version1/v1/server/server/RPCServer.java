package main.version1.v1.server.server;

public interface RPCServer {
    //开启监听
    void start(int port);
    void stop();
}
