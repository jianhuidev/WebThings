// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.projectmanage;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class ConfigurationPlanActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.projectmanage.ConfigurationPlanActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689724, "field 'mPlanlayout'");
    target.mPlanlayout = (android.widget.RelativeLayout) view;
    view = finder.findRequiredView(source, 2131689725, "field 'mConfplanLeftIv' and method 'onClick'");
    target.mConfplanLeftIv = (android.widget.ImageView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
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

  public static void reset(com.kys26.webthings.projectmanage.ConfigurationPlanActivity target) {
    target.mPlanlayout = null;
    target.mConfplanLeftIv = null;
    target.mConfplanNextStep = null;
  }
}
