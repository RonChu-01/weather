package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wb.chuyong on 2017/4/13.
 */

public class Suggestion {

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class Carwash{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public Carwash carwash;

    public Sport sport;

}








