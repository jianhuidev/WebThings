// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.query;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class WarningHumActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.query.WarningHumActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131297090, "field 'mTitle'");
    target.mTitle = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131297040, "field 'mSuplow'");
    target.mSuplow = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131296634, "field 'mLow'");
    target.mLow = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131297039, "field 'mSuphigh'");
    target.mSuphigh = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131296542, "field 'mHigh'");
    target.mHigh = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131296291, "field 'mActivePhone'");
    target.mActivePhone = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131296290, "field 'mActiveLow'");
    target.mActiveLow = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131296289, "field 'mActiveHigh'");
    target.mActiveHigh = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131296441, "field 'mDegree'");
    target.mDegree = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131297079, "field 'mTime'");
    target.mTime = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131296326, "field 'mAutoTrue'");
    target.mAutoTrue = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131296323, "field 'mAutoFalse'");
    target.mAutoFalse = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131296292, "field 'mActiveRg'");
    target.mActiveRg = (android.widget.RadioGroup) view;
    view = finder.findRequiredView(source, 2131296601, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131297041, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
  }

  public static void reset(com.kys26.webthings.query.WarningHumActivity target) {
    target.mTitle = null;
    target.mSuplow = null;
    target.mLow = null;
    target.mSuphigh = null;
    target.mHigh = null;
    target.mActivePhone = null;
    target.mActiveLow = null;
    target.mActiveHigh = null;
    target.mDegree = null;
    target.mTime = null;
    target.mAutoTrue = null;
    target.mAutoFalse = null;
    target.mActiveRg = null;
  }
}
