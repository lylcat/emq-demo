package cn.ayulong.emqdemo.ehcache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
// @CacheConfig(cacheNames = { "userCache" }) 配置缓存基本信息cacheNames缓存名称
public class MapCache<K, V> {

    // 存放缓存的容器
    public Map<K, V> concurrentHashMap = new ConcurrentHashMap<>();

    // 纯手写单个JVM缓存框架 缓存概念偏向于临时
    // 代码分析 容器Map集合
    // 如何设计时间有效期，开两个线程，主线程存放定时job  每隔1秒

    // 存储
    // @Cacheable 该方法查询数据库完毕之后存入到缓存
    public void put(K k, V v) {
        concurrentHashMap.put(k, v);
    }

    // 查询
    public V get(K k) {
        return concurrentHashMap.get(k);
    }

    public void remove(K k) {
        concurrentHashMap.remove(k);
    }
}
