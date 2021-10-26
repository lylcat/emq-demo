package cn.ayulong.emqdemo.controller;

import cn.ayulong.emqdemo.mqtt.enums.QosEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mqtt")
public class WebHookController {

    private static final Logger log = LoggerFactory.getLogger(WebHookController.class);

    private final Map<String, Boolean> clientStatus = new HashMap<>();

    @PostMapping("/webhook")
    public void hook(@RequestBody Map<String, Object> params) {
        log.info("emqx 触发了webhook，请求体数据={}", params);
        String action = (String) params.get("action");
        String clientId = (String) params.get("clientid");
        if (action.equals("client_connected")) {
            log.info("客户端{}接入本系统", clientId);
            clientStatus.put(clientId, true);
            // 自动为客户端产生订阅
            autoSub(clientId, "autoSub/#", QosEnum.QoS2, true);
        }
        if (action.equals("client_disconnected")) {
            log.info("客户端{}下线", clientId);
            clientStatus.put(clientId, false);
            // 自动为客户端取消订阅
            autoSub(clientId, "autoSub/#", QosEnum.QoS2, false);
        }
    }

    @GetMapping("/allStatus")
    public Map<String, Boolean> getStatus() {
        return clientStatus;
    }

    /**
     * 自动订阅和取消订阅
     * @param clientId
     * @param topicFilter
     * @param qos
     * @param sub
     */
    private void autoSub(String clientId, String topicFilter, QosEnum qos, boolean sub) {
        RestTemplate restTemplate = new RestTemplateBuilder().basicAuthentication("admin", "public")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
        // 封装请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("clientid", clientId);
        params.put("topic", topicFilter);
        params.put("qos", qos.value());

        // 设置头信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 设置请求体
        HttpEntity<Object> entity = new HttpEntity<>(params, headers);

        // 发送请求
        if (sub) {
            // 自动订阅
            new Thread(() -> {
                ResponseEntity<String> resEntity = restTemplate.postForEntity("http://192.168.1.111:8081/api/v4/mqtt/subscribe", entity, String.class);
                log.info("自动订阅的结果：{}", resEntity.getBody());
            }).start();
            return;
        }
        // 取消订阅
        ResponseEntity<String> resEntity = restTemplate.postForEntity("http://192.168.1.111:8081/api/v4/mqtt/unsubscribe", entity, String.class);
        log.info("自动取消订阅的结果：{}", resEntity.getBody());
    }

}

