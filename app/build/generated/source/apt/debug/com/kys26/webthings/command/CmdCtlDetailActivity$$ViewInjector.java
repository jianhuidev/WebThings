// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.command;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class CmdCtlDetailActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.command.CmdCtlDetailActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296740, "field 'mQuerydetailTitle'");
    target.mQuerydetailTitle = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131296738, "field 'mQuerydetailChoicegw'");
    target.mQuerydetailChoicegw = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131296654, "field 'mMessageList'");
    target.mMessageList = (android.widget.ListView) view;
    view = finder.findRequiredView(source, 2131296737, "field 'mQuerydetailBg'");
    target.mQuerydetailBg = (android.widget.LinearLayout) view;
    view = finder.findRequiredView(source, 2131296601, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131296690, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131296373, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
  }

  public static void reset(com.kys26.webthings.command.CmdCtlDetailActivity target) {
    target.mQuerydetailTitle = null;
    target.mQuerydetailChoicegw = null;
    target.mMessageList = null;
    target.mQuerydetailBg = null;
  }
}
