package com.kys26.webthings.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kys26.webthings.global.Constant;
import com.kys26.webthings.method.MethodTools;
import com.zhangyx.MyGestureLock.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 李赛鹏
 * @class 新建节点中的二级列表
 * Created by Administrator on 2016/12/18.
 */

public class ExpandListAdapter extends BaseExpandableListAdapter {
    Context mContext;
    private List<HashMap<String, Object>> list;
    private int type;//0:数据网管  1：视频网关
    // private List<ArrayList> childlist;
    private List<ArrayList<HashMap<String, Object>>> childposition;

    public ExpandListAdapter(Context context, List<HashMap<String, Object>> list, List<ArrayList<HashMap<String, Object>>> childposition, int Type) {
        this.mContext = context;
        this.list = list;
        this.type = Type;
        this.childposition = childposition;
    }

    @Override
    public int getGroupCount() {
//        Log.e("mapLList"," "+Constant.pricelist.size());
        if (type==0) {
            return Constant.pricelist.size()-1;
        }else {
            return 1;
        }
    }

    @Override
    public int getChildrenCount(int groupposition) {
//        Log.e("getChildrenCount", "childsize:" + Constant.childposition.get(groupposition).size());
        if (type==0) {
            return Constant.childposition.get(groupposition).size();
        }else {
            Log.e("expand",Constant.childposition.get(7).toString());
            return Constant.childposition.get(7).size();
        }

    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        if (type==0) {
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertview, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertview == null) {
            holder = new GroupViewHolder();
            convertview = LayoutInflater.from(mContext).inflate(R.layout.addnodegroup, null);
            holder.groupimg = (ImageView) convertview.findViewById(R.id.group_additemiv);
            holder.grouptv = (TextView) convertview.findViewById(R.id.group_ndname);
            convertview.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertview.getTag();
        }
        holder.grouptv.setTextColor(Color.BLACK);
        if (type==0) {
            holder.groupimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.childposition.get(groupPosition).add(newHashmap(groupPosition));
                    Message message = new Message();
                    message.what=0;
                    message.obj=groupPosition;
                    MethodTools.listHandler.sendMessage(message);
                }
            });
            holder.grouptv.setText(list.get(groupPosition).get("name").toString() + "(设备编码范围:" + list.get(groupPosition).get("idbegin") + "-" + list.get(groupPosition).get("idend") + ")");
        }else {
            holder.grouptv.setText(list.get(7).get("name").toString() + "(设备编码范围:" + list.get(7).get("idbegin") + "-" + list.get(7).get("idend") + ")");
            holder.groupimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.childposition.get(7).add(newHashmap(7));
                    Message message = new Message();
                    message.what=0;
                    message.obj=7;
                    MethodTools.listHandler.sendMessage(message);
                }
            });
        }

        return convertview;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertview, ViewGroup parent) {
        ChildViewHolder holder  = new ChildViewHolder();
        if (convertview == null) {
            convertview = LayoutInflater.from(mContext).inflate(R.layout.addnodechild, null);
            holder.childidet = (EditText) convertview.findViewById(R.id.addnodechild_idet);
            holder.childnameet = (EditText) convertview.findViewById(R.id.addnodechild_nameet);
            holder.childdescribeet = (EditText) convertview.findViewById(R.id.addnodechild_describeet);
            holder.childpriceet = (EditText) convertview.findViewById(R.id.addnodechild_priceet);
            holder.childedelbtn = (Button) convertview.findViewById(R.id.addnodechild_deletebtn);
            convertview.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertview.getTag();
        }
        holder.childidet.setHintTextColor(Color.GRAY);
        holder.childnameet.setHintTextColor(Color.GRAY);
        holder.childdescribeet.setHintTextColor(Color.GRAY);
        holder.childpriceet.setHintTextColor(Color.GRAY);
        if (type==0) {
            holder.childpriceet.setText("单价："+list.get(groupPosition).get("price").toString());
            holder.childedelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Constant.childposition.get(groupPosition).remove(childPosition);
                    Message message = new Message();
                    message.what=0;
                    message.obj = groupPosition;
                    MethodTools.listHandler.sendMessage(message);
                }
            });
        }else {
            holder.childpriceet.setText("单价："+list.get(7).get("price").toString());
            holder.childedelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message message = new Message();
                    message.what=0;
                    Constant.childposition.get(7).remove(childPosition);
                    message.obj = 0;
                    MethodTools.listHandler.sendMessage(message);
                }
            });
        }
        holder.childpriceet.setTextColor(Color.BLACK);
        holder.childdescribeet.setTextColor(Color.BLACK);
        holder.childnameet.setTextColor(Color.BLACK);
        holder.childidet.setTextColor(Color.BLACK);
        return convertview;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public class GroupViewHolder {
        TextView grouptv;
        ImageView groupimg;
    }

    public class ChildViewHolder{
        EditText childidet;
        EditText childnameet;
        EditText childdescribeet;
        EditText childpriceet;
        Button childedelbtn;
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

    /**
     * @param position
     * @function 二级列表添加新的Hashmap
     * @return map:  price:价格  name:类型
     */
    public HashMap<String ,Object> newHashmap(int position){
        HashMap map = Constant.pricelist.get(position);
        return map;
    }
}
