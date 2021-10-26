package cn.ayulong.emqdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController {

    @RequestMapping("/mvTest")
    public ModelAndView mvTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("hello", "world");
        return new ModelAndView("", "", map.getClass().cast(map));
    }

}
