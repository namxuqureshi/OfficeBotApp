package com.example.n_u.officebotapp.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.example.n_u.officebotapp.R;
import com.example.n_u.officebotapp.models.Friend;
import com.example.n_u.officebotapp.models.Group;
import com.example.n_u.officebotapp.models.Message;
import com.example.n_u.officebotapp.models.Reply;
import com.example.n_u.officebotapp.models.SearchUser;
import com.example.n_u.officebotapp.models.Tag;
import com.example.n_u.officebotapp.utils.AppLog;
import com.example.n_u.officebotapp.utils.OBSession;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.appinvite.AppInviteInvitation;

import java.util.Objects;

public class NavigationActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_INVITE = 1020;
    protected final Context context = this;
    protected DrawerLayout drawer = null;
    protected NavigationView navigationView = null;
    protected ActionBarDrawerToggle toggle = null;
    protected Toolbar toolbar = null;
    NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_navigation);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nav("Navigation");
    }

    protected void nav(String name) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        toolbar.setTitleMarginStart(0);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

    }

    protected void nav(boolean b, String name) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        toolbar.setTitleMarginStart(0);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_home:
                if (Objects.equals(getClass(), TagActivity.class)) {
                    this.drawer.closeDrawers();
                } else {
                    intent = new Intent(this, TagActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
                break;
            case R.id.nav_profile:
                if (Objects.equals(getClass(), ProfileTagActivity.class)) {
                    drawer.closeDrawers();
                } else {
                    intent = new Intent(this, ProfileTagActivity.class);
                    startActivity(intent);
                }
                AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
                break;
//            case R.id.nav_search:
//                //TODO
//                break;
            case R.id.nav_scan:
                if (mNfcAdapter != null)
                    if (!mNfcAdapter.isEnabled()) {
                        Toast.makeText(getApplicationContext(),
                                R.string.nfc_activate,
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent("android.settings.NFC_SETTINGS"));
                    } else {
                        intent = new Intent(this, NFCActivity.class);
                        intent.putExtra(this.getString(R.string.activity_key), "TagActivity");
                        startActivity(intent);
                    }
                else
                    Toast.makeText(getApplicationContext(),
                            R.string.nfc_not_support,
                            Toast.LENGTH_LONG).show();
                AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
                break;
            case R.id.nav_history:
                if (Objects.equals(getClass(), TagActivity.class)) {
                    this.drawer.closeDrawers();
                } else {
                    intent = new Intent(this, TagActivity.class);
                    intent.putExtra(getString(R.string.history), getString(R.string.history));
                    startActivity(intent);
                }
                break;
            case R.id.nav_friend_list:
                if (Objects.equals(getClass(), FriendListActivity.class)) {
                    this.drawer.closeDrawers();
                } else {
                    intent = new Intent(this, FriendListActivity.class);
                    startActivity(intent);
                }
                AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
                break;
            case R.id.nav_groups:
                if (Objects.equals(getClass(), GroupsActivity.class)) {
                    this.drawer.closeDrawers();
                } else {
                    intent = new Intent(this, GroupsActivity.class);
                    startActivity(intent);
                }
                AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
                break;
            case R.id.nav_requests:
                if (Objects.equals(getClass(), RequestsActivity.class)) {
                    this.drawer.closeDrawers();
                } else {
                    intent = new Intent(this, RequestsActivity.class);
                    startActivity(intent);
                }
                AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
                break;
            case R.id.nav_requests_friends:
                intent = new Intent(this, RequestFriendActivity.class);
                startActivity(intent);
                AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
                break;
            case R.id.nav_share:
                intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
//                        .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);
                break;
            case R.id.nav_logout:

                FacebookSdk.sdkInitialize(context);
                LoginManager.getInstance().logOut();
                new Delete().from(Friend.class).execute();
                new Delete().from(Message.class).execute();
                new Delete().from(Group.class).execute();
                new Delete().from(Reply.class).execute();
                new Delete().from(SearchUser.class).execute();
                new Delete().from(Tag.class).execute();
                OBSession.deletePreference(getApplicationContext());
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                AppLog.setVibrate(this, AppLog.INTENSITY_HIGH);
                finish();
                break;
            case R.id.nav_setting:
                if (Objects.equals(getClass(), ProfileSettingActivity.class)) {
                    this.drawer.closeDrawers();
                } else {
                    intent = new Intent(this, ProfileSettingActivity.class);
                    startActivity(intent);
                }
                AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
                break;
            case R.id.nav_help:
                intent = new Intent(this, IntroActivity.class);
                startActivity(intent);
                AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.e("TAG", "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }
}
