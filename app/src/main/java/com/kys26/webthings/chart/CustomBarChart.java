//package com.kys26.webthings.chart;
//
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.view.WindowManager;
//
//import com.ab.activity.AbActivity;
//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.charts.BarLineChartBase;
//import com.github.mikephil.charting.utils.Legend;
//import com.github.mikephil.charting.utils.XLabels;
//import com.github.mikephil.charting.utils.YLabels;
//import com.kys26.webthings.main.BaseActivity;
//import com.lidroid.xutils.ViewUtils;
//import com.zhangyx.MyGestureLock.R;
//import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarEntry;
//import java.util.ArrayList;
//
///**
// * Created by 徐建强 on 2015/7/27.
// */
//public class CustomBarChart extends AbActivity {
//    private BarChart hChart;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setAbContentView(R.layout.custombarchart);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        // 初始化IOC注解
//        ViewUtils.inject(this);
//
//        initView();
//        //绘制柱状图
//        barChart();
//    }
//    private void initView() {
//
//        this.setTitleText("History Rainfall Detail");
//        this.setTitleTextSize(20);
//        this.setTitleTextMargin(BaseActivity.marginToLeftTitleNormal, 0, 0, 0);
//        this.setLogo(R.drawable.button_selector_back);
//        this.setTitleLayoutBackground(R.drawable.top_bg);
//        this.setLogoLine(R.drawable.line);
//    }
//    private void barChart(){
//
//        hChart = (BarChart)findViewById(R.id.barChart);
//
//        // enable the drawing of values
//        hChart.setDrawYValues(true);
//
//        hChart.setDrawValueAboveBar(true);
//
//        hChart.setDescription("month");
//
//        // if more than 60 entries are displayed in the chart, no values will be
//        // drawn
//        hChart.setMaxVisibleValueCount(60);
//
//        // disable 3D
//        hChart.set3DEnabled(true);
//
//        // scaling can now only be done on x- and y-axis separately
//        hChart.setPinchZoom(false);
//
//        // draw shadows for each bar that show the maximum value
//        hChart.setDrawBarShadow(true);
//
//        hChart.setUnit("mm");
//
//        hChart.setDrawXLabels(true);
//
//        hChart.setDrawGridBackground(false);
//        hChart.setDrawHorizontalGrid(true);
//        hChart.setDrawVerticalGrid(false);
//        hChart.setDrawYLabels(true);
//
//        // sets the text size of the values inside the chart
//        hChart.setValueTextSize(10f);
//
//        hChart.setDrawBorder(false);
//
//        hChart.setBorderPositions(new BarLineChartBase.BorderPosition[] {BarLineChartBase.BorderPosition.LEFT,
//                BarLineChartBase.BorderPosition.RIGHT});
//
//        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
//
//        XLabels xl = hChart.getXLabels();
//        xl.setPosition(XLabels.XLabelPosition.BOTTOM);
//        xl.setCenterXLabelText(true);
//        xl.setTypeface(tf);
//
//        YLabels yl = hChart.getYLabels();
//        yl.setTypeface(tf);
//        yl.setLabelCount(8);
//        yl.setPosition(YLabels.YLabelPosition.LEFT);
//
//        hChart.setValueTypeface(tf);
//
//        // add a nice and smooth animation
//        hChart.animateY(2500);
//        setDataBar();
//
//        Legend l = hChart.getLegend();
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//        l.setFormSize(8f);
//        l.setXEntrySpace(4f);
//    }
//    private void setDataBar() {
//        //二十四小时制
//        String[] time = {"Jan","Feb","Mar","Apr","May","June","July"};
//        float[] temp = {2.0f,4.0f,10.0f,17.0f,31.0f,61.0f,108.0f};
//       //,18.0f,20.0f,22.0f,24.0f,25.0f,27.0f,26.0f,27.0f,27.0f,28.0f,27.0f,27.0f,26.0f,25.0f,23.0f,23.0f,22.0f
//
//        ArrayList<String> xVals = new ArrayList<String>();
//        for (int i = 0; i < time.length; i++) {
//            xVals.add(time[i]);
//        }
//        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
//
//        for (int i = 0; i < temp.length; i++) {
//
//            yVals1.add(new BarEntry(temp[i], i));
//        }
//        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
//        set1.setBarSpacePercent(35f);
//
//        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
//        dataSets.add(set1);
//
//        BarData dataBar = new BarData(xVals, dataSets);
//
//        hChart.setData(dataBar);
//    }
//}
