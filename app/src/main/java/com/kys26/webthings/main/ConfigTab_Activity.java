package com.kys26.webthings.main;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.permission.PermissionActivity;
import com.zhangyx.MyGestureLock.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kys_26:徐建强 on 2015/7/8.
 * function: for Ativity change by Tab
 */
public class ConfigTab_Activity extends TabActivity {

    private LinearLayout mainActLayout,platformLayout,settingsLayout,PersonAct_Btn;
    private Button quareBtn,historyBtn,settingsBtn;
    private Button Reminde_MyClass,Reminde_File,Reminde_Broad;
    //private Button Reminde_Preson;
    private int bmpW;
    public static ViewPager mPager;
    private List<View> ListViews;
    private int offset = 0;
    public static final int MAIN= Menu.FIRST;
    public static final int DICTIONARY= Menu.FIRST;
    public static final int ABOUT= Menu.FIRST;
    private int currIndex = 0;
    public static LocalActivityManager manager = null;
    private final static String TAG = "ConfigTabActivity";
    public final Context context = ConfigTab_Activity.this;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);

        manager = new LocalActivityManager(this, true);

        manager.dispatchCreate(savedInstanceState);

        InitTextView();

        InitViewPager();
    }

    /**
     * Created by kys_26:徐建强 on 2015/7/8.
     * function:FindView and Add Listener
     */
    private void InitTextView() {

        //提示条
      Reminde_MyClass=(Button)findViewById(R.id.tab_layout_bottom_myclassbtn_reminder);
      Reminde_MyClass.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_press);

      Reminde_File=(Button)findViewById(R.id.tab_layout_bottom_filesbtn_reminder);
      Reminde_File.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);

      Reminde_Broad=(Button)findViewById(R.id.tab_layout_bottom_broadcastbtn_reminder);
      Reminde_Broad.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);

//      Reminde_Preson=(Button)findViewById(R.id.tab_layout_bottom_personbtn_reminder);
//      Reminde_Preson.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);

        //布局跳转和控件跳转
        mainActLayout = (LinearLayout)findViewById(R.id.main_btn);
        platformLayout = (LinearLayout) findViewById(R.id.classbro_btn);
        settingsLayout = (LinearLayout) findViewById(R.id.files_btn);

        quareBtn = (Button)findViewById(R.id.quare);
        historyBtn = (Button) findViewById(R.id.history);
        settingsBtn = (Button) findViewById(R.id.settings);
       //PersonAct_Btn=(Button)findViewById(R.id.person_btn);

        //布局跳转和控件跳转监听
        mainActLayout.setOnClickListener(new MyOnClickListener(0));
        platformLayout.setOnClickListener(new MyOnClickListener(1));
        settingsLayout.setOnClickListener(new MyOnClickListener(2));

        quareBtn.setOnClickListener(new MyOnClickListener(0));
        historyBtn.setOnClickListener(new MyOnClickListener(1));
        settingsBtn.setOnClickListener(new MyOnClickListener(2));
       //PersonAct_Btn.setOnClickListener(new MyOnClickListener(3));
    }
    /**
     * Created by kys_26:徐建强 on 2015/4/8.
     * function:Add ViewPager and realize ArrayView for goto some Activity
     */
    private void InitViewPager() {

        mPager = (ViewPager) findViewById(R.id.vPager);

                ListViews = new ArrayList<View>();

        MyPagerAdapter mpAdapter = new MyPagerAdapter(ListViews);

//        Intent mainActivity = new Intent(context,Quare.class);
//        ListViews.add(getView("", mainActivity));

        Intent historyActivity = new Intent(context, PermissionActivity.class);
        ListViews.add(getView("", historyActivity));

//        Intent settingsActivity = new Intent(context,SettingsActivity.class);
//        ListViews.add(getView("", settingsActivity));

       /* Intent intent_Person_Activity=new Intent(context, Person_Activity.class);
        ListViews.add(getView("",intent_Person_Activity));*/

        mPager.setAdapter(mpAdapter);

        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    /**
     * Created by kys_26:徐建强 on 2015/7/8.
     * function:getActivityView and use Intent goto
     * @param id,intent
     * @return id,intent
     */
    private View getView(String id, Intent intent)

    {
        return manager.startActivity(id, intent).getDecorView();

    }
    /**
     * Created by kys_26:徐建强 on 2015/4/8.
     * function:initialize animation and get data of slide
     */
    private void InitImageView() {


        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.tab_bottom_reminder_bg_normal).getWidth();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenW = dm.widthPixels;

        offset = (screenW / 3 - bmpW) / 2;
        System.out.println("offset==" + offset);
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);

    }

    /**
     * Created by kys_26:徐建强 on 2015/7/8.
     * function: Make ClickListener id data for three button of bottom and set bg to reminder button
     * @return null
     */
    public class MyOnClickListener implements View.OnClickListener {

        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }
        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
            switch (index)
            {
                case 0:
                    Reminde_MyClass.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_press);
                    Reminde_Broad.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_File.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    //Reminde_Preson.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    break;
                case 1:
                    Reminde_MyClass.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_Broad.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_File.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_press);
                    //Reminde_Preson.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    break;
                case 2:
                    Reminde_MyClass.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_Broad.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_press);
                    Reminde_File.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                   // Reminde_Preson.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    break;
               /* case 3:
                    Reminde_MyClass.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_Broad.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_File.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                   // Reminde_Preson.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_press);
                    break;*/
                default:
            }
        }

    };
    /**
     * Created by kys_26:徐建强 on 2015/7/8.
     * function:judge click for slide to what tab,tab listener
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset * 2 + bmpW;

        int two = one * 2;

        int three=two*2;
        @Override
        public void onPageSelected(int arg0) {

            Animation animation = null;

            // Intent intent = new Intent();

            switch (arg0) {

                case 0:
                    Reminde_MyClass.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_press);
                    Reminde_Broad.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_File.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    //Reminde_Preson.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    if (currIndex == 1) {

                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }/*else if(currIndex==3){
                        animation = new TranslateAnimation(three, 0, 0, 0);
                    }*/
                    break;

                case 1:
                    Reminde_MyClass.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_Broad.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_File.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_press);
                    //Reminde_Preson.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    if (currIndex == 0) {

                        animation = new TranslateAnimation(offset, one, 0, 0);

                    } else if (currIndex == 2) {

                        animation = new TranslateAnimation(two, one, 0, 0);

                    }/*else if(currIndex==3){
                        animation = new TranslateAnimation(three, two, 0, 0);
                    }*/

                    break;

                case 2:
                    Reminde_MyClass.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_Broad.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_press);
                    Reminde_File.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    //Reminde_Preson.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);

                    if (currIndex == 0) {

                        animation = new TranslateAnimation(offset, two, 0, 0);

                    } else if (currIndex == 1) {

                        animation = new TranslateAnimation(one, two, 0, 0);

                    }/*else if(currIndex==3){
                        animation = new TranslateAnimation(three,one, 0, 0);
                    }*/

                    break;
               /* case 3:
                    Reminde_MyClass.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_Broad.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    Reminde_File.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_normal);
                    //Reminde_Preson.setBackgroundResource(R.drawable.tab_bottom_reminder_bg_press);
                    if (currIndex == 0) {

                        animation = new TranslateAnimation(offset, two, 0, 0);

                    } else if (currIndex == 1) {

                        animation = new TranslateAnimation(two, one, 0, 0);

                    }else if(currIndex==2){
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    }

                    break;*/
            }
            currIndex = arg0;

            animation.setFillAfter(true);
            animation.setDuration(500);
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    /**
     * Created by kys_26:徐建强 on 2015/7/8.
     * function:Create Adapter for Pager
     */
    public class MyPagerAdapter extends PagerAdapter {

        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {

            this.mListViews = mListViews;
        }
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }
        @Override
        public void finishUpdate(View arg0) {
        }
        @Override
        public int getCount() {

            return mListViews.size();
        }
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {

            return null;

        }

        @Override
        public void startUpdate(View arg0) {

        }
    }
    //exit remide
    public  boolean onKeyDown(int KeyCode,KeyEvent event)
    {
        switch (KeyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                IntentDialog.showExitDialog(this);
                return true;
            default:
                break;
        }
        return super.onKeyDown(KeyCode,event);
    }
}
