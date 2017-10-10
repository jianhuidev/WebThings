package com.kys26.webthings.query;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.kys26.webthings.chart.MyMarkerView;
import com.kys26.webthings.method.MethodTools;
import com.zhangyx.MyGestureLock.R;

import java.util.ArrayList;

/**
 * @author 李赛鹏
 * @fuction 折线图
 * Created by kys_9 on 2016/9/24.
 */
public class Chart extends Activity {
    private LineChart temp_chart, hum_chart;
    String[] temp_x;
    String[] temp_y;
    String[] hum_x;
    String[] hum_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);


        Log.i("tag", "what" + "temp" + MethodTools.tempList.size());
        temp_x = new String[MethodTools.tempList.size()];
        temp_y = new String[MethodTools.tempList.size()];
        for (int i = 0; i < MethodTools.tempList.size(); i++) {
            String aa[];
            temp_x[i] = MethodTools.tempList.get(i).get("time").toString();
            Log.e("chart",""+temp_x[i]);
            temp_y[i] = MethodTools.tempList.get(i).get("ave").toString();
            aa = splitData(temp_x[i]);
            Log.e("chart",""+temp_y[i]);
//            temp_x[i] = aa[1] + "-" + aa[2];
            Log.i("tag", " Temp " + aa[1] + "-" + aa[2]);
        }
        Log.i("tag", "what" + "hum");
        hum_x = new String[MethodTools.humList.size()];
        hum_y = new String[MethodTools.humList.size()];
        for (int i = 0; i < MethodTools.humList.size(); i++) {
            String aa[];
            hum_x[i] = MethodTools.humList.get(i).get("time").toString();
            hum_y[i] = MethodTools.humList.get(i).get("ave").toString();
            aa = splitData(temp_x[i]);
            hum_x[i] = aa[1] + "-" + aa[2];
            Log.i("tag", " Hum " + hum_x[i] + " " + hum_y[i]);
        }

        temp_chart = (LineChart) findViewById(R.id.tempChart);
        hum_chart = (LineChart) findViewById(R.id.humChart);
        setChart(temp_chart, 1);
        setChart(hum_chart, 2);
    }

    /**
     * @param data//数据
     * @return
     * @author kys_26使用者：徐建强  on 2015-11-2
     * @function:对接收到的数据进行切割处理, 返回给图表方法进行绘制曲线图
     */
    private String[] splitData(String data) {
        String[] dataArray = null;
        //切割中括号数据
        String[] dataArray1 = data.split("-");
//        String [] dataArray2=dataArray1[1].split("-");
//        dataArray = dataArray1[0].split(",");
        return dataArray1;
    }

    public void setChart(LineChart Chart, int what) {

        // 设置在Y轴上是否是从0开始显示
        Chart.setStartAtZero(false);
        //是否在Y轴显示数据就是曲线上的数据
        // Chart.setDrawYValues(true);
        //设置网格
        Chart.setDrawBorder(true);
        Chart.setBorderPositions(new BarLineChartBase.BorderPosition[]{
                BarLineChartBase.BorderPosition.BOTTOM});
        //在chart上的右下角加描述
        Chart.setDescription("hour");

        //°C
        //设置透明度
        Chart.setAlpha(0.8f);
        //设置网格底下的那条线的颜色
        Chart.setBorderColor(Color.rgb(213, 216, 214));
        //设置Y轴前后倒置
        Chart.setInvertYAxisEnabled(false);
        //设置高亮显示
        Chart.setHighlightEnabled(true);
        //设置是否可以触摸，如为false，则不能拖动，缩放等
        Chart.setTouchEnabled(true);
        //设置是否可以拖拽，缩放
        Chart.setDragEnabled(true);
        Chart.setScaleEnabled(true);
        //设置是否能扩大扩小
        Chart.setPinchZoom(true);
        // 设置背景颜色
        // Chart.setBackgroundColor(Color.GRAY);
        //设置点击chart图对应的数据弹出标注
        MyMarkerView mv = new MyMarkerView(Chart.this, R.layout.custom_marker_view);
        // define an offset to change the original position of the marker
        mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());
        // set the marker to the chart
        Chart.setMarkerView(mv);
        // enable/disable highlight indicators (the lines that indicate the
        Chart.setHighlightIndicatorEnabled(false);
        //设置字体格式，如正楷
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "OpenSans-Regular.ttf");
        Chart.setValueTypeface(tf);
// 加载数据

        XLabels xl = Chart.getXLabels();
        xl.setPosition(XLabels.XLabelPosition.BOTTOM); // 设置X轴的数据在底部显示
        xl.setTypeface(tf); // 设置字体
        xl.setTextSize(10f); // 设置字体大小
        xl.setSpaceBetweenLabels(3); // 设置数据之间的间距

        YLabels yl = Chart.getYLabels();
        // yl.setPosition(YLabelPosition.LEFT_INSIDE); // set the position
        yl.setTypeface(tf); // 设置字体
        yl.setTextSize(10f); // s设置字体大小
        yl.setLabelCount(5); // 设置Y轴最多显示的数据个数
        String[] _x;
        String[] _y;
        if (what == 1) {
            //设置Y轴上的单位
            Chart.setUnit("°C");
            setDataCurve(temp_x, temp_y, Chart);
        } else if (what == 2) {
            //设置Y轴上的单位
            Chart.setUnit("°H");
            // _y=HUM_Y;
            setDataCurve(hum_x, hum_y, Chart);
        }
        //从X轴进入的动画
        Chart.animateX(4000);
        Chart.animateY(3000);   //从Y轴进入的动画
        Chart.animateXY(3000, 3000);    //从XY轴一起进入的动画
        //设置最小的缩放
        Chart.setScaleMinima(0.5f, 1f);
        //设置视口
        // Chart.centerViewPort(10, 50);
        // get the legend (only possible after setting data)
        Legend l = Chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);  //设置图最下面显示的类型
        l.setTypeface(tf);
        l.setTextSize(15);
        l.setTextColor(Color.GRAY);
        l.setFormSize(0f); // set the size of the legend forms/shapes
        // 刷新图表
        Chart.invalidate();
        //等待进度弹出框消失
        // IntentDialog.dialog.cancel();
    }

    /**
     * @return
     * @author kys_26使用者：徐建强  on 2015-11-2
     * @function:为图标填充数据
     */
    private void setDataCurve(String[] _x, String[] _y, LineChart Chart) {
        Log.i("Chart1", String.valueOf(Chart.getId()));
        //先将传入的值进行切割，以适配画图方法中的数据格式要求后，再赋值画图
        String[] Ydata = _y;
        String[] XData = _x;
        Log.e("Ydata", " " + Ydata[0]);
        Log.e("Xdata", " " + XData[0]);
        //添加x轴的数据
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < XData.length; i++) {
            xVals.add(XData[i]);
        }
        //添加Y轴额数据
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < Ydata.length; i++) {
            yVals.add(new Entry(Float.parseFloat(Ydata[i]), i));
        }
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "");
        set1.setDrawCubic(true);  //设置曲线为圆滑的线
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(false);  //设置包括的范围区域填充颜色
        set1.setDrawCircles(true);  //设置有圆点
        set1.setLineWidth(2f);    //设置线的宽度
        set1.setCircleSize(5f);   //设置小圆的大小
        set1.setHighLightColor(Color.rgb(104, 241, 175));
        set1.setColor(Color.rgb(105, 194, 224));    //设置曲线的颜色
        // create a data object with the datasets
        // Log.i(TAG,"x轴:"+xVals.get(0)+"y轴:"+yVals.get(0));
        LineData dataCurve = new LineData(xVals, set1);
        // set data
        Chart.setData(dataCurve);
    }
}
