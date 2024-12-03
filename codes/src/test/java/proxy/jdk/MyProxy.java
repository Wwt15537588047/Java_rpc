package proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author wt
 * @Description 实现InvocationHandler接口，并重新invoke方法
 * @Data 2024/12/3 下午2:41
 */
public class MyProxy implements InvocationHandler {
    /**
     * 代理类中的真实对象
     */
    private final Object target;

    public MyProxy(Object target){
        this.target = target;
    }

    /**
     * proxy：动态生成的代理类
     * method：与代理类对象调用的方法相对应
     * args：当前method方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("进入代理类...");
        Object invoke = method.invoke(target, args);
        System.out.println("invoke : " + invoke);
        System.out.println("离开代理类...");
        return null;
    }

    public <T>T getProxy(Class<T> clazz){
        /**
         * ClassLoader loader,类加载器，用于加载代理对象
         * Class<?>[] interfaces,被代理类实现的一些接口
         * InvocationHandler h,实现了InvocationHandler接口的对象
         */
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
