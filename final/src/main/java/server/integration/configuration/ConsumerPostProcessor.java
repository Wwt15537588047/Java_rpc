package server.integration.configuration;

import client.proxy.ClientProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import server.integration.References;

import java.lang.reflect.Field;

/**
 * @Author wt
 * @Description 消费者后置处理器
 * @Data 2024/12/2 下午7:05
 */
@Slf4j
@Configuration
public class ConsumerPostProcessor implements InitializingBean, BeanPostProcessor, EnvironmentAware {
    /**
     * 读取配置文件后的操作---->启动RPC
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setEnvironment(Environment environment) {

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    /**
     * 消费者获取代理对象，发生请求
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取所有字段
        Field[] fields = bean.getClass().getDeclaredFields();
        // 遍历所有字段找到Reference 注解的字段
        for (Field field : fields) {
            if(field.isAnnotationPresent(References.class)){
                final References references = field.getAnnotation(References.class);
                final Class<?> aClass = field.getType();
                field.setAccessible(true);
                Object object = null;

                ClientProxy clientProxy = new ClientProxy();
                //获取服务代理对象
                log.info("接口类型:{},接口参数:{}",aClass,references);
                object = clientProxy.getProxy(aClass, references);

                // 将消费者原接口替换为RPC的代理对象
                try {
                    field.set(bean, object);
                    field.setAccessible(false);
                    log.info(beanName + " field:" + field.getName() + "注入成功");
                } catch (IllegalAccessException e) {
                    log.info(beanName + " field:" + field.getName() + "注入失败");
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
}
