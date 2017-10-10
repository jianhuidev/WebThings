// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.projectmanage;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class CreateProjectActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.projectmanage.CreateProjectActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689727, "field 'mCreateProjectLeftIv' and method 'onClick'");
    target.mCreateProjectLeftIv = (android.widget.ImageView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131689728, "field 'mCreateProjectTittle'");
    target.mCreateProjectTittle = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131689665, "field 'mTitleRl'");
    target.mTitleRl = (android.widget.RelativeLayout) view;
    view = finder.findRequiredView(source, 2131689730, "field 'mCreateProjectStep0'");
    target.mCreateProjectStep0 = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131689731, "field 'mCreateProjectApplicationType'");
    target.mCreateProjectApplicationType = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131689732, "field 'mCreateProjectVarieTytype'");
    target.mCreateProjectVarieTytype = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131689733, "field 'mCreateProjectManager'");
    target.mCreateProjectManager = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131689734, "field 'mCreateProjectManagerPhone'");
    target.mCreateProjectManagerPhone = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131689735, "field 'mCreateProjectClient'");
    target.mCreateProjectClient = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131689736, "field 'mCreateProjectList'");
    target.mCreateProjectList = (com.kys26.webthings.view.CustomProjectList) view;
    view = finder.findRequiredView(source, 2131689737, "field 'mCreateProjectClientPhone'");
    target.mCreateProjectClientPhone = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131689738, "field 'mCreateProjectClientMailBox'");
    target.mCreateProjectClientMailBox = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131689739, "field 'mCreateProjectClientAccount'");
    target.mCreateProjectClientAccount = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131689742, "field 'mCreateProjectSelectFile'");
    target.mCreateProjectSelectFile = (android.widget.Button) view;
    view = finder.findRequiredView(source, 2131689743, "field 'mCreateProjectFileTxt'");
    target.mCreateProjectFileTxt = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131689744, "field 'mCreateProjectNextStep' and method 'onClick'");
    target.mCreateProjectNextStep = (android.widget.LinearLayout) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131689729, "field 'mCreateProjectScrollview'");
    target.mCreateProjectScrollview = (android.widget.ScrollView) view;
    view = finder.findRequiredView(source, 2131689740, "field 'mCreateProjectFarmName'");
    target.mCreateProjectFarmName = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131689741, "field 'mCreateProjectFarmAddress'");
    target.mCreateProjectFarmAddress = (android.widget.EditText) view;
  }

  public static void reset(com.kys26.webthings.projectmanage.CreateProjectActivity target) {
    target.mCreateProjectLeftIv = null;
    target.mCreateProjectTittle = null;
    target.mTitleRl = null;
    target.mCreateProjectStep0 = null;
    target.mCreateProjectApplicationType = null;
    target.mCreateProjectVarieTytype = null;
    target.mCreateProjectManager = null;
    target.mCreateProjectManagerPhone = null;
    target.mCreateProjectClient = null;
    target.mCreateProjectList = null;
    target.mCreateProjectClientPhone = null;
    target.mCreateProjectClientMailBox = null;
    target.mCreateProjectClientAccount = null;
    target.mCreateProjectSelectFile = null;
    target.mCreateProjectFileTxt = null;
    target.mCreateProjectNextStep = null;
    target.mCreateProjectScrollview = null;
    target.mCreateProjectFarmName = null;
    target.mCreateProjectFarmAddress = null;
  }
}
