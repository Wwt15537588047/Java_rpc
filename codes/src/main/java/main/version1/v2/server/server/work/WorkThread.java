package main.version1.v2.server.server.work;

import lombok.AllArgsConstructor;
import main.version1.v2.common.message.RPCRequest;
import main.version1.v2.common.message.RPCResponse;
import main.version1.v2.server.provider.ServiceProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

@AllArgsConstructor
public class WorkThread implements Runnable{
    private Socket socket;
    private ServiceProvider serviceProvider;
    @Override
    public void run() {
        try {
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
            //读取客户端传过来的request
            RPCRequest rpcRequest = (RPCRequest) ois.readObject();
            //反射调用服务方法获取返回值
            RPCResponse rpcResponse=getPRCResp(rpcRequest);
            //向客户端写入response
            oos.writeObject(rpcResponse);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private RPCResponse getPRCResp(RPCRequest req){
        // 获取到服务名
        String interfaceName = req.getInterfaceName();
        // 得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        // 反射调用方法
        Method method = null;
        try {
            method = service.getClass().getMethod(req.getMethodName(), req.getParamsType());
            Object invoke = method.invoke(service, req.getParams());
            return RPCResponse.sussess(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RPCResponse.fail();
        }
    }
}
