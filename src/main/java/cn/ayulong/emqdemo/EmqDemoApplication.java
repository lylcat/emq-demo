package cn.ayulong.emqdemo;

import cn.ayulong.emqdemo.mqtt.client.EmqClient;
import cn.ayulong.emqdemo.mqtt.enums.QosEnum;
import cn.ayulong.emqdemo.mqtt.properties.MqttProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCaching // 开启缓存
public class EmqDemoApplication {

    private final EmqClient emqClient;

    private final MqttProperties properties;

    public EmqDemoApplication(EmqClient emqClient, MqttProperties properties) {
        this.emqClient = emqClient;
        this.properties = properties;
    }

    public static void main(String[] args) {
        SpringApplication.run(EmqDemoApplication.class, args);
    }


    // @PostConstruct
    public void init() {
        // 连接服务端
        emqClient.connect(properties.getUsername(), properties.getPassword());
        // 订阅一个主题
        emqClient.subscribe("testtopic/#", QosEnum.QoS2);
        // 开启一个新的线程 每隔5秒去向 testtopic/123 发布消息
        new Thread(() -> {
            while (true) {
                emqClient.publish("testtopic/123",
                        "publish msg: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        QosEnum.QoS2, false);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
