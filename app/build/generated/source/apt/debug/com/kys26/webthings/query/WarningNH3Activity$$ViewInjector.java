// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.query;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class WarningNH3Activity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.query.WarningNH3Activity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689623, "field 'mTitle'");
    target.mTitle = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131689864, "field 'mSuphigh'");
    target.mSuphigh = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131689865, "field 'mHigh'");
    target.mHigh = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131689866, "field 'mActivePhone'");
    target.mActivePhone = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131689867, "field 'mActiveLow'");
    target.mActiveLow = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131689868, "field 'mActiveHigh'");
    target.mActiveHigh = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131689869, "field 'mDegree'");
    target.mDegree = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131689760, "field 'mTime'");
    target.mTime = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131689871, "field 'mAutoTrue'");
    target.mAutoTrue = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131689872, "field 'mAutoFalse'");
    target.mAutoFalse = (android.widget.RadioButton) view;
    view = finder.findRequiredView(source, 2131689870, "field 'mActiveRg'");
    target.mActiveRg = (android.widget.RadioGroup) view;
    view = finder.findRequiredView(source, 2131689709, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131689873, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
  }

  public static void reset(com.kys26.webthings.query.WarningNH3Activity target) {
    target.mTitle = null;
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
