//package com.kys26.webthings.permission;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.Gravity;
//import android.widget.TextView;
//
//import com.ab.activity.AbActivity;
//import com.github.mikephil.charting.charts.BarLineChartBase;
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.utils.Legend;
//import com.github.mikephil.charting.utils.XLabels;
//import com.github.mikephil.charting.utils.YLabels;
//import com.kys26.webthings.bean.HistoryDetailBean;
//import com.kys26.webthings.chart.MyMarkerView;
//import com.kys26.webthings.dialog.IntentDialog;
//import com.kys26.webthings.httpconstant.Code;
//import com.kys26.webthings.httpconstant.Path;
//import com.kys26.webthings.httpnetworks.VolleyJsonRequest;
//import com.kys26.webthings.main.BaseActivity;
//import com.kys26.webthings.method.MethodTools;
//import com.kys26.webthings.util.MyToast;
//import com.lidroid.xutils.ViewUtils;
//import com.lidroid.xutils.view.annotation.ViewInject;
//import com.zhangyx.MyGestureLock.R;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
///**
// * @function:查看历史数据
// * @Created by 徐建强 on 2015/7/24.
// */
//public class permissionDetailActivity extends AbActivity {
//
//    /**曲线图声明*/
//    private LineChart mChart;
//    /**发送后台的数据,既是从HistoryActivity中传过来的数据分配存储空间*/
//    private String timeType,nodeType,gwId,time="0",framName,gateName,nodeName,timeName;
//    /**此类男打印输出标识*/
//    private String TAG="webthings.history.permissionDetailActivity";
//    /**农场名称标签控件*/
//    @ViewInject(R.id.framName)
//    private TextView framNameText;
//    /**网关，节点类型名称标签*/
//    @ViewInject(R.id.gatewag)
//    private TextView gatewawText;
//    /**时间标签*/
//    @ViewInject(R.id.time)
//    private TextView timeText;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setAbContentView(R.layout.activity_historydetaill);
//
//        // 初始化IOC注解
//        ViewUtils.inject(this);
//        //初始化UI组件
//        initView();
//    }
//
//    /**
//     * @function:初始化UI组件
//     * @Created by 徐建强 on 2015/7/24.
//     */
//    private void initView() {
//
//       /* //设置全屏显示
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
//        this.setTitleText("历史详情");
//        this.setTitleTextSize(20);
//        this.setTitleTextMargin(BaseActivity.marginToLeftTitleNormal, 0, 0, 0);
//        this.setLogo(R.drawable.button_selector_back);
//        this.setTitleLayoutBackground(R.drawable.top_bg);
//        this.setLogoLine(R.drawable.line);
//
//        //接收上一个选择界面的删选数据，以下是要界面显示的数据
//         framName=MethodTools.session.get("farmName").toString();
//         gateName=MethodTools.session.get("gatewayName").toString();
//         nodeName=MethodTools.session.get("nodeName").toString();
//         timeName=MethodTools.session.get("timeName").toString();
//        //以下是要发送后台的数据
//         timeType=MethodTools.session.get("timeType").toString();
//         nodeType=MethodTools.session.get("nodeType").toString();
//         gwId=MethodTools.session.get("gwId").toString();
//        if(timeType.equals("1")){
//          time=MethodTools.session.get("time").toString();
//        }
//        //设置查询房间号的详情标签提示
//        framNameText.setText("养殖场:"+framName);
//        gatewawText.setText("网关:"+gateName+"。"+"节点类型:"+nodeName);
//        timeText.setText("查看方式:"+timeName);
//        //网络请求
//        requestJson(gwId, nodeType, timeType, time);
//    }
//    /**
//     * @author kys_26使用者：徐建强  on 2015-11-7
//     * @function:获后台数据
//     * 用于绘制折线图
//     * @param gwId
//     * @param nodeType
//     * @param time
//     * @param timeType
//     * @return null
//     */
//    private void requestJson(String gwId,String nodeType,String timeType,String time){
//        //设置进度条显示等待
//        IntentDialog.createDialog(permissionDetailActivity.this, "正在获取数据......").show();
//        //调用基类封装数据
//        HistoryDetailBean historyDetailBean=new HistoryDetailBean();
//        historyDetailBean.setGwId(gwId);
//        historyDetailBean.setSearchType(nodeType);
//        historyDetailBean.setSearchWay(timeType);
//        historyDetailBean.setDateTime(time);
//        historyDetailBean.setRequestType("mobile");
//        //调用静态SharedPreferences获取cookie值
//        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
//                Context.MODE_PRIVATE);
//        //开始进行发送数据
//        VolleyJsonRequest.JsonRequestWithCookie(permissionDetailActivity.this,
//                Path.host + Path.curveChart,
//                MethodTools.gson.toJson(historyDetailBean),
//                MethodTools.sPreFerCookie.getString("cookie", "null"));
//        //重写Handler中的message方法，获取数据后进行异步加载
//        MethodTools.HnadlerVOlleyJson= new Handler() {
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what== Code.SUCCESS) {
//                    //获取数据后进行处理
//                    analysis(msg.obj.toString());
//                }else if(msg.what==Code.FAILURE){
//                    //弹出进度条消失
//                    IntentDialog.dialog.cancel();
//                    MyToast.makeImgAndTextToast(permissionDetailActivity.this, getResources().getDrawable(R.drawable.tips_sry),
//                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
//                }
//            }
//        };
//    }
//    /**
//     * @author kys_26使用者：徐建强  on 2015-11-10
//     * @function:对获取后台的数据进行解析
//     * @return null
//     */
//    private void analysis(String str){
//        Log.i(TAG, str);
//        String data_y;
//        String data_x;
//        try{
//            JSONObject jsonObjectAnl=new JSONObject(str);
//            data_x=jsonObjectAnl.getString("categories");
//            data_y=jsonObjectAnl.getString("data");
//            //获取数据后，开始绘制条形曲线图
//            curveChart(data_x,data_y);
//        }catch (Exception e){
//            Log.i("json解析异常", e.toString());
//        }
//    }
//    /**
//     * @author kys_26使用者：徐建强  on 2015-11-2
//     * @function:绘制图标基本元素
//     * @param Y//Y轴数据
//     * @param X //X轴数据
//     * @return
//     */
//    private void curveChart(String X,String Y){
//
//        mChart = (LineChart) findViewById(R.id.curveChart);
//        // 设置在Y轴上是否是从0开始显示
//        mChart.setStartAtZero(false);
//        //是否在Y轴显示数据，就是曲线上的数据
//      //  mChart.setDrawYValues(true);
//        //设置网格
//        mChart.setDrawBorder(true);
//        mChart.setBorderPositions(new BarLineChartBase.BorderPosition[] {
//                BarLineChartBase.BorderPosition.BOTTOM});
//        //在chart上的右下角加描述
//        mChart.setDescription("hour");
//        //设置Y轴上的单位
//        mChart.setUnit("°C");
//        //°C
//        //设置透明度
//        mChart.setAlpha(0.8f);
//        //设置网格底下的那条线的颜色
//        mChart.setBorderColor(Color.rgb(213, 216, 214));
//        //设置Y轴前后倒置
//        mChart.setInvertYAxisEnabled(false);
//        //设置高亮显示
//        mChart.setHighlightEnabled(true);
//        //设置是否可以触摸，如为false，则不能拖动，缩放等
//        mChart.setTouchEnabled(true);
//        //设置是否可以拖拽，缩放
//        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
//        //设置是否能扩大扩小
//        mChart.setPinchZoom(true);
//        // 设置背景颜色
//        // mChart.setBackgroundColor(Color.GRAY);
//        //设置点击chart图对应的数据弹出标注
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        // define an offset to change the original position of the marker
//        mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());
//        // set the marker to the chart
//        mChart.setMarkerView(mv);
//        // enable/disable highlight indicators (the lines that indicate the
//        mChart.setHighlightIndicatorEnabled(false);
//        //设置字体格式，如正楷
//        Typeface tf = Typeface.createFromAsset(getAssets(),
//                "OpenSans-Regular.ttf");
//        mChart.setValueTypeface(tf);
//
//        XLabels xl = mChart.getXLabels();
//        xl.setPosition(XLabels.XLabelPosition.BOTTOM); // 设置X轴的数据在底部显示
//        xl.setTypeface(tf); // 设置字体
//        xl.setTextSize(10f); // 设置字体大小
//        xl.setSpaceBetweenLabels(3); // 设置数据之间的间距
//
//        YLabels yl = mChart.getYLabels();
//        // yl.setPosition(YLabelPosition.LEFT_INSIDE); // set the position
//        yl.setTypeface(tf); // 设置字体
//        yl.setTextSize(10f); // s设置字体大小
//        yl.setLabelCount(5); // 设置Y轴最多显示的数据个数
//        // 加载数据
//        setDataCurve(X,Y);
//        //从X轴进入的动画
//        mChart.animateX(4000);
//        mChart.animateY(3000);   //从Y轴进入的动画
//        mChart.animateXY(3000, 3000);    //从XY轴一起进入的动画
//
//        //设置最小的缩放
//        mChart.setScaleMinima(0.5f, 1f);
//        //设置视口
//        // mChart.centerViewPort(10, 50);
//        // get the legend (only possible after setting data)
//        Legend l = mChart.getLegend();
//        l.setForm(Legend.LegendForm.LINE);  //设置图最下面显示的类型
//        l.setTypeface(tf);
//        l.setTextSize(15);
//        l.setTextColor(Color.GRAY);
//        l.setFormSize(0f); // set the size of the legend forms/shapes
//        // 刷新图表
//        mChart.invalidate();
//        //等待进度弹出框消失
//        IntentDialog.dialog.cancel();
//    }
//    /**
//     * @author kys_26使用者：徐建强  on 2015-11-2
//     * @function:为图标填充数据
//     * @param Y//Y轴数据
//     * @param X //X轴数据
//     * @return
//     */
//    private void setDataCurve(String X,String Y) {
//
//        //先将传入的值进行切割，以适配画图方法中的数据格式要求后，再赋值画图
//        String[] Ydata =splitData(Y);
//        String[] XData =splitData(X);
//        //添加x轴的数据
//        ArrayList<String> xVals = new ArrayList<String>();
//        for (int i = 0; i < XData.length; i++) {
//            xVals.add(XData[i]);
//        }
//        //添加Y轴额数据
//        ArrayList<Entry> yVals = new ArrayList<Entry>();
//        for (int i = 0; i < Ydata.length; i++) {
//            yVals.add(new Entry(Float.parseFloat(Ydata[i]), i));
//        }
//      // create a dataset and give it a type
//        LineDataSet set1 = new LineDataSet(yVals, "");
//        set1.setDrawCubic(true);  //设置曲线为圆滑的线
//        set1.setCubicIntensity(0.2f);
//        set1.setDrawFilled(false);  //设置包括的范围区域填充颜色
//        set1.setDrawCircles(true);  //设置有圆点
//        set1.setLineWidth(2f);    //设置线的宽度
//        set1.setCircleSize(5f);   //设置小圆的大小
//        set1.setHighLightColor(Color.rgb(104, 241, 175));
//        set1.setColor(Color.rgb(105, 194, 224));    //设置曲线的颜色
//        // create a data object with the datasets
//        LineData dataCurve = new LineData(xVals,set1);
//        // set data
//        mChart.setData(dataCurve);
//    }
//    /**
//     * @author kys_26使用者：徐建强  on 2015-11-2
//     * @function:对接收到的数据进行切割处理,
//     * 返回给图表方法进行绘制曲线图
//     * @param data//数据
//     * @return
//     */
//    private String[] splitData(String data)
//    {
//        String [] dataArray=null;
//        //切割中括号数据
//        String [] dataArray1=data.split("\\[");
//        String [] dataArray2=dataArray1[1].split("\\]");
//               dataArray=dataArray2[0].split(",");
//       return dataArray;
//    }
//}