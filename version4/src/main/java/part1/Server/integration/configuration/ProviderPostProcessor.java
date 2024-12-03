package part1.Server.integration.configuration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import part1.Server.integration.RpcService;
import part1.Server.provider.ServiceProvider;
import part1.Server.server.RpcServer;
import part1.Server.server.impl.NettyRPCRPCServer;

/**
 * @Author: yty
 * @Description: 提供者后置处理器
 * @DateTime: 2024/11/07 23:14
 **/
@Slf4j
@Configuration
public class ProviderPostProcessor implements InitializingBean,BeanPostProcessor, EnvironmentAware{



    // 通过@Value注解读取配置文件更改为通过EnvironmentAware方法读取
//    @Value("${rpc.server.host:127.0.0.1}")
    private String host;

//    @Value("${rpc.server.port:9999}")
    private int port;
    private ServiceProvider serviceProvider;

    private RpcServer rpcServer;

    /**
     * 服务注册，在Spring容器初始化Bean之前执行，通过Spring的BeanPostProcessor机制自动触发
     * 实现的是「BeanPostProcessor」中的接口
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ensureServiceProviderInitialized();
        Class<?> beanClass = bean.getClass();
        // 找到bean上带有 RpcService 注解的类
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService == null){
            return bean;
        }
        String group = rpcService.group();
        String version = rpcService.version();
        log.info("获取提供方服务现实bean的实现类类{},组号{},版本号{}",beanClass,group,version);
        serviceProvider.provideServiceInterface(bean, rpcService);
        return bean;
    }

    /**
     * 在Spring容器加载配置文件并「初始化完成后」执行 ——> 启动 RPC 服务
     * 实现的是「Initialization」中的接口
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ensureServiceProviderInitialized();
        Thread t = new Thread(() -> {
            try {
                log.info("服务端启动线程,主机号：{}, 端口：{}", host, port);
                rpcServer.start(port);
            } catch (Exception e) {
                log.error("启动RPC失败", e);
            }
        });
        // 守护线程，随主线程退出而停止
        t.setDaemon(true);
        t.start();

    }

    public void ensureServiceProviderInitialized(){
        // 双重检验确保只被初始化一次
        if(serviceProvider == null){
            serviceProvider = new ServiceProvider(host,port);
            rpcServer = new NettyRPCRPCServer(serviceProvider);
        }
    }

    /**
     * 读取配置文件,实现EnvironmentAware接口,提供访问Spring环境变量的能力
     * 实现的是「EnvironmentAware」中的接口
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.host = environment.getProperty("rpc.server.host", "127.0.0.1");
        this.port = Integer.parseInt(environment.getProperty("rpc.server.port", "9999"));
        log.info("RPC服务端host:{},端口port:{}", host, port);
    }
}