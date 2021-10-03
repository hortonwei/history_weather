package com.hor.service;

import com.hor.bean.OneDay;
import com.hor.mapper.WeatherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WeatherService {

    @Autowired
    private WeatherMapper weatherMapper;

    public void getWeatherInsert(OneDay oneDay) {
        weatherMapper.insertWeather(oneDay);
    }

    public Integer queryIfCityNameExist(String cityName) {
        return weatherMapper.queryIfCityNameExist(cityName);
    }

    public Integer queryIfExistSameRow(OneDay oneDay) {
        return weatherMapper.queryIfExistSameRow(oneDay);
    }


}
