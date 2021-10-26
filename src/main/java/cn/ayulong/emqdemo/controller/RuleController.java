package cn.ayulong.emqdemo.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/resource")
public class RuleController {

    @PostMapping("/process")
    public void process(@RequestBody Map<String, Object> params) {
        System.out.println("---规则引擎---");
        params.forEach((key, value) ->
                System.out.println(key + ": " + value));
    }
}
