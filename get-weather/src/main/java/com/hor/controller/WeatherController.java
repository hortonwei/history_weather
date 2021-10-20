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

    private final String yearNo = "2019";

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
        cityNameMap.put("beijing", "北京");
        cityNameMap.put("shanghai", "上海");
        cityNameMap.put("guangzhou", "广州");
        cityNameMap.put("shenzhen", "深圳");
        cityNameMap.put("chengdu", "成都");
        cityNameMap.put("hangzhou", "杭州");
        cityNameMap.put("chongqing", "重庆");
        cityNameMap.put("wuhan", "武汉");
        cityNameMap.put("xian", "西安");
        cityNameMap.put("suzhou", "苏州");
        cityNameMap.put("nanjing", "南京");
        cityNameMap.put("changsha", "长沙");
        cityNameMap.put("qingdao", "青岛");
        cityNameMap.put("shenyang", "沈阳");
        cityNameMap.put("kunming", "昆明");
        cityNameMap.put("dalian", "大连");
        cityNameMap.put("fuzhou", "福州");
        cityNameMap.put("xiamen", "厦门");
        cityNameMap.put("harbin", "哈尔滨");
        cityNameMap.put("zhuhai", "珠海");
        cityNameMap.put("nanning", "南宁");
        cityNameMap.put("guiyang", "贵阳");
        cityNameMap.put("dongguan", "东莞");
        cityNameMap.put("foshan", "佛山");
        cityNameMap.forEach((nameEN, nameCN) -> {
            try {
                boolean isFail = getWeatherService.insert3(nameEN, nameCN, yearNo);
                if (isFail) {
                    throw new InterruptedException();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


}
