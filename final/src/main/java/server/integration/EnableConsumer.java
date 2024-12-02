package server.integration;

/**
 * @Author wt
 * @Description TODO
 * @Data 2024/12/2 下午7:07
 */

import org.springframework.context.annotation.Import;
import server.integration.configuration.ConsumerPostProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消费者项目启动注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ConsumerPostProcessor.class)
public @interface EnableConsumer {
}
