package com.weather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by wb.chuyong on 2017/4/6.
 */

public class Province extends DataSupport{

    private int id;
    private String provinceName;
    private int provinceCode;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getProvinceCode() {
        return provinceCode;
    }
}


