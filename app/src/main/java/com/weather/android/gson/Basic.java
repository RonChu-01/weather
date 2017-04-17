package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wb.chuyong on 2017/4/13.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public int weatherId;

    //根据basic的数据结构，定义一个内部类(返回数据的格式)
    public class Update{

        @SerializedName("loc")
        public String updateTime;

    }

    public Update update;

}
