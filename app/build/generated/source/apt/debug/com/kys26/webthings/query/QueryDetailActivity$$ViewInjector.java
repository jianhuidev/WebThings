// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.query;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class QueryDetailActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.query.QueryDetailActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689803, "field 'mNodeDetailList'");
    target.mNodeDetailList = (android.support.v7.widget.RecyclerView) view;
  }

  public static void reset(com.kys26.webthings.query.QueryDetailActivity target) {
    target.mNodeDetailList = null;
  }
}
