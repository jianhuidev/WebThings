package com.kys26.webthings.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kys-36 on 2017/5/23.
 *
 * @param
 * @author
 * @function
 */

public class HistoryData {
    public String time;//日期
    public String NH3;//NH3
    public String temp;//温度
    public String hum;//湿度
    public String sunPower;//光照强度

    public static List<HistoryData> analysis(JSONObject jb){
        List<HistoryData> list = new ArrayList<>();
        try {
            JSONObject jsonObject = jb.getJSONObject("data");
            JSONArray jsonArray = jsonObject.getJSONArray("shidu");
            for (int i =0;i<jsonArray.length();i++){
                HistoryData historyData1 = new HistoryData();
                historyData1.setTime(jsonArray.getJSONObject(i).getString("time"));
                historyData1.setHum(jsonArray.getJSONObject(i).getString("平均"));
                list.add(historyData1);
            }
            jsonArray = jsonObject.getJSONArray("anqi");
            for (int i =0;i<jsonArray.length();i++){
                list.get(i).setNH3(jsonArray.getJSONObject(i).getString("平均"));
            }
            jsonArray = jsonObject.getJSONArray("wendu");
            for (int i =0;i<jsonArray.length();i++){
                list.get(i).setTemp(jsonArray.getJSONObject(i).getString("平均"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNH3() {
        return NH3;
    }

    public void setNH3(String NH3) {
        this.NH3 = NH3;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHum() {
        return hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public String getSunPower() {
        return sunPower;
    }

    public void setSunPower(String sunPower) {
        this.sunPower = sunPower;
    }
}
