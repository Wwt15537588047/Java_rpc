package proxy.jdk;

import proxy.jdk.imp.GetUserNameByIdImp;

/**
 * @Author wt
 * @Description 测试类实现
 * @Data 2024/12/3 下午2:42
 */
public class Test {
    public static void main(String[] args) {
        GetUserNameByIdImp getUserNameByIdImp = new GetUserNameByIdImp();
        MyProxy myProxy = new MyProxy(getUserNameByIdImp);
        // 获取代理对象
        GetUserNameById proxy = myProxy.getProxy(GetUserNameById.class);
        String userName = proxy.getUserName(1);
    }
}
