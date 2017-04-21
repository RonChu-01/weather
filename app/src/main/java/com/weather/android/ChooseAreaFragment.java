package com.weather.android;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weather.android.db.City;
import com.weather.android.db.County;
import com.weather.android.db.Province;
import com.weather.android.util.HttpUtil;
import com.weather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wb.chuyong on 2017/4/11.
 */

public class ChooseAreaFragment extends Fragment{

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    //省列表
    private List<Province> provinceList;

    //市列表
    private List<City> cityList;

    //县列表
    private List<County> countyList;

    //选中的省
    private Province selectProvince;

    //选中的市
    private City selectCity;

    //当前选中的级别
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area , container , false);

        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(getContext() , android.R.layout.simple_list_item_1 , dataList);
        listView.setAdapter(adapter);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //省级数据未加载前不会进入，不会执行if后面的代码
                if(currentLevel == LEVEL_PROVINCE){
                    selectProvince = provinceList.get(position);
                    queryCity();

                }else if (currentLevel == LEVEL_CITY){
                    selectCity = cityList.get(position);
                    queryCounty();

                    //如果选择的是县级则启动WeatherActivity
                }else if(currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();

                    //instanceof 用于判断一个对象是否属于某个实例，判断引用的碎片属于哪个活动
                    if(getActivity() instanceof MainActivity){

                        Intent intent = new Intent(getActivity() , WeatherActivity.class);
                        //通过intent传递数据
                        intent.putExtra("weather_id" , weatherId);
                        startActivity(intent);
                        getActivity().finish();

                    } else if(getActivity() instanceof WeatherActivity){

                        WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                        weatherActivity.drawerLayout.closeDrawers();
                        weatherActivity.swipeRefresh.setRefreshing(true);
                        weatherActivity.requestWeather(weatherId);

                    }


                }
            }
        });

        //返回按钮的点击事件，会判断当前listview所处的级别
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCity();

                }else if (currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        //从这里开始加载省级数据
        queryProvince();
    }

    /*
    查询全国所有的省，优先从数据库中查找，没有的去服务器拿
     */
    private void queryProvince(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);

        provinceList = DataSupport.findAll(Province.class);

        if(provinceList.size() > 0){
           dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;

            //一开始数据库是没有数据的，所以if语句不会执行，执行else语句，即从服务器获取数据
            //待再次调用queryProvince()方法时，就会执行if语句，即从数据库中查找，并显示出来
        } else {
            String address = "http://guolin.tech/api/china/";
            queryFromServer(address , "province");
        }
    }

    /*
    查询选中的省所有的市，优先从数据库中查找，如果没有去服务器上拿
     */
    private void queryCity(){

        titleText.setText(selectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        cityList = DataSupport.where("provinceId = ?"
                , String.valueOf(selectProvince.getId())).find(City.class);

        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){

                dataList.add(city.getCityName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;

        } else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/ " + provinceCode;
            queryFromServer(address , "city");
        }
    }

    private void queryCounty(){

        titleText.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ? "
                , String.valueOf(selectCity.getId())).find(County.class);

        if(countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){

                dataList.add(county.getCountyName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;

        }else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" +cityCode;
            queryFromServer(address , "county");
        }

    }

    /*
    根据传入的地址和类型，从服务器上获取省市县数据
     */
    private void queryFromServer(String address , final String type){

        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext() , "加载失败" , Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseText = response.body().string();
                boolean result = false;

                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText , selectProvince.getId());
                }else if("county".equals(type)){
                    result= Utility.handleCountyResponse(responseText , selectCity.getId());
                }

                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            } else if("city".equals(type)){
                                queryCity();
                            } else if("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    /*
    显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*
    关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }


}




