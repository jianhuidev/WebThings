// Generated code from Butter Knife. Do not modify!
package com.kys26.webthings.login;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class RegisterActivity$$ViewInjector {
  public static void inject(Finder finder, final com.kys26.webthings.login.RegisterActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296671, "field 'mNicknameEdit'");
    target.mNicknameEdit = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131297106, "field 'mTruenameEdit'");
    target.mTruenameEdit = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131296698, "field 'mPasswordEdit'");
    target.mPasswordEdit = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131296475, "field 'mEmailEdit'");
    target.mEmailEdit = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131296704, "field 'mPhoneEdit'");
    target.mPhoneEdit = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131296377, "field 'mCmdLeftBack'");
    target.mCmdLeftBack = (android.widget.ImageView) view;
    view = finder.findRequiredView(source, 2131296600, "field 'mLeftBtn' and method 'onClick'");
    target.mLeftBtn = (android.widget.FrameLayout) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131296957, "field 'mRelativeLayout'");
    target.mRelativeLayout = (android.widget.RelativeLayout) view;
    view = finder.findRequiredView(source, 2131296946, "field 'mRegistBtn' and method 'onClick'");
    target.mRegistBtn = (android.widget.Button) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131296468, "field 'mEditLayout'");
    target.mEditLayout = (android.widget.LinearLayout) view;
  }

  public static void reset(com.kys26.webthings.login.RegisterActivity target) {
    target.mNicknameEdit = null;
    target.mTruenameEdit = null;
    target.mPasswordEdit = null;
    target.mEmailEdit = null;
    target.mPhoneEdit = null;
    target.mCmdLeftBack = null;
    target.mLeftBtn = null;
    target.mRelativeLayout = null;
    target.mRegistBtn = null;
    target.mEditLayout = null;
  }
}
