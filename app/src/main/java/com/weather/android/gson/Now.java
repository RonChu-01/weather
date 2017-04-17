package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wb.chuyong on 2017/4/13.
 */

public class Now {

    @SerializedName("tmp")
    public String tmperature;

    public class More{

        @SerializedName("txt")
        public String info;

    }

    @SerializedName("cond")
    public More more;

}
