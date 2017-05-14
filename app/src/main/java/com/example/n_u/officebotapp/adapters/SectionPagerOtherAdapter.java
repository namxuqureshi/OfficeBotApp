package com.example.n_u.officebotapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.n_u.officebotapp.fragments.FragmentFriend;
import com.example.n_u.officebotapp.fragments.FragmentOther;

public class SectionPagerOtherAdapter
        extends FragmentPagerAdapter {
    public SectionPagerOtherAdapter(FragmentManager manager) {
        super(manager);
    }

    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(final int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//                return PlaceholderFragment2.newInstance(position + 1);

        switch (position) {
            case 0:
                //page 1
                return FragmentOther.newInstance(position);
            case 1:
                //page 2
                return FragmentFriend.newInstance(position);
            default:
                //this page does not exists
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        switch (position) {
            case 0:
                return "Tags";
            case 1:
                return "Yours";

        }
        return null;
    }
}



/* Location:           C:\Users\N_U\Desktop\Tolo\ob\classes30-dex2jar.jar

 * Qualified Name:     com.example.n_u.officebot.adapters.SectionPagerOtherAdapter

 * JD-Core Version:    0.7.0.1

 */