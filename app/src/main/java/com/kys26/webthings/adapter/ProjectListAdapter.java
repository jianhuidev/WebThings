package com.kys26.webthings.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.kys26.webthings.global.Constant;
import com.zhangyx.MyGestureLock.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 李赛鹏
 * @class 新建项目中的ListView的适配器
 * Created by Administrator on 2016/12/6.
 */

public class ProjectListAdapter extends BaseAdapter {
    public Context mContext;
    //这是传进来的价格数据
    private List<String> list;
    private String TAG = "ProjectListAdapter";
    private int type;
    /**
     * 判断是否需要重新加载
     */
    private boolean isagain = false;

    /**
     * @param context
     * @param list
     * @param Type    0:数据网关 1：视频网关
     */
    public ProjectListAdapter(Context context, List<String> list, int Type) {
        //*****************************************************************************************
        while (Constant.positionList.size() < Type + 1) {
            Log.e("mapsize", " " + Constant.positionList.size() + "  Type" + (type + 1));

            Constant.positionList.add(new HashMap<String, Object>());
        }
        //*****************************************************************************************

        this.mContext = context;
        this.list = list;
        Log.e("mapList","size:"+list.size());
        Constant.GwNodeList = new ArrayList<List>();
        this.type = Type;
    }

    public void isAgain() {
        isagain = true;
    }

    @Override
    public int getCount() {
        Log.e("mapList","size:"+list.size());
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.project_list_item, null);

            holder.Priceet = (EditText) view.findViewById(R.id.projectlist_priceet);
            holder.Idet = (EditText) view.findViewById(R.id.projectlist_idet);
            holder.Nameet = (EditText) view.findViewById(R.id.projectlist_nameet);
            holder.Deletebtn = (Button) view.findViewById(R.id.projectlist_deletebtn);
            holder.Describeet = (EditText) view.findViewById(R.id.projectlist_describeet);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Log.e(TAG, "price:" + list.get(i).toString());
        holder.Priceet.setText("单价:" + list.get(i).toString());
        holder.Deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(i);
                switch (type) {
                    case 0:
                        Constant.datapositionList.remove(i);
                        for (int j =i;j<Constant.datanum;j++){
                            if (j==Constant.datanum-1){
                                if (null != Constant.positionList.get(type).get("Idet" + String.valueOf(j))) {
                                    Constant.positionList.get(0).remove("Idet" + String.valueOf(j));
                                }
                                if (null != Constant.positionList.get(type).get("Nameet" + String.valueOf(j))) {
                                    Constant.positionList.get(0).remove("Nameet" + String.valueOf(j));
                                }
                                if (null != Constant.positionList.get(type).get("Describeet" + String.valueOf(j))) {
                                    Constant.positionList.get(0).remove("Describeet" + String.valueOf(j));
                                }
//                                Constant.positionList.get(0).remove("Price" + String.valueOf(j));
                            }else {
                                if (null != Constant.positionList.get(type).get("Idet" + String.valueOf(j+1))) {
                                    Constant.positionList.get(0).put("Idet" + String.valueOf(j),Constant.positionList.get(0).get("Idet" + String.valueOf(j+1)));
                                }else {
                                    Constant.positionList.get(0).remove("Idet" + String.valueOf(j));
                                }
                                if (null != Constant.positionList.get(type).get("Nameet" + String.valueOf(j+1))) {
                                    Constant.positionList.get(0).put("Nameet" + String.valueOf(j),Constant.positionList.get(0).get("Nameet" + String.valueOf(j+1)));
                                }else {
                                    Constant.positionList.get(0).remove("Nameet" + String.valueOf(j));
                                }
                                if (null != Constant.positionList.get(type).get("Describeet" + String.valueOf(j+1))) {
                                    Constant.positionList.get(0).put("Describeet" + String.valueOf(j),Constant.positionList.get(0).get("Describeet" + String.valueOf(j+1)));
                                }else {
                                    Constant.positionList.get(0).remove("Describeet" + String.valueOf(j));
                                }
                            }
                        }
                        Constant.datanum--;
                        break;
                    case 1:
                        Constant.videopositionList.remove(i);
                        for (int j =i;j<Constant.videonum;j++){
                            if (j==Constant.videonum-1){
                                if (null != Constant.positionList.get(type).get("Idet" + String.valueOf(j))) {
                                    Constant.positionList.get(1).remove("Idet" + String.valueOf(j));
                                }
                                if (null != Constant.positionList.get(type).get("Nameet" + String.valueOf(j))) {
                                    Constant.positionList.get(1).remove("Nameet" + String.valueOf(j));
                                }
                                if (null != Constant.positionList.get(type).get("Describeet" + String.valueOf(j))) {
                                    Constant.positionList.get(1).remove("Describeet" + String.valueOf(j));
                                }
//                                Constant.positionList.get(1).remove("Price" + String.valueOf(j));
                            }else {
                                if (null != Constant.positionList.get(type).get("Idet" + String.valueOf(j+1))) {
                                    Constant.positionList.get(1).put("Idet" + String.valueOf(j),Constant.positionList.get(0).get("Idet" + String.valueOf(j+1)));
                                }else {
                                    Constant.positionList.get(1).remove("Idet" + String.valueOf(j));
                                }
                                if (null != Constant.positionList.get(type).get("Nameet" + String.valueOf(j+1))) {
                                    Constant.positionList.get(1).put("Nameet" + String.valueOf(j),Constant.positionList.get(0).get("Nameet" + String.valueOf(j+1)));
                                }else {
                                    Constant.positionList.get(1).remove("Nameet" + String.valueOf(j));
                                }
                                if (null != Constant.positionList.get(type).get("Describeet" + String.valueOf(j+1))) {
                                    Constant.positionList.get(1).put("Describeet" + String.valueOf(j),Constant.positionList.get(0).get("Describeet" + String.valueOf(j+1)));
                                }else {
                                    Constant.positionList.get(1).remove("Describeet" + String.valueOf(j));
                                }
                            }
                        }
                        Constant.videonum--;
                        break;
                }
                notifyDataSetChanged();
            }
        });
        Log.d("map", "position::" + i);
        holder.Idet.setTag(i);
        holder.Nameet.setTag(i);
        holder.Priceet.setTag(i);
        holder.Deletebtn.setTag(i);
        holder.Describeet.setTag(i);
//        if (isagain){
        Log.e("map", "isagain");
        if (!(Constant.positionList.size() < type + 1)) {
            if (null != Constant.positionList.get(type).get("Idet" + String.valueOf(holder.Idet.getTag().toString()))) {
                holder.Idet.setText(Constant.positionList.get(type).get("Idet" + String.valueOf(holder.Idet.getTag().toString())).toString());
            } else {
//                if (i != 0 && null != Constant.positionList.get(type).get("Idet" +
//                        String.valueOf(Integer.valueOf(holder.Idet.getTag().toString()) - 1))) {
//                    //不用看懂这句话，恩，就是这样
//                    //总体来说就是ID++
//                    holder.Idet.setText(String.valueOf(Integer.valueOf(Constant.positionList.get(type).get("Idet" +
//                            String.valueOf(Integer.valueOf(holder.Idet.getTag().toString()) - 1)).toString()) + 1));
//                } else {
                    holder.Idet.setText("");
//                }
            }
            if (null != Constant.positionList.get(type).get("Nameet" + String.valueOf(holder.Idet.getTag().toString()))) {
                holder.Nameet.setText(Constant.positionList.get(type).get("Nameet" + String.valueOf(holder.Idet.getTag().toString())).toString());
            } else {
                holder.Nameet.setText("");
            }
            if (null != Constant.positionList.get(type).get("Describeet" + String.valueOf(holder.Idet.getTag().toString()))) {
                holder.Describeet.setText(Constant.positionList.get(type).get("Describeet" + String.valueOf(holder.Idet.getTag().toString())).toString());
            } else {
                holder.Describeet.setText("");
            }
            if (i == getCount()) {
                isagain = false;
            }
        }
//        }
//        else {
        Log.e("map", "notisagain");


        editListener(holder.Idet, i, 0);
        editListener(holder.Nameet, i, 1);
        editListener(holder.Describeet, i, 2);
//        }
        return view;
    }

    /***
     * @param editText 传进来具体是哪一个的editText
     * @param position 传进来具体是listView的第几个item
     * @param a        传进来具体是那个editText
     * @fuction editText的监听
     */
    public void editListener(final EditText editText, final int position, final int a) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                switch (type) {
                    case 0:
                        judgeEditText(a, Integer.valueOf(editText.getTag().toString()), editText, type);
                        break;
                    case 1:
                        judgeEditText(a, Integer.valueOf(editText.getTag().toString()), editText, type);
                        break;
                }
            }
        });
        if (type == 0) {
            if (Integer.valueOf(editText.getTag().toString()) + 1 > Constant.datanum) {
                Constant.datanum = Integer.valueOf(editText.getTag().toString()) + 1;
            }
        } else if (type == 1) {
            if (Integer.valueOf(editText.getTag().toString()) + 1 > Constant.videonum) {
                Constant.videonum = Integer.valueOf(editText.getTag().toString()) + 1;
            }
        }
    }

    //判断是哪一个editText
    public void judgeEditText(int a, int position, EditText editText, int type) {
        Log.d("map", "a:" + a + "  position:" + position);
        switch (a) {
            case 0:
                if (editText.getText().toString().length() == 0) {
                    Constant.positionList.get(type).remove("Idet" + position);
                } else {
                    Constant.positionList.get(type).put("Idet" + position, editText.getText().toString());
                }
                //Constant.datapositionList.add(position,map);
                break;
            case 1:
                if (editText.getText().toString().length() == 0) {
                    Constant.positionList.get(type).remove("Nameet" + position);
                } else {
                    Constant.positionList.get(type).put("Nameet" + position, editText.getText().toString());
                }
                break;
            case 2:
                if (editText.getText().toString().length() == 0) {
                    Constant.positionList.get(type).remove("Describeet" + position);
                } else {
                    Constant.positionList.get(type).put("Describeet" + position, editText.getText().toString());
                }
                break;
        }

        Log.e("map??", " " + Constant.positionList.get(type).toString());
    }

    class ViewHolder {
        EditText Idet;
        EditText Nameet;
        EditText Priceet;
        EditText Describeet;
        Button Deletebtn;
    }
}

