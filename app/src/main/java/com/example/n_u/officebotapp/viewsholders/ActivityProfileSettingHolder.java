package com.example.n_u.officebotapp.viewsholders;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import com.example.n_u.officebotapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityProfileSettingHolder {
    @Bind(R.id.profile_img)
    CircleImageView profileImg;
    @Bind(R.id.pic_set_float_btn)
    FloatingActionButton picSetFloatBtn;
    @Bind(R.id.nm_label)
    TextInputLayout nmLabel;
    @Bind(R.id.profile_name)
    EditText profileName;
    @Bind(R.id.ph_label)
    TextInputLayout phLabel;
    @Bind(R.id.profile_phone)
    EditText profilePhone;
    @Bind(R.id.cp_label)
    TextInputLayout cpLabel;
    @Bind(R.id.profile_new_password)
    EditText profileNewPassword;
    @Bind(R.id.profile_new_email)
    EditText profileNewEmail;
    @Bind(R.id.em_label)
    TextInputLayout emLabel;

    public ActivityProfileSettingHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public TextInputLayout getPhLabel() {
        return phLabel;
    }

    public EditText getProfileNewPassword() {
        return profileNewPassword;
    }

    public CircleImageView getProfileImg() {
        return profileImg;
    }

    public EditText getProfilePhone() {
        return profilePhone;
    }

    public FloatingActionButton getPicSetFloatBtn() {
        return picSetFloatBtn;
    }

    public TextInputLayout getCpLabel() {
        return cpLabel;
    }

    public TextInputLayout getNmLabel() {
        return nmLabel;
    }

    public EditText getProfileName() {
        return profileName;
    }

    public EditText getProfileNewEmail() {
        return profileNewEmail;
    }

    public TextInputLayout getEmLabel() {
        return emLabel;
    }
}

