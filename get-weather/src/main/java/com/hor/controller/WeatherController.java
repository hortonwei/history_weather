package com.hor.controller;

import com.hor.bean.OneDay;
import com.hor.service.WeatherService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class WeatherController {

    @Autowired
    private WeatherService getWeatherService;

    // 各城市一年的数据
    @GetMapping("/insert_batch")
    public  void insert_batch() throws IOException, InterruptedException {
        List<String> cityNameList = new ArrayList<>();
        Collections.addAll(cityNameList, "kunming");
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

    //lishi.tianqi.com
    @GetMapping("/insert")
    public void insert(String cityName) throws IOException, InterruptedException {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36";
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String yearNo = "2020";

        String getUrl;
        for (int monthNo = 1; monthNo <= 12; monthNo++) {
            Thread.sleep(500);
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
        String yearNo = "2019";
        map.forEach((cityName, areaId) -> {
            try {
                insert2(yearNo, cityName, areaId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    //https://tianqi.2345.com/wea_history/54511.htm
    @GetMapping("/insert2")
    public void insert2(String yearNo, String cityName, String areaId) throws InterruptedException {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36";
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        Pattern patternNum = Pattern.compile("[^0-9]"); //匹配数字
        Pattern pattern = Pattern.compile("[~]"); //去除某特殊字符
        StringBuffer getUrl = new StringBuffer();
        for (int monthNo = 1; monthNo <= 12; monthNo++) {
            //拼接URL
            Thread.sleep(500);
            getUrl.delete(0, getUrl.length());
            getUrl.append("https://tianqi.2345.com/Pc/GetHistory?areaInfo[areaId]=");
            getUrl.append(areaId); //城市的地区id
            getUrl.append("&areaInfo[areaType]=2&date[year]=");
            getUrl.append(yearNo);
            getUrl.append("&date[month]=");
            getUrl.append(monthNo);
            //请求
            Document document = null;
            try {
                document = Jsoup.connect(String.valueOf(getUrl)).userAgent(userAgent).get();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            Elements tr = document.getElementsByTag("tr");
            for (Element element : tr) {
                OneDay oneDay = new OneDay();
                oneDay.setCityName(cityName);
                Elements td = element.getElementsByTag("td");
                int row = 1;
                StringBuilder res = new StringBuilder();
                for (Element element1 : td) {
                    String text = element1.text();
                    // 特殊字符‘~’处理, \u9634~\u5c0f\u96ea<\/td>\n (阴~小雪)
                    String str1 = text.split("<")[0];
                    if (row == 4) {
                        if (text.contains("~")) {
                            String[] split = str1.split("~");
                            for (int i = 0; i < split.length; i++) {
                                res.append(unicodeToString(split[i]));
                                if (i < split.length - 1) {
                                    res.append("转");
                                }
                            }
                            oneDay.setWeatherAbbr(res.toString());
                        }
                    }
                    //常规分割
                    //注意：\u4e1c\u5317\u98ce1\u7ea7<\/td>\n (东北风1级), u98ce1如果不判断长度会转换成‘东北賡级’
                    String[] split = str1.split("\\\\"); //反斜杠分割
                    StringBuilder stringBuilder = new StringBuilder();
                    String temp = "";
                    String num = "";
                    boolean flag = false;
                    for (String s : split) {
                        temp = s;
                        if (s.length() == 6) {
                            num = s.substring(5, 6); //风力值
                            temp = s.substring(0, 5);
                            flag = true;
                        }
                        if (temp.length() == 5) {
                            temp = "\\" + temp;
                        }
                        if (StringUtils.isEmpty(temp)) {
                            continue;
                        }
                        stringBuilder.append(unicodeToString(temp));
                        if (flag) {
                            stringBuilder.append(num);
                            flag = false;
                        }
                    }
                    if (row == 1) {
                        String s = stringBuilder.substring(0, 10);
                        LocalDate localDate = LocalDate.parse(s, formatter);
                        oneDay.setDate(localDate);
                    }
                    if (row == 2 ) {
                        String substring = stringBuilder.substring(0, stringBuilder.indexOf("°"));
                        oneDay.setMaxTemp(Integer.parseInt(substring));
                    }
                    if (row == 3) {
                        String substring = stringBuilder.substring(0, stringBuilder.indexOf("°"));
                        oneDay.setMinTemp((Integer.parseInt(substring)));
                    }
                    if (row == 4) {
                        oneDay.setWeatherAbbr(stringBuilder.toString());
                    }
                    if (row == 5) {
                        oneDay.setWindPower(stringBuilder.toString());
                    }
                    if (row == 6) {
                        Matcher matcher = patternNum.matcher(stringBuilder.toString());
                        String trim = matcher.replaceAll("").trim();
                        try {
                            int parseInt = Integer.parseInt(trim);
                            oneDay.setAqi(parseInt);
                        } catch (NumberFormatException e) {
                            System.out.println("aqi为空");
                            oneDay.setAqi(0);
                        }
                    }
                    row++;
                    res.delete(0, res.length());
                    stringBuilder.setLength(0);
                }
                Integer ifExistSameRow = queryIfExistSameRow(oneDay);
                if (ifExistSameRow > 0) {
                    System.out.println("存在重复数据---" + oneDay.getCityName() + oneDay.getDate());
                }else {
                    System.out.println(oneDay);
                    getWeatherService.getWeatherInsert(oneDay);
                }
            }
        }
    }


    private Integer queryIfExistSameRow(OneDay oneDay) {
        return getWeatherService.queryIfExistSameRow(oneDay);
    }

    private String unicodeToString(String unicode) {
        StringBuilder string = new StringBuilder();
        String[] hex = unicode.split("\\\\u");
        string.append(hex[0]);
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            string.append((char) data);
        }
        return string.toString();
    }
}
