package com.hor.service;

import com.hor.bean.OneDay;
import com.hor.bean.OneMonthJson;
import com.hor.mapper.WeatherMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class WeatherService {

    @Autowired
    private WeatherMapper weatherMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36";
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final String appid = "24287291";
    private final String appsecret = "7kIjgkkq";

    public void getWeatherInsert(OneDay oneDay) {
        weatherMapper.insertWeather(oneDay);
    }

    public Integer queryIfExistSameRow(OneDay oneDay) {
        return weatherMapper.queryIfExistSameRow(oneDay);
    }

    //使用API接口（每个邮箱注册试用200次请求）
    //https://tianqiapi.com/api?version=history&appid=61273811&appsecret=G3vsSZqC&city=北京&year=2020&month=1
    public boolean insert3(String nameEN, String nameCN, String yearNo) throws InterruptedException {
        StringBuffer url = new StringBuffer();
        Document doc;
        for (int monthNo = 1; monthNo <= 12; monthNo++) {
            Thread.sleep(500);
            url.setLength(0);
            url.append("https://tianqiapi.com/api?version=history&appid=").append(appid).append("&appsecret=").append(appsecret)
                    .append("&city=").append(nameCN).append("&year=").append(yearNo).append("&month=");
            url.append(monthNo);
            Query query = new Query(Criteria.where("city").is(nameCN).and("date").is(yearNo + monthNo));
            query.limit(1);
            List<OneMonthJson> all = mongoTemplate.find(query, OneMonthJson.class, yearNo);
            if (all.size() == 0) {
                try {
                    doc = Jsoup.connect(String.valueOf(url)).ignoreContentType(true).userAgent(userAgent).get();
                    //doc = Jsoup.parse(new URL(url.toString()).openStream(), "GBK", String.valueOf(url));
                } catch (IOException e) {
                    e.printStackTrace();
                    return true;
                }
                String s = convertUnicode(doc.text());
                if (s.contains("\"errcode\":100")) {
                    System.out.println("响应错误码，退出循环");
                    return true;
                }
                System.out.println(s);
                mongoTemplate.insert(s, yearNo);
            } else {
                System.out.println("跳过重复数据");
            }
        }
        return false;
    }

    public static String convertUnicode(String ori){
        char aChar;
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    //https://tianqi.2345.com/wea_history/54511.htm
    public void insert2(String yearNo, String cityName, String areaId) throws InterruptedException {
        Pattern patternNum = Pattern.compile("[^0-9]"); //匹配数字
        Pattern pattern = Pattern.compile("[~]"); //去除某特殊字符
        StringBuffer getUrl = new StringBuffer();
        for (int monthNo = 1; monthNo <= 12; monthNo++) {
            //拼接URL
            Thread.sleep(500);
            getUrl.delete(0, getUrl.length());
            getUrl.append("https://tianqi.2345.com/Pc/GetHistory?areaInfo[areaId]=").append(areaId).append("&areaInfo[areaType]=2&date[year]=")
                    .append(yearNo).append("&date[month]=").append(monthNo);
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
                    getWeatherInsert(oneDay);
                }
            }
        }
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

    //lishi.tianqi.com
    public void insert1(String cityName, String yearNo) throws InterruptedException {
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
                        getWeatherInsert(oneDay);
                    }
                }
            }
        }
        System.out.println("---完成---");
    }


}
