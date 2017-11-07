// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class GWFragment$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.fragment.GWFragment target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296710, "field 'mPm' and method 'onClick'");
    target.mPm = (android.widget.TextView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131296549, "field 'mHum'");
    target.mHum = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131297060, "field 'mTemp'");
    target.mTemp = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131297037, "field 'mSunpower'");
    target.mSunpower = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131296430, "field 'mDate'");
    target.mDate = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131297165, "field 'mWeek'");
    target.mWeek = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131296551, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131297062, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131297038, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
  }

  public static void reset(com.kys26.webthings.fragment.GWFragment target) {
    target.mPm = null;
    target.mHum = null;
    target.mTemp = null;
    target.mSunpower = null;
    target.mDate = null;
    target.mWeek = null;
  }
}
