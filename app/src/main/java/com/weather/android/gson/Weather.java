package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wb.chuyong on 2017/4/13.
 */

//一个总的实体类，引用之前创建的各个实体类（分别用于存储解析数据的实体类）
public class Weather {

    //返回状态只需解析获取即可，无需再建一个实体类
    public String status;

    public AQI aqi;

    public Basic basic;

    public Now now;

    public Suggestion suggestion;

    //由于预报的信息返回数据有点特殊，这里做一个处理
    //daily_forecase包含一个数组
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}













