// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.history;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class HistoryActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.history.HistoryActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689746, "field 'mNH3'");
    target.mNH3 = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131689747, "field 'mTemp'");
    target.mTemp = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131689748, "field 'mHum'");
    target.mHum = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131689749, "field 'mSunpower'");
    target.mSunpower = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131689745, "field 'mTypeGroup'");
    target.mTypeGroup = (android.widget.RadioGroup) view;
    view = finder.findRequiredView(source, 2131689751, "field 'mDay'");
    target.mDay = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131689752, "field 'mMonth'");
    target.mMonth = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131689753, "field 'mYear'");
    target.mYear = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131689750, "field 'mDateGroup'");
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
