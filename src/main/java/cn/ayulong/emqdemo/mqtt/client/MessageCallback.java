package cn.ayulong.emqdemo.mqtt.client;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageCallback implements MqttCallback {

    private static final Logger log = LoggerFactory.getLogger(MessageCallback.class);

    /**
     * 丢失了对服务端的链接后触发的回调
     * @param throwable
     */
    @Override
    public void connectionLost(Throwable throwable) {
        // 资源的清理重连
        log.info("丢失了对服务端的链接");
    }

    /**
     * 应用收到消息后触发的回调
     * @param s
     * @param mqttMessage
     * @throws Exception
     */
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        log.info("订阅者收到了消息, topic={}, messageId={}, qos={}, payload={}",
                s, mqttMessage.getId(),
                mqttMessage.getQos(),
                new String(mqttMessage.getPayload()));
    }

    /**
     * 消息发布者消息发布完成时产生的回调
     * @param iMqttDeliveryToken
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        int messageId = iMqttDeliveryToken.getMessageId();
        String[] topics = iMqttDeliveryToken.getTopics();
        log.info("消息发布完成，messageId={}，topics={}", messageId, topics);
    }
}
