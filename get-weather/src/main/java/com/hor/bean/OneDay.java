package com.hor.bean;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @Author: HortonWei@foxmail.com
 * @Date: 2021/7/27 23:38
 */
@Data
public class OneDay implements Serializable {

     private static final long serialVersionUID = -5809782578272943999L;

     private String id;
     private String cityName;
     private LocalDate date;
     private int maxTemp;
     private int minTemp;
     private String weatherAbbr;
     private String windPower;
     private int aqi;

}
