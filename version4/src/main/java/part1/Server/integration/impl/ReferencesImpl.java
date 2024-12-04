package part1.Server.integration.impl;

import part1.Server.integration.References;

import java.lang.annotation.Annotation;

/**
 * @Author wt
 * @Description 接口References实现类，接口不能实例化，要想使用请求参数转换工具消除注解的存在，必须创建一个实现类
 * @Data 2024/12/4 上午11:08
 */
public class ReferencesImpl implements References {
    private String version;
    private String group;
    private String loadBalance;

    public ReferencesImpl(String version, String group, String loadBalance) {
        this.version = version;
        this.group = group;
        this.loadBalance = loadBalance;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public String loadBalance() {
        return loadBalance;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

//    @Override
//    public Class<? extends Annotation> annotationType() {
//        return null;
//    }
}
