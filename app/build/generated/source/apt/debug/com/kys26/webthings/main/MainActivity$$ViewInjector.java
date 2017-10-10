// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.main;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.main.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689785, "field 'fragment'");
    target.fragment = (android.widget.FrameLayout) view;
    view = finder.findRequiredView(source, 2131689786, "field 'mBtnBottomLeft' and method 'onClick'");
    target.mBtnBottomLeft = (android.widget.RelativeLayout) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131689788, "field 'mBtnBottomMiddle' and method 'onClick'");
    target.mBtnBottomMiddle = (android.widget.RelativeLayout) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131689790, "field 'mBtnBottomRight' and method 'onClick'");
    target.mBtnBottomRight = (android.widget.RelativeLayout) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131689784, "field 'mBackground'");
    target.mBackground = (android.widget.LinearLayout) view;
  }

  public static void reset(com.kys26.webthings.main.MainActivity target) {
    target.fragment = null;
    target.mBtnBottomLeft = null;
    target.mBtnBottomMiddle = null;
    target.mBtnBottomRight = null;
    target.mBackground = null;
  }
}
