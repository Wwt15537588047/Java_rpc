package server.integration;

import org.springframework.context.annotation.Import;
import server.integration.configuration.ProviderPostProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author wt
 * @Description 提供者项目启动注解
 * @Data 2024/12/2 下午7:07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ProviderPostProcessor.class)
public @interface EnableProvider {
}
