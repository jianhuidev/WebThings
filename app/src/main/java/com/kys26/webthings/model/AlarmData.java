package com.kys26.webthings.model;

import android.app.AlarmManager;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/6/7.
 */

public class AlarmData {
    private AlarmManager mAlarmManager;
    private boolean isRegister;

    public void AlarmData() {


    }

    public void setAlarmManager(AlarmManager alarmManager) {
        mAlarmManager = alarmManager;
    }

    public AlarmManager getAlarmManager() {
        return mAlarmManager;
    }

}
