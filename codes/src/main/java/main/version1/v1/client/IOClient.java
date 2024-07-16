package main.version1.v1.client;

import main.version1.v1.common.message.RPCRequest;
import main.version1.v1.common.message.RPCResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// 负责将Client端的请求发送给服务端，并从Server端读取响应
public class IOClient {
    public static RPCResponse sendRequest(String host, int port, RPCRequest req){
        // 使用socket与服务端建立连接
        try {
            Socket socket = new Socket(host, port);
            // 构建输入输出流
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            // 发送数据
            oos.writeObject(req);
            oos.flush();
            // 接收数据
            RPCResponse resp = (RPCResponse) ois.readObject();
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
