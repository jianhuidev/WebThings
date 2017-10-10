package com.kys26.webthings.util;

import com.kys26.webthings.interfac.NodeType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	/**
	 * 判断字符串是否为空
	 * @param str 要判断的字符串
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str==null||str.trim().length()==0){
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 判断字符串是否相等
	 * @param str0  要判断的字符串1
	 * @param str1  要判断的字符串2
	 * @return boolen
	 */
	public static boolean isEquality(String str0,String str1){
		if(str0.equals(str1)){
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * 处理url
	 * 如果不是以http://或者https://开头，就添加http://
	 * @param url 被处理的url
	 * @return
	 */
	public static String preUrl(String url){
		if(url==null){
			return null;
		}
		if(url.startsWith("http://")||url.startsWith("https://")){
			return url;
		}
		else{
			return "http://"+url;
		}
	}
	/**
	 * 判断手机号合格是是否正确
	 * @param mobiles 被判断的手机号
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^13\\d{9}|14[57]\\d{8}|15[012356789]\\d{8}|18[01256789]\\d{8}|17[0678]\\d{8}");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	/**
	 * 判断邮箱格式是否正确;
	 * @param email 被判断的邮箱
	 * @return
     */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}
	/**
	 * @function 通过得到的状态参数返回相应状态
	 * @param stat 状态参数
	 * @return 状态
	 */
	public static String getState(int stat){
		switch (stat){
			case 85:
				return "工作正常";
			case 34:
				return "已关闭";
			case 74:
				return "已禁用";
			case 254:
				return "未激活";
			case 238:
				return "故障或未在线";
			default:
				return null;
		}
	}
	/**
	 * 分割时间
	 * @param time
	 * @return
	 */
	public static int[] getTime(String time){
		String timeSE[] = time.split("-");
		String timeS[] = timeSE[0].split(":");
		String timeE[] = timeSE[1].split(":");
		int timeNeed[] = new int[]{Integer.decode(timeS[0]),Integer.decode(timeS[1]),
				Integer.decode(timeE[0]),Integer.decode(timeE[1])};
			return timeNeed;

	}
	/**
	 * 设置节点类型
	 * @param nodeType
	 * @return
	 */
	public static String setNodeType(NodeType nodeType) {
		switch (nodeType){
			case WIND:			//通风
				return "1";
			case COOLING:		//降温
				return "2";
			case FEED:			//喂食
				return "6";
			case DUNG:			//除粪
				return "7";
			case LIGHT:			//照明
				return "4";
			case HEATING:		//加温
				return "3";
			case HUMIDIFICATION://加湿
				return "9";
			case FILL_LIGHT:	//补光
				return "5";
			case STERILIZATION:	//杀菌
				return "8";
		}
		return null;
	}
}

