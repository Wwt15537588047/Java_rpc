package server.integration.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import server.integration.RpcService;
import server.provider.ServiceProvider;
import server.server.RPCServer;
import server.server.impl.NettyRPCServerImpl;

/**
 * @Author wt
 * @Description 提供者后置处理器
 * @Data 2024/12/2 下午7:06
 */
@Slf4j
public class ProviderPostProcessor implements InitializingBean, BeanPostProcessor, EnvironmentAware {

    // 写死本机
    private static final ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);

    private static final RPCServer rpcServer = new NettyRPCServerImpl(serviceProvider);

    /**
     * 读取配置文件后的操作,启动RPC服务
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Thread t = new Thread(()->{
            try {
                log.info("服务端启动线程...");
                rpcServer.start(9999);
            }catch (Exception e){
                log.error("服务端启动线程失败...");
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void setEnvironment(Environment environment) {

    }

    /**
     * 服务注册
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
        Class<?> aClass = bean.getClass();
        // 找到Bean上带有RpcService注解的
        RpcService rpcService = aClass.getAnnotation(RpcService.class);
        if(rpcService == null){
            return bean;
        }
        String group = rpcService.group();
        String version = rpcService.version();
        log.info("获取提供方服务现实bean的实现类类{},组号{},版本号{}",aClass,group,version);
        serviceProvider.providerServiceInterface(bean, rpcService);
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
