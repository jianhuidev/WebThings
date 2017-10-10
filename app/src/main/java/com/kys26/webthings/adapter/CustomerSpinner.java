package com.kys26.webthings.adapter;

import java.util.ArrayList;

import com.zhangyx.MyGestureLock.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomerSpinner extends Spinner {

	/**输出标识*/
	private String TAG="webthings.adapter.CustomerSpinner";
	/**设置传输对象*/
	public Context context;
	private Dialog dialog = null;
	private ArrayList<String> list;//ArrayList<String> list储所要显示的数据
	private String Msg;
    private String  listPosition;
	
	public CustomerSpinner(Context context) {
		super(context);
	}

	public CustomerSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = getContext();
		  if (isInEditMode()) {
	            return;
	        }
	        //为MyDateSpinner设置adapter，主要用于显示spinner的text
		  CustomerSpinner.this.setAdapter(new BaseAdapter() {
	            @Override
	            public int getCount() {
	                return 1;
	            }
	 
	            @Override
	            public Object getItem(int arg0) {
	                return null;
	            }
	 
	            @Override
	            public long getItemId(int arg0) {
	                return 0;
	            }
	 
	            @Override
	            public View getView(int arg0, View arg1, ViewGroup arg2) {
	                TextView text = new TextView(CustomerSpinner.this.getContext());
	                text.setText(list.get(arg0));
                    listPosition=list.get(arg0);
	                text.setTextColor(Color.BLACK);
	                text.setTextSize(15);
                    text.setGravity(Gravity.CENTER);
	                return text;
	            }
	        });
		
	}

	@Override
	public boolean performClick() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.customspinner, null);
		ListView listview = (ListView) view.findViewById(R.id.formcustomspinner_list);
		ListviewAdapter adapters = new ListviewAdapter(context, getList());
		listview.setAdapter(adapters);
		listview.setOnItemClickListener(onItemClick);
		dialog = new Dialog(context, R.style.spinner_dialog);//创建Dialog并设置样式主题
		LayoutParams params = new LayoutParams(600, LayoutParams.WRAP_CONTENT);
		dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		dialog.show();
		dialog.addContentView(view, params);
		return true;
	}

	OnItemClickListener onItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			setSelection(position);
			setMsg(list.get(position));
			if (dialog != null) 
			{
				Message msg=new Message();
				//对时间的选择要弹出第二个选择器，这里做判断处理
               /*  if(list.get(position).equals("具体日期"))
				 {
					 //发送条件为选择具体日期
					 msg.what= Code.TIMEREADY;
					 MethodTools.HnadlerVOlleyJson.sendMessage(msg);
				 }else{
					 //发送条件为非具体日期
					 msg.what= Code.TIMEOUT;
					 MethodTools.HnadlerVOlleyJson.sendMessage(msg);
				 }*/
				dialog.dismiss();
				dialog = null;
			}
		}
	};
	
	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	public String getMsg() {

		return listPosition;
	}

	public void setMsg(String Msg) {
		this.Msg = Msg;
	}
	
	class ListviewAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<String> Times;
		public ListviewAdapter(Context context,ArrayList<String> Times){
			this.context = context;
			this.Times = Times;
		}
		@Override
		public int getCount() {
			return Times.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			if(arg1 == null&&list.size() != 0){
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(context);
				arg1 = inflater.inflate(R.layout.spinner_item, null);
				viewHolder.SysTimes = (TextView)arg1.findViewById(R.id.spinner_itemText);
				arg1.setTag(viewHolder);
			}else
				viewHolder = (ViewHolder) arg1.getTag();
				viewHolder.SysTimes.setText(Times.get(arg0));
			return arg1;
		}
		
		
		class ViewHolder {
			public TextView SysTimes;
		}

	}
}
