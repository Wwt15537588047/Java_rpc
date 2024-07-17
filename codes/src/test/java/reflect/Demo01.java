package reflect;


import reflect.testServices.TestService;
import reflect.testServices.impl.TestServiceImpl;

import java.lang.reflect.Method;

public class Demo01 {
    public static void main(String[] args) throws NoSuchMethodException {
        System.out.println("=====");
        // 获取方法名
        Method getUserById = TestService.class.getMethod("getUserById", Integer.class);
        System.out.println(getUserById);
        // 获取方法对应的接口名
        System.out.println(getUserById.getDeclaringClass().getName());

        System.out.println("======");
        TestService testService = new TestServiceImpl();
        // 获取实现的接口字节码集合
        Class<?>[] interfaces = testService.getClass().getInterfaces();
        String name = testService.getClass().getName();
        System.out.println(name);
        // 遍历接口字节码集合
        for (Class<?> interfacea : interfaces) {
            // 获取接口名称
            System.out.println(interfacea.getName());
            // 遍历各个接口中定义的所有方法
            Method[] declaredMethods = interfacea.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                System.out.println("innnnnnnn" + declaredMethod);
            }
        }
        System.out.println("=========");
        // 获取实现类中定义的所有方法
        Method[] methods = testService.getClass().getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method);
        }
    }
}
