package client.rpcClient.impl;



import client.rpcClient.RPCClient;
import client.serviceCenter.ServiceCenter;
import client.serviceCenter.impl.ZKServiceCenterImpl;
import common.message.RPCResponse;
import common.message.RPCRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SimpleSocketRPCClientImpl implements RPCClient {
//    private String host;
//    private int port;
    private ServiceCenter serviceCenter;
    public SimpleSocketRPCClientImpl(){
        this.serviceCenter = new ZKServiceCenterImpl();
    }
    @Override
    public RPCResponse sendRequest(RPCRequest request) {
        // 从注册中心获取host, port
        InetSocketAddress inetSocketAddress = serviceCenter.serviceDiscovery(request.getInterfaceName() + "." + request.getReferences().version());
        String host = inetSocketAddress.getHostName();
        int port = inetSocketAddress.getPort();
        try {
            Socket socket=new Socket(host, port);
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());

            oos.writeObject(request);
            oos.flush();

            RPCResponse response=(RPCResponse) ois.readObject();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
