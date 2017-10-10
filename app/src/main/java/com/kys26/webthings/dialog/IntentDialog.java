package com.kys26.webthings.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhangyx.MyGestureLock.R;

@SuppressLint("ShowToast")
public class IntentDialog {

	/**
	 * @category退出对话框
     * @param ctx
	 */
	public static void showExitDialog(final Activity ctx) {

		new AlertDialog.Builder(ctx)
				.setIcon(R.drawable.exit)
				.setTitle(R.string.exit_title)
				.setMessage(R.string.exit_account)
				.setPositiveButton(R.string.exit_sure,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								//结束当前activiy
								System.exit(0);// 退出整个程序
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						})
				.setNegativeButton(R.string.exit_canle,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}
	/**
	 * 得到自定义的progressDialog
	 * @param context
	 * @param msg
	 * @return
	 */
	/**声明静态的Dialog，方便显示和取消*/
	public static  Dialog dialog;

	public static Dialog createDialog(Context context, String msg) {

			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.coustom_dialog, null);// 得到加载view
			LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
			// main.xml中的ImageView
			ProgressBar spaceshipImage = (ProgressBar) v.findViewById(R.id.img);
			TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
			// 加载动画
			Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
					context, R.anim.custom_dialog_anim);
			// 使用ImageView显示动画
			spaceshipImage.startAnimation(hyperspaceJumpAnimation);
			tipTextView.setText(msg);// 设置加载信息

		    dialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

			dialog.setCancelable(true);// 设置可以用“返回键”取消
			dialog.setContentView(layout, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));// 设置布局
		return dialog;
	}

}
