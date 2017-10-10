package com.kys26.webthings.adapter;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.dialog.CustomDialog;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.main.MainActivity;
import com.kys26.webthings.method.MethodTools;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kys-36 on 2017/5/6.
 *
 * @param
 * @author
 * @function
 */

public class FarmAdapter extends RecyclerView.Adapter<FarmAdapter.ViewHolder> {
    private MainActivity mContext;
    CustomDialog dialog;
    EditText edit;

    public FarmAdapter(MainActivity context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mContext.getLayoutInflater().inflate(R.layout.item_farm, null);
        return new ViewHolder(view);
    }

    //显示dialog

    private void ShowDialog(final int position) {
        dialog = new CustomDialog();
        dialog.setTitle("更改名字");
        dialog.setTitleSize(18);
        edit = new EditText(mContext);
        edit.setTextSize(16);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();//获取屏幕宽度
        int editHeight = (dm.widthPixels) / 6;
        edit.setHeight(editHeight);
        Drawable d = ContextCompat.getDrawable(mContext, R.drawable.null_select);
        edit.setBackgroundDrawable(d);
        edit.setText(MethodTools.farmDataList.get(position).getFarm_name());
        dialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
            @Override
            public void sureListener() {
                JSONObject object = new JSONObject();
                try {
                    object.put("farm_id", MethodTools.farmDataList.get(position).getFarm_id());
                    object.put("farm_name", edit.getText());
                    object.put("farm_describe", MethodTools.farmDataList.get(position).getDescribe());
                    object.put("farm_address", MethodTools.farmDataList.get(position).getFarm_address());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mContext.doRegist(Path.host + Path.URL_UPDATE_FARM, object, MainActivity.UPDATE_FARM);
                //这里写请求
            }

            @Override
            public void cancelListener() {
                dialog.DissMissDialog();
            }
        });
        dialog.setView(edit);
        dialog.show(mContext.getFragmentManager(), "TAG");
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        if (mContext.mGWStateList.get(position).getFarmname() != null)
        holder.mTextView.setText(MethodTools.mGWStateList.get(position).getFarmname());
        //holder.mTextView.setText(MethodTools.farmDataList.get(position).getFarm_name());
        if (MethodTools.mGWStateList.get(position).getKid_stat() != 85) {
            holder.mImageView.setSelected(true);
        } else {
            holder.mImageView.setSelected(false);
        }
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mImageView.isSelected()) {
                    Toast.makeText(mContext, "当前养殖场不可用", Toast.LENGTH_SHORT).show();
                } else {
//                    JSONObject jb = new JSONObject();
//                    try {
//                        jb.put("farmid", MethodTools.farmDataList.get(position).getFarm_id());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    mContext.position = position;
                    Log.e("position", position + "");
                    mContext.ChangeToMiddle(position);
                    mContext.setEnableRefresh(true, mContext.mGwfragment.refreshListener);
                    mContext.RequestWebData(position);//请求监控数据
                    mContext.RequestNodeState(position);//请求节点数据
                }
            }
        });
        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onLongClick(View v) {
                ShowDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return MethodTools.mGWStateList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CardView mCardView;
        TextView mTextView;
        ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.item_farm_card);
            mTextView = (TextView) itemView.findViewById(R.id.farm_name);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView4);
        }
    }

}
