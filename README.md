# JavaRPC_wt

2024-12-01使用Java实现的RPC框架基本完成,目前包含功能有：
* Java原生Socket通信、高性能Netty通信。
* 序列化方式包括：Java原生、JSON、Protostuff、Kryo、Hessian。
* 编解码器实现：自定义编解码实现、解决Netty的半包、粘包问题。
* 客户端本地缓存服务列表以及基于Watcher机制实现的发布订阅更新功能。
* 基于Zookeeper实现的服务注册以及服务发现功能。
* Zk充当配置中心实现幂等接口注册以及超时重试。
* 客户端负载均衡实现：当前支持轮询法、随机法、一致性哈希法。
* 服务端自我保护机制：限流算法-目前支持令牌桶算法。
* 调用方熔断机制实现。支持熔断器三种状态的切换，开启、半开、关闭。

## 项目结构目录
目前包含两个Maven工程：codes、final
codes中包含有每个版本的具体实现，详细分解各个功能。
final是最终整体项目实现。

## TODO
* 加入更多序列化方式：protobuf、Hessian等。
* 支持基于cglib实现的反射。
* 自定义编解码器功能的进一步完善。
* 考虑集成springboot项目。
* 引入更强的性能测试。
* 目前能想到的就这么多，后续会逐步完善这个项目。

## 2024-12-02
加入版本号机制，同时嵌入SpringBoot。
对于接口方法是否幂等的判断，可以直接根据自定义注解，在具体方法上面标注。
此外自定义注解主持组号、令牌桶算法的容量以及负载均衡算法类型等。


## 更新自定义编解码器，在请求头中添加更多字段
* 升级自定义编解码器：
   * 1.魔术：判断是否是无效数据包。（4个字节）
   * 2.版本号：用于支持协议的支持。（1个字节）
   * 3.消息类型：目前有请求、响应（1个字节）
   * 4.序列化算法：JDK\JSON\Protostuff（1个字节）
   * 为了对其填充，在这里添加一个字节的无意义数据，让请求头中的消息保持在16个字节
   * 5.请求序号：为了双工通信，提供异步能力（4个字节）
   * 6.正文长度：用于指明data的长度（4个字节）


新增Protostuff序列化方式，并且支持动态配置具体序列化方式，目前支持的有JDK\JSON\Protostuff


使用JSON方式序列化时：
com.alibaba.fastjson.JSONException: not support Type Annotation.
==>alibaba的序列化和反序列化器默认不支持注解类型的，及时在fastJson2中支持，但序列化后注解类型全部序列化为null，反序列化时造成严重的数据丢失。
解决方案：在序列化和反序列化时构造中间类进行传输，将注解类型转为普通类型,详情参照common->util->RequestTransForm工具类的使用

加入Protostuff序列化方式已经成功实现。

新增Kryo、Hessian序列化方式,
Kryo初始化时,除了顶层类,还需要注册所有内部可能使用的嵌套类,如Object[]、Class<?>[]对于后者需要分别注册Class.class和Class[].class