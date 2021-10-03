package com.hor.mapper;

import com.hor.bean.OneDay;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface WeatherMapper {

    void insertWeather(OneDay oneDay);

    //void updateById(ParamDTO param);

    Integer queryIfCityNameExist(String cityName);

    Integer queryIfExistSameRow(OneDay oneDay);

}
