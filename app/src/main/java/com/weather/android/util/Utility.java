package com.weather.android.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.weather.android.db.City;
import com.weather.android.db.County;
import com.weather.android.db.Province;
import com.weather.android.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wb.chuyong on 2017/4/7.
 */

public class Utility {

    //解析和处理服务器返回的省级数据
    public static boolean handleProvinceResponse(String response){

        if(!TextUtils.isEmpty(response)){

            try {

                //后台返回的数据是json格式的字符串（大括号中括号，一层一层的取，先取出中括号，再获取大括号的数组对象）
                JSONArray allProvinces = new JSONArray(response);

                for (int i = 0 ;i<allProvinces.length(); i++){

                    //获取json数据对象，从外面第一层开始取
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    //创建实体类省的事例，用于操作
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();

                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;

    }


    //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response , int provinceId){

        if(!TextUtils.isEmpty(response)){

            try {

                JSONArray allCity = new JSONArray(response);

                for(int i = 0 ;i<allCity.length(); i++){

                    JSONObject cityObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();

                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的县级数据
    public static boolean handleCountyResponse(String response , int cityId){

        if(!TextUtils.isEmpty(response)){

            try {
                JSONArray allCounty = new JSONArray(response);

                for(int i = 0 ;i<allCounty.length(); i++){

                    JSONObject countyObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();

                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;

    }


    /*
    将返回的json数据解析成weather实体类
     */
    @Nullable
    public static Weather handlerWeatherResponse(String response){

        try {

            JSONObject jsonObject = new JSONObject(response);

            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");

            String weatherContent = jsonArray.getJSONObject(0).toString();

            return new Gson().fromJson(weatherContent ,Weather.class);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }


}









