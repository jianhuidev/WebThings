// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.projectmanage;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class AddNodeActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.projectmanage.AddNodeActivity target, Object source) {
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
    view = finder.findRequiredView(source, 2131296298, "field 'mAddNodeGwSpiner'");
    target.mAddNodeGwSpiner = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131296304, "field 'mAddgatewayStep'");
    target.mAddgatewayStep = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131296308, "field 'mAddnodeGwid'");
    target.mAddnodeGwid = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131296309, "field 'mAddnodeGwname'");
    target.mAddnodeGwname = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131296306, "field 'mAddnodeExlist'");
    target.mAddnodeExlist = (android.widget.ExpandableListView) view;
    view = finder.findRequiredView(source, 2131296299, "field 'mAddNodeNextStep' and method 'onClick'");
    target.mAddNodeNextStep = (android.widget.TextView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
  }

  public static void reset(com.kys26.webthings.projectmanage.AddNodeActivity target) {
    target.mAddgatewayLeftIv = null;
    target.mAddNodeGwSpiner = null;
    target.mAddgatewayStep = null;
    target.mAddnodeGwid = null;
    target.mAddnodeGwname = null;
    target.mAddnodeExlist = null;
    target.mAddNodeNextStep = null;
  }
}
