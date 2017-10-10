package com.kys26.webthings.history;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.kys26.webthings.adapter.DateListAdapter;
import com.kys26.webthings.chart.MyMarkerView;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.HistoryData;
import com.kys26.webthings.util.DataUtil;
import com.kys26.webthings.util.MyToast;
import com.lidroid.xutils.ViewUtils;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/9.
 */

public class HistoryActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.NH3)
    RadioButton mNH3;
    @InjectView(R.id.temp)
    RadioButton mTemp;
    @InjectView(R.id.hum)
    RadioButton mHum;
    @InjectView(R.id.sunpower)
    RadioButton mSunpower;
    @InjectView(R.id.type_group)
    RadioGroup mTypeGroup;
    @InjectView(R.id.day)
    RadioButton mDay;
    @InjectView(R.id.month)
    RadioButton mMonth;
    @InjectView(R.id.year)
    RadioButton mYear;
    @InjectView(R.id.date_group)
    RadioGroup mDateGroup;
    private LineChart mChart;
    private List<String> date = new ArrayList<>();
    private StringBuffer xYing, yYing;
    private List<Float> data = new ArrayList<>();
    private RecyclerView date_list;
    private FrameLayout left_btn;
    private int farmPosition = -1;
    private DateListAdapter mAdapter;
    private String TAG;
    private final int week = 1;//最近一周
    private final int month = 2;//最近一月
    private final int year = 3;//最近一年
    private int now = week;//现在的

    /**
     * 存儲SharedPreferences
     * */
    private SharedPreferences day_pref;
    private SharedPreferences.Editor day_editor;

    private SharedPreferences month_pref;
    private SharedPreferences.Editor month_editor;

    private SharedPreferences year_pref;
    private SharedPreferences.Editor year_editor;
    private String jb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_history);
        ButterKnife.inject(this);
        // 初始化IOC注解
        ViewUtils.inject(this);
        day_pref = getSharedPreferences("day_data",MODE_PRIVATE);
        month_pref = getSharedPreferences("month_data",MODE_PRIVATE);
        year_pref = getSharedPreferences("year_data",MODE_PRIVATE);
        TAG = getClass().getName();
        initHandler();
        mDay.setChecked(true);
        mNH3.setChecked(true);
        initRegist(week);
        setTitle("历史记录");
        setRightBtnVisible(false);
        addRightWidget(1);
        initView();
    }

    /**
     * 初始化请求
     */
    private void initRegist(int i) {
        Intent in = getIntent();
        farmPosition = in.getIntExtra("position",0);
        JSONObject jsonb = new JSONObject();
        try {
            jsonb.put("gwid",in.getStringExtra("gwid"));
            jsonb.put("searchType",i);
            /**
             * 执行 天，月，年 的数据，请求，存储
             * */
            if (mDateGroup.getCheckedRadioButtonId() == R.id.day){
                if ( (day_pref.getString("mark","0")) .equals("0") ){
                    doRegist(Path.host+Path.URL_HISTORY,jsonb,History);
                    dismissProgress();
                    Log.e("第一次","--------------------第一次，天天天，请求");
                }else {
                    jb = day_pref.getString("jb","0");
                    Message message = new Message();
                    message.what = History;
                    message.obj = jb;
                    MethodTools.handlerJson.sendMessage(message);
                    Log.e("二次","-------------天天天，获取储存的数据");
                }
            }else if (mDateGroup.getCheckedRadioButtonId() == R.id.month){
                if ( (month_pref.getString("mark","0")) .equals("0") ){
                    doRegist(Path.host+Path.URL_HISTORY,jsonb,History);
                    dismissProgress();
                    Log.e("第一次","------------------第一次，月月月，请求");
                }else {
                    jb = month_pref.getString("jb","0");
                    Message message = new Message();
                    message.what = History;
                    message.obj = jb;
                    MethodTools.handlerJson.sendMessage(message);
                    Log.e("二次","------------------月月月，获取储存的数据");
                }
            }else if (mDateGroup.getCheckedRadioButtonId() == R.id.year){

                if ( (year_pref.getString("mark","0")) .equals("0") ){
                    doRegist(Path.host+Path.URL_HISTORY,jsonb,History);
                    dismissProgress();
                    Log.e("第一次","----------------第一次，年年年，请求");
                }else {
                    jb = year_pref.getString("jb","0");
                    Message message = new Message();
                    message.what = History;
                    message.obj = jb;
                    MethodTools.handlerJson.sendMessage(message);
                    Log.e("二次","---------------年年年，获取储存的数据");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        this.finish();
    }

    @Override
    protected void onClickRight() {
        super.onClickRight();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_history;
    }

    /**
     * 更新 NH3,temp,hum,sunpower
     */
    private void changeUI(){
        data = new ArrayList<>();
        date = new ArrayList<>();
        for (int i = 0; i < MethodTools.farmDataList.get(farmPosition).getHistoryDatas().size(); i++) {
            date.add(DataUtil.changeFormat(MethodTools.farmDataList.get(farmPosition).getHistoryDatas().get(i).getTime(),now));
            switch (mTypeGroup.getCheckedRadioButtonId()){
                case R.id.NH3:
                    data.add(Float.parseFloat(MethodTools.farmDataList.get(farmPosition).getHistoryDatas().get(i).getNH3()));
                    break;
                case R.id.temp:
                    data.add(Float.parseFloat(MethodTools.farmDataList.get(farmPosition).getHistoryDatas().get(i).getTemp()));
                    break;
                case R.id.hum:
                    data.add(Float.parseFloat(MethodTools.farmDataList.get(farmPosition).getHistoryDatas().get(i).getHum()));
                    break;
                case R.id.sunpower:
//                    data.add(Float.parseFloat(MethodTools.farmDataList.get(farmPosition).getHistoryDatas().get(i).getTemp()));
                    break;
            }
        }
        initChart(data, date);
        mAdapter = new DateListAdapter(HistoryActivity.this, date, data);
        date_list.setAdapter(mAdapter);
    }

    /**
     * @function 初始化view
     */
    private void initView() {
        setEnableRefresh(false,null);
//        mNH3.setChecked(true);
//        mDay.setChecked(true);

        mTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                changeUI();
            }
        });

        mDateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.day:
                        now = week;
                        initRegist(week);
                        break;
                    case R.id.month:
                        now = month;
                        initRegist(month);
                        break;
                    case R.id.year:
                        now = year;
                        initRegist(year);
                        break;
                }
            }
        });

        data = new ArrayList<>();
        date = new ArrayList<>();
        for (int i = 0; i < MethodTools.farmDataList.get(farmPosition).getHistoryDatas().size(); i++) {
            date.add(DataUtil.changeFormat(MethodTools.farmDataList.get(farmPosition).getHistoryDatas().get(i).getTime(),now));
            switch (mTypeGroup.getCheckedRadioButtonId()){
                case R.id.NH3:
                    data.add(Float.parseFloat(MethodTools.farmDataList.get(farmPosition).getHistoryDatas().get(i).getNH3()));
                    break;
                case R.id.temp:
                    data.add(Float.parseFloat(MethodTools.farmDataList.get(farmPosition).getHistoryDatas().get(i).getTemp()));
                    break;
                case R.id.hum:
                    data.add(Float.parseFloat(MethodTools.farmDataList.get(farmPosition).getHistoryDatas().get(i).getHum()));
                    break;
                case R.id.sunpower:
//                    data.add(Float.parseFloat(MethodTools.farmDataList.get(farmPosition).getHistoryDatas().get(i).getTemp()));
                    break;
            }
        }
        initChart(data, date);
        date_list = (RecyclerView) findViewById(R.id.date_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        date_list.setLayoutManager(linearLayoutManager);
        mAdapter = new DateListAdapter(this, date, data);
        date_list.setAdapter(mAdapter);
        left_btn = (FrameLayout) findViewById(R.id.left_btn);
        left_btn.setOnClickListener(this);
    }
    private void initHandler() {
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.handlerJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i(TAG,"handler：获取到");
                if (msg.what ==History) {
                    try {
                        /**        ------------------------------------                           */
                        if (mDateGroup.getCheckedRadioButtonId() == R.id.day){
                            /**天天天天天                                               天天天天*/
                            if (day_pref.getString("mark","0").equals("day")){//已请求flag 为true
                                JSONObject jb = new JSONObject(msg.obj.toString());
                                MethodTools.farmDataList.get(farmPosition).setHistoryDatas(HistoryData.analysis(jb));
                                Log.e("二次"+day_pref.getString("mark","0")," -----------  天天天");
                            }else {//首次
                                day_editor = day_pref.edit();
                                day_editor.putString("jb",msg.obj.toString());
                                day_editor.putString("mark","day");
                                day_editor.apply();
                                JSONObject jb = new JSONObject(msg.obj.toString());
                                MethodTools.farmDataList.get(farmPosition).setHistoryDatas(HistoryData.analysis(jb));
                                Log.e("一次"+day_pref.getString("mark","0"),"--------天，存储操作");
                            }
                            /**天天天天天                                               天天天天天*/
                        }else if (mDateGroup.getCheckedRadioButtonId() == R.id.month){
                            /**月月月                                                       月月月*/
                            if (month_pref.getString("mark","0").equals("month")){//已请求flag 为true
                                JSONObject jb = new JSONObject(msg.obj.toString());
                                MethodTools.farmDataList.get(farmPosition).setHistoryDatas(HistoryData.analysis(jb));
                                Log.e("二次"+month_pref.getString("mark","0")," --------  月月月");
                            }else {//首次
                                month_editor = month_pref.edit();
                                month_editor.putString("jb",msg.obj.toString());
                                month_editor.putString("mark","month");
                                month_editor.apply();
                                JSONObject jb = new JSONObject(msg.obj.toString());
                                MethodTools.farmDataList.get(farmPosition).setHistoryDatas(HistoryData.analysis(jb));
                                Log.e("一次"+month_pref.getString("mark","0"),"-----------月，存储操作");
                            }
                            /**月月月                                                       月月月*/
                        }else if (mDateGroup.getCheckedRadioButtonId() == R.id.year){
                            /**年年年                                                       年年年*/
                            if (year_pref.getString("mark","0").equals("year")){//已请求flag 为true
                                JSONObject jb = new JSONObject(msg.obj.toString());
                                MethodTools.farmDataList.get(farmPosition).setHistoryDatas(HistoryData.analysis(jb));
                                Log.e("二次"+year_pref.getString("mark","0")," ----------  年年年");
                            }else {//首次
                                year_editor = year_pref.edit();
                                year_editor.putString("jb",msg.obj.toString());
                                year_editor.putString("mark","month");
                                year_editor.apply();
                                JSONObject jb = new JSONObject(msg.obj.toString());
                                MethodTools.farmDataList.get(farmPosition).setHistoryDatas(HistoryData.analysis(jb));
                                Log.e("一次"+month_pref.getString("mark","0"),"-----------年，存储操作");
                            }
                            /**年年年                                                       年年年*/
                        }
                        /**        ------------------------------------                           */
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    changeUI();
                }else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    MyToast.makeImgAndTextToast(HistoryActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                    MethodTools.state = false;
                }
                dismissDialog();
            }
        };
    }
    /**
     * @function 初始化chartview
     */
    private void initChart(List<Float> data, List<String> date) {
        mChart = (LineChart) findViewById(R.id.area_chart);
//        mChart.setSpaceBetweenLabels(1);
        xYing = new StringBuffer();
        yYing = new StringBuffer();
        // 设置在Y轴上是否是从0开始显示
        mChart.setStartAtZero(true);
        // 是否在Y轴显示数据，就是曲线上的数据
        mChart.setDrawYValues(true);
        // 设置网格
//        mChart.setDrawBorder(false);
//        mChart.setBorderPositions(new BarLineChartBase.BorderPosition[]{BarLineChartBase.BorderPosition.BOTTOM});
        // 在chart上的右下角加描述
        mChart.setDescription("");
        // 设置Y轴上的单位
        mChart.setUnit("");
        // 设置透明度
        mChart.setAlpha(0.8f);
        // 设置网格底下的那条线的颜色
        mChart.setBorderColor(Color.TRANSPARENT);
        // 设置Y轴前后倒置
        mChart.setInvertYAxisEnabled(false);
        // 设置高亮显示
        mChart.setHighlightEnabled(true);
        // 设置是否可以触摸，如为false，则不能拖动，缩放等
        mChart.setTouchEnabled(true);
        // 设置是否可以拖拽，缩放
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setValueTextColor(Color.WHITE);
        // 设置是否能扩大扩小
        mChart.setPinchZoom(true);
        // 设置背景颜色
        mChart.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
        //设置是否显示表格颜色
        mChart.setDrawGridBackground(false);
        // 设置点击chart图对应的数据弹出标注
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        // define an offset to change the original position of the marker
        // (optional)
        mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());
        // set the marker to the chart(意为设置每个数据点的特殊标签,当手指触摸到数据点时显示)
        mChart.setMarkerView(mv);
        // enable/disable highlight indicators (the lines that indicate the
        // highlighted Entry)
        mChart.setHighlightIndicatorEnabled(false);
        // 设置字体格式，如正楷
        XLabels xl = mChart.getXLabels();
        // xl.setAvoidFirstLastClipping(true);
        // xl.setAdjustXLabels(true);
        xl.setPosition(XLabels.XLabelPosition.BOTTOM); // 设置X轴的数据在底部显示
        xl.setTextSize(13f); // 设置字体大小
        xl.setTextColor(Color.WHITE);
//        xl.setTextColor(R.color.white);
        xl.setSpaceBetweenLabels(3); // 设置数据之间的间距

        YLabels yl = mChart.getYLabels();
//        yl.setPosition(null);
        // yl.setPosition(YLabelPosition.LEFT_INSIDE); // set the position
        yl.setTextSize(10f); // s设置字体大小
        yl.setLabelCount(5); // 设置Y轴最多显示的数据个数
        yl.setTextColor(Color.WHITE);
//        yl.setTextColor(R.color.white);
        // 加载数据
        setData(data, date);
        // 设置最小的缩放
        mChart.setScaleMinima(0.5f, 1f);
        // 设置视口
        // mChart.centerViewPort(10, 50);

        // get the legend (only possible after setting data)
        mChart.setDrawLegend(false); //是否显示左下角的东西
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE); // 设置图最下面显示的类型
        l.setTextSize(15);
        l.setTextColor(Color.WHITE);
        l.setFormSize(30f); // set the size of the legend forms/shapes
        // 刷新图表
        mChart.invalidate();
    }

    private void setData(List<Float> Linedata, List<String> date) {
        // 获取系统当前时间
//        Calendar c = Calendar.getInstance();
//        String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
//        String minute = Integer.toString(c.get(Calendar.MINUTE));
//        String second = Integer.toString(c.get(Calendar.SECOND));
        // 这个方法是自己想出来的，实时动态的后缀数据适应于画图库
//        xYing.append(hour + ":" + minute + ":" + second + "/");
//        yYing.append(Ying + "/");
        // 将切割的数据放入数组，准备画图
//        String[] aa = xYing.toString().split("/");
//        String[] bb = yYing.toString().split("/");
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < date.size(); i++) {
            xVals.add(date.get(i));
        }
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < Linedata.size(); i++) {
            yVals.add(new Entry(Linedata.get(i), i));
        }
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "氨气浓度");//这里第二个值是legend(图例)的值
        set1.setDrawCubic(true); // 设置曲线为圆滑的线
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(false); // 设置包括的范围区域填充颜色
        set1.setDrawCircles(true); // 设置有圆点
        set1.setLineWidth(2f); // 设置线的宽度
        set1.setCircleSize(5f); // 设置小圆的大小
        set1.setCircleColor(Color.WHITE);
        set1.setHighLightColor(Color.WHITE);
        set1.setColor(Color.WHITE); // 设置曲线的颜色
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets
        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        // set data
        mChart.setData(data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
        }
    }
    /**
     * 当该活动完全不可见的时候，将SharedPreferences 存储的数据清空
     * */
     @Override
    protected void onStop() {
         day_pref.edit().clear().apply();
         Log.e("天","------------------删除数据");
         month_pref.edit().clear().apply();
         Log.e("月","------------------删除数据");
         year_pref.edit().clear().apply();
         Log.e("年","------------------删除数据");
         Log.e("onDestroyonDestroy","清除，删除存的了");
         super.onStop();
    }
}
