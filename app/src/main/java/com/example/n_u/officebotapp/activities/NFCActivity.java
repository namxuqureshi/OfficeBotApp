package com.example.n_u.officebotapp.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.n_u.officebotapp.R;
import com.example.n_u.officebotapp.helpers.Progress;
import com.example.n_u.officebotapp.intefaces.IStatus;
import com.example.n_u.officebotapp.models.Status;
import com.example.n_u.officebotapp.utils.AppLog;
import com.example.n_u.officebotapp.utils.InternetConnection;
import com.example.n_u.officebotapp.utils.OBSession;
import com.example.n_u.officebotapp.utils.OfficeBotURI;
import com.example.n_u.officebotapp.utils.StringConverter;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v7.app.AlertDialog.Builder;

public class NFCActivity
        extends Activity {
    public static final float FLOAT_ROTATE = 20.0F;
    public static final String[][] STRINGS = new String[0][];
    private final HashMap<String, Object> body = new HashMap<>();
    private final IStatus request = (IStatus) OfficeBotURI.retrofit().create(IStatus.class);
    private Context context = this;
    private boolean flag = true;
    private CircleImageView imgInner = null;
    private CircleImageView imgOuter = null;
    private NfcAdapter mNfcAdapter = null;
    private ViewPropertyAnimator tmpInner = null;
    private ViewPropertyAnimator tmpOuter = null;

    public static void setupForegroundDispatch(Activity activity, NfcAdapter nfcAdapter) {
        Object localObject = new Intent(activity.getApplicationContext(), activity.getClass());
        ((Intent) localObject).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        localObject = PendingIntent.getActivity(activity.getApplicationContext(), 0, (Intent) localObject, 0);
        IntentFilter[] arrayOfIntentFilter = new IntentFilter[3];
        arrayOfIntentFilter[0] = new IntentFilter();
        arrayOfIntentFilter[0].addAction("android.nfc.action.NDEF_DISCOVERED");
        arrayOfIntentFilter[0].addCategory("android.intent.category.SELECTED_ALTERNATIVE");
        arrayOfIntentFilter[1] = new IntentFilter();
        arrayOfIntentFilter[1].addAction("android.nfc.action.TAG_DISCOVERED");
        arrayOfIntentFilter[1].addCategory("android.intent.category.SELECTED_ALTERNATIVE");
        arrayOfIntentFilter[2] = new IntentFilter();
        arrayOfIntentFilter[2].addAction("android.nfc.action.TECH_DISCOVERED");
        arrayOfIntentFilter[2].addCategory("android.intent.category.SELECTED_ALTERNATIVE");
        nfcAdapter.enableForegroundDispatch(activity, (PendingIntent) localObject, arrayOfIntentFilter, STRINGS);
    }

    public static void stopForegroundDispatch(Activity activity, NfcAdapter nfcAdapter) {
        nfcAdapter.disableForegroundDispatch(activity);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        if (this.mNfcAdapter == null) {
//            Toast.makeText(this,
//                    R.string.nfc_not_support,
//                    Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//        if (!this.mNfcAdapter.isEnabled()) {
//            Toast.makeText(getApplicationContext(),
//                    R.string.nfc_activate,
//                    Toast.LENGTH_SHORT).show();
//            startActivity(new Intent("android.settings.NFC_SETTINGS"));
//        }
        setContentView(R.layout.activity_nfc);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        imgOuter = ((CircleImageView) findViewById(R.id.animate));
        imgInner = ((CircleImageView) findViewById(R.id.animate2));
        animate();
        ((TextView) findViewById(R.id.text_title_in_bar)).setText(getString(R.string.nfc_activity));
        handleIntent(getIntent());
        findViewById(R.id.backBtnImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void onStart() {
        this.flag = true;
        super.onStart();
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    protected void onResume() {
        setupForegroundDispatch(this, this.mNfcAdapter);
        super.onResume();
    }

    protected void onPause() {
        stopForegroundDispatch(this, this.mNfcAdapter);
        super.onPause();
    }

    private void animate() {
        this.tmpOuter = this.imgOuter.animate();
        this.tmpInner = this.imgInner.animate();
        final Thread localThread = new Thread(new Runnable() {
            float i = FLOAT_ROTATE;
            float i2 = -FLOAT_ROTATE;

            public void run() {
                while (flag) {
                    tmpOuter.rotation(this.i);
                    tmpInner.rotation(this.i2);
                    try {
                        this.i += FLOAT_ROTATE;
                        this.i2 -= FLOAT_ROTATE;
                        Thread.sleep(500L);
                        AppLog.setVibrate(context, AppLog.INTENSITY_LOW);
                    } catch (InterruptedException localInterruptedException) {
                        AppLog.logString(localInterruptedException.getMessage());
                    }
                }
            }
        });
        if (this.mNfcAdapter.isEnabled()) {
            this.tmpOuter.start();
            localThread.start();
        }
    }

    private void handleIntent(final Intent paramIntent) {
        Object action = paramIntent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            AppLog.setVibrate(this, AppLog.INTENSITY_MIDDLE);
            final String ssn = StringConverter.
                    bytesToHex(((Tag) paramIntent.
                            getParcelableExtra(NfcAdapter.EXTRA_TAG)).
                            getId());
            body.put(getString(R.string.ssn_key), ssn);
            body.put(getString(R.string.user_id_key), OBSession.getPreference(getString(R.string.id_key), this.context));
            retrofit2.Call<Status> call = request.verifyTag(this.body);
            Progress.Show(context, getString(R.string.scan) + "ing..");
            flag = false;
            if (InternetConnection.checkConnection(this)) {
                call.enqueue(new Callback<Status>() {
                    public void onFailure(Call<Status> call,
                                          Throwable t) {
                        AppLog.toastShortString("Net Not Available" + t.getMessage()
                                , getApplicationContext());
                        Progress.Cancel();
                        finish();
                    }

                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    public void onResponse(Call<Status> call,
                                           final Response<Status> response) {
                        Log.e("TAG", "onResponse: " + response.body());
                        Intent i;
                        AppLog.setVibrate(context, AppLog.INTENSITY_MIDDLE);
                        if (response.code() == 200) {
                            if (!response.body().isExist()) {
                                i = new Intent(context, AddNewTagActivity.class);
                                i.putExtra(getString(R.string.tag_id_key), ssn);
                                startActivity(i);
                                Progress.Cancel();
                                finish();
                            } else if ((response.body().isExist()) && (response.body().isOwner())) {
                                Progress.Cancel();
                                i = new Intent(context, TagMainActivity.class);
                                i.putExtra(getString(R.string.tag_id_key), response.body().getTagId());
                                startActivity(i);
                                finish();
                            } else if (response.body().getPermission() != null) {
                                if (Objects.equals(response.body().getPermission(), "n")) {
                                    Progress.Cancel();
                                    if (response.body().getUserId() > 1) {
                                        Builder builder = new Builder(context);
                                        builder.setMessage(R.string.send_reqeu);
                                        builder.setPositiveButton(R.string.send, new OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> localHashMap = new HashMap<>();
                                                IStatus localIStatus = OfficeBotURI.retrofit().create(IStatus.class);
                                                localHashMap.put(getString(R.string.user_id_key), OBSession.getPreference(getString(R.string.user_id_key), context));
                                                localHashMap.put(getString(R.string.user2_id_key), response.body().getUserId());
                                                localIStatus.getSendRequestStatus(localHashMap).enqueue(new Callback<Status>() {
                                                    public void onFailure(Call<Status> call,
                                                                          Throwable throwable) {
                                                        Toast.makeText(context,
                                                                throwable.getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }

                                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                                        if (response.code() == 200) {
                                                            if (response.body().isRequest()) {
                                                                Toast.makeText(context, "Request Send Successfully", Toast.LENGTH_LONG).show();

                                                            } else
                                                                Toast.makeText(context, "Request Send UnSuccessfully", Toast.LENGTH_SHORT).show();

                                                        } else
                                                            Toast.makeText(context, "Server Offline" + response.code(), Toast.LENGTH_LONG).show();

                                                        finish();
                                                    }
                                                });
                                                dialog.cancel();
                                                AppLog.setVibrate(context, AppLog.INTENSITY_MIDDLE);
                                                Toast.makeText(context, R.string.request_send, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        builder.setNegativeButton(getString(R.string.no), new OnClickListener() {
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                dialogInterface.cancel();
                                                AppLog.setVibrate(context, AppLog.INTENSITY_MIDDLE);
                                                finish();
                                            }
                                        });
                                        builder.create().show();
                                    }
                                    Toast.makeText(context,
                                            R.string.private_tag,
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Progress.Cancel();
                                    i = new Intent(context, ScanTagActivity.class);
                                    i.putExtra(getString(R.string.tag_id_key), response.body().getTagId());
                                    i.putExtra(getString(R.string.permission_key), response.body().getPermission());
                                    startActivity(i);
                                    finish();
                                }
                            } else {
                                Progress.Cancel();
                                i = new Intent(context, ScanTagActivity.class);
                                i.putExtra(getString(R.string.tag_id_key), response.body().getTagId());
                                i.putExtra(getString(R.string.permission_key), response.body().getPermission());
                                startActivity(i);
                                finish();

                            }
                        } else {
                            Toast.makeText(context,
                                    "Server Offline" + response.code(),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            } else {
                AppLog.toastShortString(AppLog.NET_NOT_AVAILABLE
                        , getApplicationContext());
                finish();
            }
        }
    }

    public void goBack(View paramView) {
        this.flag = false;
        super.onBackPressed();
    }

    public void onBackPressed() {
        this.flag = false;
        super.onBackPressed();
    }

    protected void onNewIntent(Intent paramIntent) {
        handleIntent(paramIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();
    }
}



/* Location:           C:\Users\N_U\Desktop\Tolo\ob\classes30-dex2jar.jar

 * Qualified Name:     com.example.n_u.officebot.activities.NFCActivity

 * JD-Core Version:    0.7.0.1

 */