// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.projectmanage;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class CompleteProject$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.projectmanage.CompleteProject target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689719, "field 'mCompleteprojectLeftIv' and method 'onClick'");
    target.mCompleteprojectLeftIv = (android.widget.ImageView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131689722, "field 'mCompleteprojectList'");
    target.mCompleteprojectList = (android.widget.ListView) view;
    view = finder.findRequiredView(source, 2131689723, "field 'mConfplanNextStep' and method 'onClick'");
    target.mConfplanNextStep = (android.widget.TextView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
  }

  public static void reset(com.kys26.webthings.projectmanage.CompleteProject target) {
    target.mCompleteprojectLeftIv = null;
    target.mCompleteprojectList = null;
    target.mConfplanNextStep = null;
  }
}
