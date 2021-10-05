package com.hor.controller;

import com.hor.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
public class WeatherController {

    @Autowired
    private WeatherService getWeatherService;

    private final String yearNo = "2020";

    // 各城市一年的数据
    @GetMapping("/insert_batch")
    public  void insert_batch() throws InterruptedException {
        List<String> cityNameList = new ArrayList<>();
        Collections.addAll(cityNameList, "kunming", "chengdu");
        for (String cityName : cityNameList) {
            getWeatherService.insert1(cityName, yearNo);
        }
    }

    // 各城市一年的数据
    @GetMapping("/insert_batch2")
    public  void insert_batch2() {
        HashMap<String, String> map = new HashMap<>(); //cityName areaId
        //map.put("beijing", "54511");
        //map.put("shanghai", "58362");
        //map.put("guangzhou", "59287");
        //map.put("shenzhen", "59493");
        //map.put("chengdu", "56294");
        //map.put("hangzhou", "58457");
        //map.put("chongqing", "57516");
        //map.put("wuhan", "57494");
        //map.put("xian", "57036");
        //map.put("suzhou", "58357");
        //map.put("nanjing", "58238");
        //map.put("changsha", "57687");
        //map.put("qingdao", "54857");
        //map.put("shenyang", "54342");
        //map.put("kunming", "56778");
        //map.put("dalian", "54662");
        //map.put("fuzhou", "58847");
        //map.put("xiamen", "59134");
        //map.put("harbin", "50953");
        //map.put("zhuhai", "59488");
        //map.put("nanning", "59431");
        map.put("guiyang", "57816");
        map.forEach((cityName, areaId) -> {
            try {
                getWeatherService.insert2(yearNo, cityName, areaId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @GetMapping("/insert_batch3")
    public void insert_batch3() {
        HashMap<String, String> cityNameMap = new HashMap<>(); //nameEN nameCN
        //map.put("beijing", "北京");
        //map.put("shanghai", "上海");
        //map.put("guangzhou", "广州");
        //map.put("shenzhen", "深圳");
        //map.put("chengdu", "成都");
        //map.put("hangzhou", "杭州");
        //map.put("chongqing", "重庆");
        //map.put("wuhan", "武汉");
        //map.put("xian", "西安");
        //map.put("suzhou", "苏州");
        //map.put("nanjing", "南京");
        //map.put("changsha", "长沙");
        //map.put("qingdao", "青岛");
        //map.put("shenyang", "沈阳");
        //map.put("kunming", "昆明");
        //map.put("dalian", "大连");
        //map.put("fuzhou", "福州");
        //map.put("xiamen", "厦门");
        //map.put("harbin", "哈尔滨");
        //map.put("zhuhai", "珠海");
        //map.put("nanning", "南宁");
        //cityNameMap.put("guiyang", "贵阳");
        cityNameMap.put("dongguan", "东莞");
        //cityNameMap.put("foshan", "佛山");
        cityNameMap.forEach((nameEN, nameCN) -> {
            try {
                getWeatherService.insert3(nameEN, nameCN, yearNo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


}
