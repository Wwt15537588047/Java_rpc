package main.version1.v2.client.rpcClient.impl;

import main.version1.v2.client.rpcClient.RPCClient;
import main.version1.v2.common.message.RPCRequest;
import main.version1.v2.common.message.RPCResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SimpleSocketRPCClientImpl implements RPCClient {
    private String host;
    private int port;
    public SimpleSocketRPCClientImpl(String host,int port){
        this.host=host;
        this.port=port;
    }
    @Override
    public RPCResponse sendRequest(RPCRequest request) {
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
