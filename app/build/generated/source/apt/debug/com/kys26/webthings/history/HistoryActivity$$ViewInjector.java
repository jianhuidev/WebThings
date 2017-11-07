// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.history;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class HistoryActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.history.HistoryActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296262, "field 'mNH3'");
    target.mNH3 = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131297060, "field 'mTemp'");
    target.mTemp = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131296549, "field 'mHum'");
    target.mHum = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131297037, "field 'mSunpower'");
    target.mSunpower = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131297119, "field 'mTypeGroup'");
    target.mTypeGroup = (android.widget.RadioGroup) view;
    view = finder.findRequiredView(source, 2131296435, "field 'mDay'");
    target.mDay = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131296660, "field 'mMonth'");
    target.mMonth = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131297189, "field 'mYear'");
    target.mYear = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131296431, "field 'mDateGroup'");
    target.mDateGroup = (android.widget.RadioGroup) view;
  }

  public static void reset(com.kys26.webthings.history.HistoryActivity target) {
    target.mNH3 = null;
    target.mTemp = null;
    target.mHum = null;
    target.mSunpower = null;
    target.mTypeGroup = null;
    target.mDay = null;
    target.mMonth = null;
    target.mYear = null;
    target.mDateGroup = null;
  }
}
