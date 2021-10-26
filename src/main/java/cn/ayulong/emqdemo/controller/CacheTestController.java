package cn.ayulong.emqdemo.controller;

import cn.ayulong.emqdemo.ehcache.MapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache_test")
public class CacheTestController {

    private final MapCache<String, Object> mapCache;

    @Autowired
    public CacheTestController(MapCache<String, Object> mapCache) {
        this.mapCache = mapCache;
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return (String) mapCache.get(key);
    }

    @GetMapping("/put")
    public String put(@RequestParam String key, @RequestParam String value) {
        mapCache.put(key, value);
        return "success";
    }

    @GetMapping("/remove")
    public String remove(@RequestParam String key) {
        mapCache.remove(key);
        return "success";
    }

}
