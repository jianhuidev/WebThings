// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.projectmanage;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class AddGateWayActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.projectmanage.AddGateWayActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296303, "field 'mAddgatewayLeftIv' and method 'onClick'");
    target.mAddgatewayLeftIv = (android.widget.ImageView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131296305, "field 'mAddgatewayTittle'");
    target.mAddgatewayTittle = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131296301, "field 'mAddgatewayDatalist'");
    target.mAddgatewayDatalist = (android.widget.ListView) view;
    view = finder.findRequiredView(source, 2131296302, "field 'mAddgatewayGwspinner'");
    target.mAddgatewayGwspinner = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131296300, "field 'mAddgatewayAdditemiv' and method 'onClick'");
    target.mAddgatewayAdditemiv = (android.widget.ImageView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131296297, "field 'mAddGateWayNextStep' and method 'onClick'");
    target.mAddGateWayNextStep = (android.widget.TextView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131296296, "field 'mAddGateWayFarmName'");
    target.mAddGateWayFarmName = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131296538, "field 'mGwlistTv'");
    target.mGwlistTv = (android.widget.TextView) view;
  }

  public static void reset(com.kys26.webthings.projectmanage.AddGateWayActivity target) {
    target.mAddgatewayLeftIv = null;
    target.mAddgatewayTittle = null;
    target.mAddgatewayDatalist = null;
    target.mAddgatewayGwspinner = null;
    target.mAddgatewayAdditemiv = null;
    target.mAddGateWayNextStep = null;
    target.mAddGateWayFarmName = null;
    target.mGwlistTv = null;
  }
}
