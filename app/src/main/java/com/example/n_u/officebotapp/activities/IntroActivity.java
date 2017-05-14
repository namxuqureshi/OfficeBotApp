package com.example.n_u.officebotapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.n_u.officebotapp.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity
        extends AppIntro {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFlowAnimation();
        addSlide(AppIntroFragment.newInstance("NFC TAGS", "Still in work progress",
                R.drawable.zzz_nfc,
                getResources().getColor(R.color.colorAccent)));
        addSlide(AppIntroFragment.newInstance("Title", "Description",
                R.drawable.home,
                getResources().getColor(R.color.colorAccent)));
        setBarColor(getResources().getColor(R.color.colorAccent));
        setSeparatorColor(Color.parseColor(getString(R.string.color)));
        showSkipButton(true);
        setProgressButtonEnabled(true);
        setVibrate(true);
        setVibrateIntensity(50);
        setFadeAnimation();
        setFlowAnimation();
        setDepthAnimation();
    }

    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    public void onSlideChanged(@Nullable Fragment oldFragment
            , @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}



/* Location:           C:\Users\N_U\Desktop\Tolo\ob\classes30-dex2jar.jar

 * Qualified Name:     com.example.n_u.officebot.activities.IntroActivity

 * JD-Core Version:    0.7.0.1

 */