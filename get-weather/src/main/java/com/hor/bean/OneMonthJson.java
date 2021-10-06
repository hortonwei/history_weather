package com.hor.bean;

import lombok.Data;

import java.util.ArrayList;

/**
 * @Author: HortonWei@foxmail.com @Date: 2021/10/6 22:35
 */
@Data
public class OneMonthJson {
    private String cityid;
    private String date;
    private String city;
    private String cityEn;
    private String avgbWendu;
    private String avgyWendu;
    private String maxWendu;
    private String minWendu;
    private String avgAqi;
    private String minAqi;
    private String minAqiDate;
    private String minAqiInfo;
    private String minAqiLevel;
    private String maxAqi;
    private String maxAqiDate;
    private String maxAqiInfo;
    private String maxAqiLevel;
    private ArrayList<com.hor.bean.Data> data;
}
