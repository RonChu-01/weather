package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wb.chuyong on 2017/4/13.
 */

public class Forecast {

    public class Temperature{

        public String max;
        public String min;

    }

    public class More{

        @SerializedName("txt_d")
        public String info;

    }

    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

}
