package com.hor.controller;

import com.hor.bean.OneDay;
import com.hor.service.WeatherService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@RestController
public class WeatherController {

    @Autowired
    private WeatherService getWeatherService;

    // 各城市一年的数据
    @GetMapping("/insert_batch")
    public  void insert_batch() throws IOException, InterruptedException {
        List<String> cityNameList = new ArrayList<>();
        Collections.addAll(cityNameList,
                "liupanshui", "zhuhai", "nanning");
        for (String cityName : cityNameList) {
            // 库中已存在城市名则跳过
            System.out.println(cityName);
            Integer ifCityNameExist = getWeatherService.queryIfCityNameExist(cityName);
            if (ifCityNameExist > 0) {
                System.out.println(cityName + "城市名查库已存在---");
            } else {
                insert(cityName);
            }
        }
    }

    @GetMapping("/insert")
    public  void insert(String cityName) throws IOException, InterruptedException {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36";
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        FileInputStream fileInputStream = new FileInputStream(new File("D:\\open-code\\mybatis\\mybatis-demo\\target\\classes/weather.properties"));
        Properties props = new Properties();
        props.load(fileInputStream);
        String yearNo = props.getProperty("yearNo");

        String getUrl;
        for (int monthNo = 1; monthNo <= 12; monthNo++) {
            Thread.sleep(2000);
            if (monthNo < 10) {
                getUrl = "https://lishi.tianqi.com/" + cityName + "/" + yearNo + "0" + monthNo + ".html";
            } else {
                getUrl = "https://lishi.tianqi.com/" + cityName + "/" + yearNo + monthNo + ".html";
            }
            System.out.println(getUrl);
            Document document = null;
            try {
                document = Jsoup.connect(getUrl).userAgent(userAgent).get();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            Elements thrui = document.getElementsByClass("thrui");
            for (Element element : thrui) {
                OneDay oneDay = new OneDay();
                oneDay.setCityName(cityName);
                Elements li = element.getElementsByTag("li");
                int time = 1;
                for (Element oneLi : li) {
                    Elements div = oneLi.getElementsByTag("div");
                    for (Element oneDiv : div) {
                        if (time == 1) {
                            String s = oneDiv.text().substring(0, 10);
                            LocalDate localDate = LocalDate.parse(s, formatter);
                            oneDay.setDate(localDate);
                        }
                        if (time == 2) {
                            String maxTemp = oneDiv.text().split("℃")[0];
                            oneDay.setMaxTemp(Integer.parseInt(maxTemp));
                        }
                        if (time == 3) {
                            String minTemp = oneDiv.text().split("℃")[0];
                            oneDay.setMinTemp(Integer.parseInt(minTemp));
                        }
                        if (time == 4) {
                            oneDay.setWeatherAbbr(oneDiv.text());
                        }
                        if (time == 5) {
                            oneDay.setWindPower(oneDiv.text());
                        }
                        time++;
                    }
                    time = 1;
                    //System.out.println(oneDay);
                    Integer ifExistSameRow = queryIfExistSameRow(oneDay);
                    if (ifExistSameRow > 0) {
                        System.out.println("存在重复数据---");
                    } else {
                        getWeatherService.getWeatherInsert(oneDay);
                    }
                }
            }
        }
        System.out.println("---完成---");
    }

    private Integer queryIfExistSameRow(OneDay oneDay) {
        return getWeatherService.queryIfExistSameRow(oneDay);
    }


}
