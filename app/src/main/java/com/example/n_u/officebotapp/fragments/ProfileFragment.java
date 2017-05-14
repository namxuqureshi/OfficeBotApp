package com.example.n_u.officebotapp.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.n_u.officebotapp.R;
import com.example.n_u.officebotapp.utils.OfficeBotURI;
import com.example.n_u.officebotapp.viewsholders.DialogueProfileHolder;

/**
 * Created by n_u on 5/6/17.
 */

public final class ProfileFragment extends DialogFragment implements View.OnClickListener {
    DialogueProfileHolder holder;
    Fragment ac = this;

    @Override
    public void onClick(View v) {
        getDialog().dismiss();
    }

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater
            , @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogue_profile, container);
        holder = new DialogueProfileHolder(v);
        holder.getDgFloatOk().setOnClickListener(this);
        return v;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Profile");
        holder.getDgProfileEmail().setText(getArguments().getString("email"));
        if (holder.getDgProfileEmail().getText().length() > 8) {
            holder.getDgProfileEmail().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        if (holder.getDgProfileEmail().getText().length() > 16) {
            holder.getDgProfileEmail().setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        }
        holder.getDgProfilePhone().setText(getArguments().getString("phone"));
        holder.getDgProfileName().setText(getArguments().getString("name"));
        Glide.with(ac)
                .load(OfficeBotURI.getFileUrlPreFix() + getArguments().getString("image"))
                .override(270, 250)
                .placeholder(R.drawable.profile_pic_default)
                .fitCenter().centerCrop()
                .into(holder.getDgProfileImg());
//        getDialog().getWindow().setAllowEnterTransitionOverlap(true);
//        getDialog().getWindow().setDimAmount(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        getDialog().getWindow().setAllowReturnTransitionOverlap(true);
        getDialog().getWindow().setEnterTransition(new Slide());
        getDialog().getWindow().setExitTransition(new Fade());

//        SoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    public static ProfileFragment newInstance(String name, String email, String phone, String image) {
        ProfileFragment frag = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("email", email);
        args.putString("phone", phone);
        args.putString("image", image);
        frag.setArguments(args);
        return frag;
    }

    public static ProfileFragment newInstance(String name, String email, String image) {
        ProfileFragment frag = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("email", email);
        args.putString("image", image);
        frag.setArguments(args);
        return frag;
    }

    public static ProfileFragment newInstance(String title) {
        ProfileFragment frag = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }
}
