package cn.ayulong.emqdemo.mqtt.client;

import cn.ayulong.emqdemo.mqtt.enums.QosEnum;
import cn.ayulong.emqdemo.mqtt.properties.MqttProperties;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Component
public class EmqClient {

    private static final Logger log = LoggerFactory.getLogger(EmqClient.class);

    private IMqttClient mqttClient;

    private final MqttProperties mqttProperties;

    private final MqttCallback mqttCallback;

    @Autowired
    public EmqClient(MqttProperties mqttProperties, MqttCallback mqttCallback) {
        this.mqttProperties = mqttProperties;
        this.mqttCallback = mqttCallback;
    }


    @PostConstruct
    public void init() {
        MqttClientPersistence mqttPersistence = new MemoryPersistence();
        try {
            mqttClient = new MqttClient(mqttProperties.getBrokerUrl(), mqttProperties.getClientId(), mqttPersistence);
        } catch (MqttException e) {
            log.error("初始化客户端mqttClient对象失败, errorMsg={}, brokerUrl={}. clientId={}",
                    e.getMessage(), mqttProperties.getBrokerUrl(), mqttProperties.getClientId());
        }
    }

    /**
     * 连接broker
     *
     * @param username 用户名
     * @param password 密码
     */
    public void connect(String username, String password) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        mqttClient.setCallback(mqttCallback);
        try {
            mqttClient.connect(options);
        } catch (MqttException e) {
            log.error("mqtt客户端连接服务端失败，失败原因{}", e.getMessage());
        }
    }

    /**
     * 断开连接
     */
    @PreDestroy
    public void disconnect() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            log.error("断开连接产生异常，异常信息{}", e.getMessage());
        }
    }

    /**
     * 重连
     */
    public void reConnect() {
        try {
            mqttClient.reconnect();
        } catch (MqttException e) {
            log.error("重连失败，失败原因{}", e.getMessage());
        }
    }

    /**
     * 发布消息
     *
     * @param topic
     * @param msg
     * @param qos
     * @param retain
     */
    public void publish(String topic, String msg, QosEnum qos, boolean retain) {

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(msg.getBytes());
        mqttMessage.setQos(qos.value());
        mqttMessage.setRetained(retain);
        try {
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            log.error("发布消息失败，errorMsg={}, topic={}, msg={}, qos={}. retain={}",
                    e.getMessage(), topic, msg, qos.value(), retain);
        }

    }

    /**
     * 订阅
     *
     * @param topicFilters
     * @param qos
     */
    public void subscribe(String topicFilters, QosEnum qos) {
        try {
            mqttClient.subscribe(topicFilters, qos.value());
        } catch (MqttException e) {
            log.error("订阅主题失败，errorMsg={}, topicFilter={}, qos={}",
                    e.getMessage(), topicFilters, qos.value());
        }
    }

    /**
     * 取消订阅
     * @param topicFilter
     */
    public void unSubscribe(String topicFilter) {
        try {
            mqttClient.unsubscribe(topicFilter);
        } catch (MqttException e) {
            log.error("取消订阅失败，errorMsg={}, topicFilter={}", e.getMessage(), topicFilter);
        }
    }

}
